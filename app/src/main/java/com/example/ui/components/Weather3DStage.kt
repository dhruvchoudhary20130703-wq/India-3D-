package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.BiomeType
import com.example.data.model.WeatherCondition
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

private data class Vec3(val x: Float, val y: Float, val z: Float)

private data class Projected3D(
    val x: Float,
    val y: Float,
    val zDepth: Float,
    val scale: Float,
)

private data class RenderSphere3D(
    val proj: Projected3D,
    val radius: Float,
    val baseColor: Color,
    val highlightColor: Color,
    val shadowColor: Color,
    val glowRadius: Float = 0f,
    val glowColor: Color = Color.Transparent,
)

private data class ParticleSeed(
    val x: Float,
    val yOffset: Float,
    val z: Float,
    val speed: Float,
    val size: Float,
    val phase: Float,
)

private fun rotateAndProject(
    v: Vec3,
    yawRad: Float,
    pitchRad: Float,
    center: Offset,
    focalLength: Float,
    unitScale: Float,
): Projected3D {
    // Rotate around Y (yaw)
    val cosY = cos(yawRad)
    val sinY = sin(yawRad)
    val x1 = v.x * cosY - v.z * sinY
    val z1 = v.x * sinY + v.z * cosY

    // Rotate around X (pitch)
    val cosP = cos(pitchRad)
    val sinP = sin(pitchRad)
    val y2 = v.y * cosP - z1 * sinP
    val z2 = v.y * sinP + z1 * cosP

    val camZ = focalLength + z2
    val perspective = focalLength / camZ.coerceAtLeast(120f)
    return Projected3D(
        x = center.x + x1 * unitScale * perspective,
        y = center.y + y2 * unitScale * perspective,
        zDepth = z2,
        scale = perspective * unitScale,
    )
}

@Composable
fun Weather3DStage(
    condition: WeatherCondition,
    biome: BiomeType,
    isDay: Boolean,
    windSpeedKmh: Double,
    cityName: String,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val yawAnim = remember { Animatable(-0.28f) }
    val pitchAnim = remember { Animatable(0.32f) }

    val infiniteTransition = rememberInfiniteTransition(label = "weather3d_loop")
    val loopPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
        ),
        label = "loopPhase",
    )
    val fastPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
        ),
        label = "fastPhase",
    )

    val rainSeeds = remember {
        val rng = Random(42)
        List(72) {
            ParticleSeed(
                x = rng.nextFloat() * 2.2f - 1.1f,
                yOffset = rng.nextFloat(),
                z = rng.nextFloat() * 2.0f - 1.0f,
                speed = 0.85f + rng.nextFloat() * 0.6f,
                size = 0.7f + rng.nextFloat() * 0.7f,
                phase = rng.nextFloat(),
            )
        }
    }

    val starSeeds = remember {
        val rng = Random(99)
        List(44) {
            ParticleSeed(
                x = rng.nextFloat() * 3.0f - 1.5f,
                yOffset = rng.nextFloat() * 1.4f - 1.25f,
                z = rng.nextFloat() * 2.4f - 1.2f,
                speed = 0.5f + rng.nextFloat() * 1.2f,
                size = 1.4f + rng.nextFloat() * 2.2f,
                phase = rng.nextFloat() * 2f * PI.toFloat(),
            )
        }
    }

    val dustSeeds = remember {
        val rng = Random(17)
        List(32) {
            ParticleSeed(
                x = rng.nextFloat() * 2.4f - 1.2f,
                yOffset = rng.nextFloat() * 0.9f - 0.35f,
                z = rng.nextFloat() * 2.0f - 1.0f,
                speed = 0.4f + rng.nextFloat() * 0.7f,
                size = 1.5f + rng.nextFloat() * 2.0f,
                phase = rng.nextFloat() * 2f * PI.toFloat(),
            )
        }
    }

    val stageDescription = "$cityName 3D ${condition.displayName} diorama"

    Box(
        modifier = modifier
            .semantics { contentDescription = stageDescription }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scope.launch {
                            launch {
                                yawAnim.animateTo(-0.28f, spring(stiffness = Spring.StiffnessMediumLow))
                            }
                            launch {
                                pitchAnim.animateTo(0.32f, spring(stiffness = Spring.StiffnessMediumLow))
                            }
                        }
                    },
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            launch {
                                yawAnim.animateTo(
                                    yawAnim.value.coerceIn(-0.85f, 0.85f),
                                    spring(stiffness = Spring.StiffnessLow),
                                )
                            }
                            launch {
                                pitchAnim.animateTo(
                                    pitchAnim.value.coerceIn(0.08f, 0.75f),
                                    spring(stiffness = Spring.StiffnessLow),
                                )
                            }
                        }
                    },
                ) { change, dragAmount ->
                    change.consume()
                    scope.launch {
                        yawAnim.snapTo((yawAnim.value + dragAmount.x * 0.0055f).coerceIn(-1.25f, 1.25f))
                        pitchAnim.snapTo((pitchAnim.value + dragAmount.y * 0.0045f).coerceIn(-0.05f, 0.92f))
                    }
                }
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w * 0.5f, h * 0.54f)
            val unitScale = minOf(w, h) * 0.34f
            val focalLength = 420f

            val idleYaw = sin(loopPhase * 2f * PI.toFloat()) * 0.08f
            val yaw = yawAnim.value + idleYaw
            val pitch = pitchAnim.value

            // 1. Night Stars in deep 3D dome
            if (!isDay) {
                starSeeds.forEach { star ->
                    val proj = rotateAndProject(
                        v = Vec3(star.x, star.yOffset, star.z),
                        yawRad = yaw * 0.45f,
                        pitchRad = pitch * 0.35f,
                        center = center,
                        focalLength = focalLength,
                        unitScale = unitScale,
                    )
                    val twinkle = 0.35f + 0.65f * ((sin(loopPhase * 4f * PI.toFloat() + star.phase) + 1f) * 0.5f)
                    drawCircle(
                        color = Color.White.copy(alpha = twinkle * 0.85f),
                        radius = star.size * proj.scale / unitScale,
                        center = Offset(proj.x, proj.y),
                    )
                }
            }

            // 2. Subtle 3D orbital atmospheric rings behind diorama
            drawAtmosphericRing3D(
                radius3d = 1.48f,
                yOffset = 0.35f,
                yawRad = yaw + loopPhase * 0.4f,
                pitchRad = pitch,
                center = center,
                focalLength = focalLength,
                unitScale = unitScale,
                color = if (isDay) Color.White.copy(alpha = 0.14f) else Color(0xFF7DD3FC).copy(alpha = 0.14f),
            )

            // 3. Extruded 3D Isometric Biome Terrain Slab
            drawBiomeTerrain3D(
                biome = biome,
                condition = condition,
                isDay = isDay,
                loopPhase = loopPhase,
                yawRad = yaw,
                pitchRad = pitch,
                center = center,
                focalLength = focalLength,
                unitScale = unitScale,
            )

            // 4. Wind 3D Ribbons sweeping over the terrain
            val windAlpha = ((windSpeedKmh.toFloat() / 35f).coerceIn(0.18f, 0.72f))
            drawWindRibbons3D(
                loopPhase = loopPhase,
                fastPhase = fastPhase,
                yawRad = yaw,
                pitchRad = pitch,
                center = center,
                focalLength = focalLength,
                unitScale = unitScale,
                alpha = windAlpha,
            )

            // 5. Collect 3D Volumetric Spheres (Sun/Moon + Multi-Cloud Clusters) for Z-depth sorting
            val spheres = mutableListOf<RenderSphere3D>()

            val showSunOrMoon = condition in listOf(
                WeatherCondition.CLEAR,
                WeatherCondition.PARTLY_CLOUDY,
                WeatherCondition.FOG_HAZE,
            )

            if (showSunOrMoon) {
                val sunOffsetX = if (condition == WeatherCondition.PARTLY_CLOUDY) 0.46f else 0.0f
                val sunOffsetY = -0.78f + sin(loopPhase * 2f * PI.toFloat()) * 0.04f
                val sunOffsetZ = if (condition == WeatherCondition.PARTLY_CLOUDY) -0.25f else 0.0f
                val proj = rotateAndProject(
                    v = Vec3(sunOffsetX, sunOffsetY, sunOffsetZ),
                    yawRad = yaw,
                    pitchRad = pitch,
                    center = center,
                    focalLength = focalLength,
                    unitScale = unitScale,
                )
                if (isDay) {
                    spheres.add(
                        RenderSphere3D(
                            proj = proj,
                            radius = 0.40f * proj.scale,
                            baseColor = Color(0xFFFFA726),
                            highlightColor = Color(0xFFFFF59D),
                            shadowColor = Color(0xFFE65100),
                            glowRadius = 0.78f * proj.scale,
                            glowColor = Color(0xFFFFB74D).copy(alpha = 0.42f),
                        ),
                    )
                    // 3D Solar Corona Rings
                    drawSolarCorona3D(
                        origin = Vec3(sunOffsetX, sunOffsetY, sunOffsetZ),
                        loopPhase = loopPhase,
                        yawRad = yaw,
                        pitchRad = pitch,
                        center = center,
                        focalLength = focalLength,
                        unitScale = unitScale,
                    )
                } else {
                    spheres.add(
                        RenderSphere3D(
                            proj = proj,
                            radius = 0.34f * proj.scale,
                            baseColor = Color(0xFFE2E8F0),
                            highlightColor = Color(0xFFFFFFFF),
                            shadowColor = Color(0xFF64748B),
                            glowRadius = 0.65f * proj.scale,
                            glowColor = Color(0xFF93C5FD).copy(alpha = 0.32f),
                        ),
                    )
                }
            }

            // Volumetric 3D Cloud clusters when cloudy / rainy / storm / snow / partly cloudy
            if (condition != WeatherCondition.CLEAR) {
                val isStorm = condition in listOf(
                    WeatherCondition.THUNDERSTORM,
                    WeatherCondition.HEAVY_RAIN,
                    WeatherCondition.RAIN,
                )
                val cloudBase = when {
                    isStorm -> Color(0xFF64748B)
                    condition == WeatherCondition.CLOUDY -> Color(0xFFCBD5E1)
                    condition == WeatherCondition.FOG_HAZE -> Color(0xFFE2E8F0).copy(alpha = 0.78f)
                    else -> Color(0xFFF8FAFC)
                }
                val cloudHighlight = when {
                    isStorm -> Color(0xFF94A3B8)
                    !isDay -> Color(0xFFCBD5E1)
                    else -> Color(0xFFFFFFFF)
                }
                val cloudShadow = when {
                    isStorm -> Color(0xFF334155)
                    !isDay -> Color(0xFF475569)
                    else -> Color(0xFF94A3B8)
                }

                val drift = sin(loopPhase * 2f * PI.toFloat()) * 0.08f
                val cloudNodes = listOf(
                    Triple(Vec3(-0.42f + drift, -0.52f, 0.12f), 0.25f, 1),
                    Triple(Vec3(-0.14f + drift, -0.62f, 0.20f), 0.31f, 1),
                    Triple(Vec3(0.20f + drift, -0.56f, 0.14f), 0.27f, 1),
                    Triple(Vec3(0.45f + drift, -0.48f, 0.06f), 0.20f, 1),
                    Triple(Vec3(-0.22f + drift, -0.45f, -0.10f), 0.23f, 1),
                    Triple(Vec3(0.10f + drift, -0.46f, -0.12f), 0.24f, 1),
                )

                val activeCount = if (condition == WeatherCondition.PARTLY_CLOUDY) 4 else cloudNodes.size
                cloudNodes.take(activeCount).forEach { (vec, r, _) ->
                    val proj = rotateAndProject(
                        v = vec,
                        yawRad = yaw,
                        pitchRad = pitch,
                        center = center,
                        focalLength = focalLength,
                        unitScale = unitScale,
                    )
                    spheres.add(
                        RenderSphere3D(
                            proj = proj,
                            radius = r * proj.scale,
                            baseColor = cloudBase,
                            highlightColor = cloudHighlight,
                            shadowColor = cloudShadow,
                        ),
                    )
                }
            }

            // Depth-sort spheres far-to-near (larger zDepth is farther away)
            spheres.sortByDescending { it.proj.zDepth }
            spheres.forEach { s ->
                val c = Offset(s.proj.x, s.proj.y)
                if (s.glowRadius > 0f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(s.glowColor, Color.Transparent),
                            center = c,
                            radius = s.glowRadius,
                        ),
                        radius = s.glowRadius,
                        center = c,
                    )
                }
                val lightCenter = Offset(c.x - s.radius * 0.34f, c.y - s.radius * 0.34f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(s.highlightColor, s.baseColor, s.shadowColor),
                        center = lightCenter,
                        radius = s.radius * 1.45f,
                    ),
                    radius = s.radius,
                    center = c,
                )
            }

            // 6. Desert Heat Shimmer / Atmospheric Motes (Thar Desert Clear/Haze)
            if (biome == BiomeType.THAR_DESERT && (condition == WeatherCondition.CLEAR || condition == WeatherCondition.FOG_HAZE)) {
                dustSeeds.forEach { mote ->
                    val xShift = ((mote.x + loopPhase * mote.speed * 1.4f + 1.2f) % 2.4f) - 1.2f
                    val yBob = mote.yOffset + sin(loopPhase * 4f * PI.toFloat() + mote.phase) * 0.05f
                    val proj = rotateAndProject(
                        v = Vec3(xShift, yBob, mote.z),
                        yawRad = yaw,
                        pitchRad = pitch,
                        center = center,
                        focalLength = focalLength,
                        unitScale = unitScale,
                    )
                    drawCircle(
                        color = Color(0xFFFCD34D).copy(alpha = 0.45f),
                        radius = mote.size * proj.scale / unitScale,
                        center = Offset(proj.x, proj.y),
                    )
                }
            }

            // 7. 3D Rain / Thunderstorm / Snow Precipitation
            when (condition) {
                WeatherCondition.DRIZZLE,
                WeatherCondition.RAIN,
                WeatherCondition.HEAVY_RAIN,
                WeatherCondition.THUNDERSTORM -> {
                    val dropCount = when (condition) {
                        WeatherCondition.DRIZZLE -> 28
                        WeatherCondition.RAIN -> 50
                        else -> rainSeeds.size
                    }
                    rainSeeds.take(dropCount).forEach { seed ->
                        val t = (fastPhase * seed.speed + seed.yOffset) % 1f
                        val yTop = -0.36f + t * 0.88f
                        val xDrift = seed.x * 0.72f - t * 0.14f
                        val zPos = seed.z * 0.72f
                        val p1 = rotateAndProject(Vec3(xDrift, yTop, zPos), yaw, pitch, center, focalLength, unitScale)
                        val p2 = rotateAndProject(Vec3(xDrift - 0.03f, yTop + 0.14f, zPos), yaw, pitch, center, focalLength, unitScale)
                        drawLine(
                            color = Color(0xFF7DD3FC).copy(alpha = (1f - t * 0.35f) * 0.85f),
                            start = Offset(p1.x, p1.y),
                            end = Offset(p2.x, p2.y),
                            strokeWidth = (2.1f * seed.size * p1.scale / unitScale).coerceIn(1.2f, 4.2f),
                            cap = StrokeCap.Round,
                        )
                        // Surface splash ripple when hitting terrain slab
                        if (t > 0.86f) {
                            val splashProgress = (t - 0.86f) / 0.14f
                            val splashPos = rotateAndProject(Vec3(xDrift, 0.50f, zPos), yaw, pitch, center, focalLength, unitScale)
                            drawOval(
                                color = Color(0xFFBAE6FD).copy(alpha = (1f - splashProgress) * 0.55f),
                                topLeft = Offset(splashPos.x - 10f * splashProgress, splashPos.y - 4f * splashProgress),
                                size = androidx.compose.ui.geometry.Size(20f * splashProgress, 8f * splashProgress),
                                style = Stroke(width = 1.4f),
                            )
                        }
                    }

                    // 3D Lightning Bolt in Thunderstorm
                    if (condition == WeatherCondition.THUNDERSTORM) {
                        val flashCycle = (fastPhase * 3f) % 1f
                        if (flashCycle < 0.24f) {
                            val boltAlpha = 1f - (flashCycle / 0.24f)
                            val boltPts = listOf(
                                Vec3(0.05f, -0.35f, 0.10f),
                                Vec3(-0.08f, -0.10f, 0.14f),
                                Vec3(0.06f, 0.05f, 0.08f),
                                Vec3(-0.04f, 0.35f, 0.04f),
                            ).map { rotateAndProject(it, yaw, pitch, center, focalLength, unitScale) }

                            val boltPath = Path().apply {
                                moveTo(boltPts.first().x, boltPts.first().y)
                                boltPts.drop(1).forEach { lineTo(it.x, it.y) }
                            }
                            drawPath(
                                path = boltPath,
                                color = Color(0xFFFEF08A).copy(alpha = boltAlpha),
                                style = Stroke(width = 4.5f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                            )
                            drawPath(
                                path = boltPath,
                                color = Color(0xFF38BDF8).copy(alpha = boltAlpha * 0.45f),
                                style = Stroke(width = 10f, cap = StrokeCap.Round, join = StrokeJoin.Round),
                            )
                        }
                    }
                }

                WeatherCondition.SNOW -> {
                    rainSeeds.take(48).forEach { seed ->
                        val t = (loopPhase * 1.8f * seed.speed + seed.yOffset) % 1f
                        val yPos = -0.38f + t * 0.88f
                        val sway = sin(loopPhase * 6f * PI.toFloat() + seed.phase) * 0.08f
                        val p = rotateAndProject(Vec3(seed.x * 0.72f + sway, yPos, seed.z * 0.72f), yaw, pitch, center, focalLength, unitScale)
                        drawCircle(
                            color = Color.White.copy(alpha = 0.88f),
                            radius = (3.2f * seed.size * p.scale / unitScale).coerceIn(1.5f, 5.5f),
                            center = Offset(p.x, p.y),
                        )
                    }
                }

                else -> Unit
            }
        }

        // Subtle interactive hint pill at bottom-right of 3D stage
        Surface(
            color = Color.Black.copy(alpha = 0.28f),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
        ) {
            Text(
                text = stringResource(R.string.drag_to_orbit_hint),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.82f),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            )
        }
    }
}

private fun DrawScope.drawAtmosphericRing3D(
    radius3d: Float,
    yOffset: Float,
    yawRad: Float,
    pitchRad: Float,
    center: Offset,
    focalLength: Float,
    unitScale: Float,
    color: Color,
) {
    val steps = 40
    val path = Path()
    for (i in 0..steps) {
        val angle = (i.toFloat() / steps) * 2f * PI.toFloat()
        val p = rotateAndProject(
            v = Vec3(cos(angle) * radius3d, yOffset, sin(angle) * radius3d),
            yawRad = yawRad,
            pitchRad = pitchRad,
            center = center,
            focalLength = focalLength,
            unitScale = unitScale,
        )
        if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
    }
    drawPath(path = path, color = color, style = Stroke(width = 1.6f))
}

private fun DrawScope.drawSolarCorona3D(
    origin: Vec3,
    loopPhase: Float,
    yawRad: Float,
    pitchRad: Float,
    center: Offset,
    focalLength: Float,
    unitScale: Float,
) {
    val ringAngle = loopPhase * 2f * PI.toFloat()
    val steps = 32
    val path = Path()
    for (i in 0..steps) {
        val a = (i.toFloat() / steps) * 2f * PI.toFloat()
        val rx = cos(a) * 0.56f
        val ry = sin(a) * 0.18f * cos(ringAngle) - sin(a) * 0.25f * sin(ringAngle)
        val rz = sin(a) * 0.56f
        val p = rotateAndProject(
            v = Vec3(origin.x + rx, origin.y + ry, origin.z + rz),
            yawRad = yawRad,
            pitchRad = pitchRad,
            center = center,
            focalLength = focalLength,
            unitScale = unitScale,
        )
        if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
    }
    drawPath(
        path = path,
        color = Color(0xFFFDE68A).copy(alpha = 0.52f),
        style = Stroke(width = 2.2f),
    )
}

private fun DrawScope.drawWindRibbons3D(
    loopPhase: Float,
    fastPhase: Float,
    yawRad: Float,
    pitchRad: Float,
    center: Offset,
    focalLength: Float,
    unitScale: Float,
    alpha: Float,
) {
    val heights = listOf(-0.18f, 0.08f, 0.26f)
    heights.forEachIndexed { idx, yBase ->
        val path = Path()
        val zBase = -0.35f + idx * 0.35f
        val shift = (fastPhase + idx * 0.33f) % 1f
        val startX = -1.25f + shift * 0.9f
        for (s in 0..14) {
            val t = s / 14f
            val x = startX + t * 1.15f
            val y = yBase + sin(t * PI.toFloat() * 2f + loopPhase * 4f) * 0.07f
            val z = zBase + cos(t * PI.toFloat()) * 0.14f
            val p = rotateAndProject(Vec3(x, y, z), yawRad, pitchRad, center, focalLength, unitScale)
            if (s == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
        }
        drawPath(
            path = path,
            color = Color.White.copy(alpha = alpha * 0.45f),
            style = Stroke(width = 2.4f, cap = StrokeCap.Round),
        )
    }
}

private fun DrawScope.drawBiomeTerrain3D(
    biome: BiomeType,
    condition: WeatherCondition,
    isDay: Boolean,
    loopPhase: Float,
    yawRad: Float,
    pitchRad: Float,
    center: Offset,
    focalLength: Float,
    unitScale: Float,
) {
    val topColor1: Color
    val topColor2: Color
    val sideLeftColor: Color
    val sideRightColor: Color

    when (biome) {
        BiomeType.THAR_DESERT -> {
            topColor1 = if (isDay) Color(0xFFF59E0B) else Color(0xFF92400E)
            topColor2 = if (isDay) Color(0xFFD97706) else Color(0xFF451A03)
            sideLeftColor = Color(0xFF78350F)
            sideRightColor = Color(0xFF451A03)
        }
        BiomeType.COASTAL, BiomeType.ISLANDS -> {
            topColor1 = if (isDay) Color(0xFF0EA5E9) else Color(0xFF0369A1)
            topColor2 = if (isDay) Color(0xFF0284C7) else Color(0xFF0C4A6E)
            sideLeftColor = Color(0xFF075985)
            sideRightColor = Color(0xFF082F49)
        }
        BiomeType.HIMALAYAN -> {
            topColor1 = if (isDay) Color(0xFF94A3B8) else Color(0xFF475569)
            topColor2 = if (isDay) Color(0xFF64748B) else Color(0xFF334155)
            sideLeftColor = Color(0xFF334155)
            sideRightColor = Color(0xFF1E293B)
        }
        else -> {
            topColor1 = if (isDay) Color(0xFF10B981) else Color(0xFF065F46)
            topColor2 = if (isDay) Color(0xFF059669) else Color(0xFF064E3B)
            sideLeftColor = Color(0xFF065F46)
            sideRightColor = Color(0xFF022C22)
        }
    }

    val yTop = 0.52f
    val yBottom = 0.72f
    val r = 0.96f

    // 4 corners of extruded 3D isometric slab
    val cornersTop = listOf(
        Vec3(-r, yTop, -r),
        Vec3(r, yTop, -r),
        Vec3(r, yTop, r),
        Vec3(-r, yTop, r),
    )
    val cornersBot = listOf(
        Vec3(-r, yBottom, -r),
        Vec3(r, yBottom, -r),
        Vec3(r, yBottom, r),
        Vec3(-r, yBottom, r),
    )

    val pTop = cornersTop.map { rotateAndProject(it, yawRad, pitchRad, center, focalLength, unitScale) }
    val pBot = cornersBot.map { rotateAndProject(it, yawRad, pitchRad, center, focalLength, unitScale) }

    // Draw 4 side faces sorted by average Z depth so rotating 360/left-right never glitches!
    data class QuadFace(val idx1: Int, val idx2: Int, val avgZ: Float, val color: Color)
    val faces = listOf(
        QuadFace(0, 1, (pTop[0].zDepth + pTop[1].zDepth) * 0.5f, sideLeftColor),
        QuadFace(1, 2, (pTop[1].zDepth + pTop[2].zDepth) * 0.5f, sideRightColor),
        QuadFace(2, 3, (pTop[2].zDepth + pTop[3].zDepth) * 0.5f, sideLeftColor),
        QuadFace(3, 0, (pTop[3].zDepth + pTop[0].zDepth) * 0.5f, sideRightColor),
    ).sortedByDescending { it.avgZ }

    faces.forEach { face ->
        val path = Path().apply {
            moveTo(pTop[face.idx1].x, pTop[face.idx1].y)
            lineTo(pTop[face.idx2].x, pTop[face.idx2].y)
            lineTo(pBot[face.idx2].x, pBot[face.idx2].y)
            lineTo(pBot[face.idx1].x, pBot[face.idx1].y)
            close()
        }
        drawPath(path = path, color = face.color)
    }

    // Top face
    val topPath = Path().apply {
        moveTo(pTop[0].x, pTop[0].y)
        lineTo(pTop[1].x, pTop[1].y)
        lineTo(pTop[2].x, pTop[2].y)
        lineTo(pTop[3].x, pTop[3].y)
        close()
    }
    drawPath(
        path = topPath,
        brush = Brush.linearGradient(
            colors = listOf(topColor1, topColor2),
            start = Offset(pTop[0].x, pTop[0].y),
            end = Offset(pTop[2].x, pTop[2].y),
        ),
    )
    drawPath(
        path = topPath,
        color = Color.White.copy(alpha = 0.28f),
        style = Stroke(width = 1.6f),
    )

    // Biome-specific 3D surface landmarks!
    when (biome) {
        BiomeType.THAR_DESERT -> {
            // Two sculpted 3D sand dune ridges on the slab
            val dunePeaks = listOf(
                listOf(Vec3(-0.68f, yTop, -0.2f), Vec3(-0.18f, yTop - 0.22f, -0.1f), Vec3(0.38f, yTop, 0.1f)),
                listOf(Vec3(-0.32f, yTop, 0.35f), Vec3(0.24f, yTop - 0.20f, 0.32f), Vec3(0.76f, yTop, 0.42f)),
            )
            dunePeaks.forEach { tri ->
                val pts = tri.map { rotateAndProject(it, yawRad, pitchRad, center, focalLength, unitScale) }
                val p = Path().apply {
                    moveTo(pts[0].x, pts[0].y)
                    lineTo(pts[1].x, pts[1].y)
                    lineTo(pts[2].x, pts[2].y)
                    close()
                }
                drawPath(p, color = if (isDay) Color(0xFFFBBF24) else Color(0xFFB45309))
            }
        }
        BiomeType.HIMALAYAN -> {
            // Three 3D snow-capped Himalayan pyramids
            val peaks = listOf(
                Vec3(-0.35f, yTop - 0.44f, -0.15f) to 0.42f,
                Vec3(0.25f, yTop - 0.36f, 0.12f) to 0.36f,
                Vec3(-0.02f, yTop - 0.28f, 0.40f) to 0.28f,
            )
            peaks.forEach { (apex, baseHalf) ->
                val base = listOf(
                    Vec3(apex.x - baseHalf, yTop, apex.z - baseHalf),
                    Vec3(apex.x + baseHalf, yTop, apex.z - baseHalf),
                    Vec3(apex.x + baseHalf, yTop, apex.z + baseHalf),
                    Vec3(apex.x - baseHalf, yTop, apex.z + baseHalf),
                )
                val pApex = rotateAndProject(apex, yawRad, pitchRad, center, focalLength, unitScale)
                val pBase = base.map { rotateAndProject(it, yawRad, pitchRad, center, focalLength, unitScale) }
                for (i in 0..3) {
                    val next = (i + 1) % 4
                    val triPath = Path().apply {
                        moveTo(pApex.x, pApex.y)
                        lineTo(pBase[i].x, pBase[i].y)
                        lineTo(pBase[next].x, pBase[next].y)
                        close()
                    }
                    drawPath(
                        triPath,
                        color = if (i % 2 == 0) Color(0xFFE2E8F0) else Color(0xFF94A3B8),
                    )
                }
            }
        }
        BiomeType.COASTAL, BiomeType.ISLANDS -> {
            // Animated 3D wave crests across the ocean slab
            for (row in -2..2) {
                val zLine = row * 0.28f
                val wavePath = Path()
                for (s in 0..10) {
                    val xLine = -0.78f + s * 0.156f
                    val yWave = yTop - 0.04f - sin(s * 0.9f + loopPhase * 2f * PI.toFloat() + row) * 0.03f
                    val p = rotateAndProject(Vec3(xLine, yWave, zLine), yawRad, pitchRad, center, focalLength, unitScale)
                    if (s == 0) wavePath.moveTo(p.x, p.y) else wavePath.lineTo(p.x, p.y)
                }
                drawPath(
                    wavePath,
                    color = Color(0xFFBAE6FD).copy(alpha = if (condition == WeatherCondition.SNOW) 0.8f else 0.55f),
                    style = Stroke(width = 2.0f, cap = StrokeCap.Round),
                )
            }
        }
        else -> {
            // Verdant 3D plateau hills
            val hills = listOf(
                Triple(Vec3(-0.32f, yTop - 0.16f, -0.12f), 0.34f, Color(0xFF34D399)),
                Triple(Vec3(0.28f, yTop - 0.12f, 0.22f), 0.28f, Color(0xFF10B981)),
            )
            hills.forEach { (apex, half, col) ->
                val pApex = rotateAndProject(apex, yawRad, pitchRad, center, focalLength, unitScale)
                val pLeft = rotateAndProject(Vec3(apex.x - half, yTop, apex.z + half * 0.5f), yawRad, pitchRad, center, focalLength, unitScale)
                val pRight = rotateAndProject(Vec3(apex.x + half, yTop, apex.z - half * 0.5f), yawRad, pitchRad, center, focalLength, unitScale)
                val hillPath = Path().apply {
                    moveTo(pLeft.x, pLeft.y)
                    quadraticTo(pApex.x, pApex.y - 14f, pRight.x, pRight.y)
                    close()
                }
                drawPath(hillPath, color = if (isDay) col else col.copy(alpha = 0.55f))
            }
        }
    }
}

@Composable
fun WeatherEmblem3D(
    condition: WeatherCondition,
    isDay: Boolean,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w * 0.5f
        val cy = h * 0.5f
        val r = minOf(w, h) * 0.32f

        val drawSun = condition in listOf(
            WeatherCondition.CLEAR,
            WeatherCondition.PARTLY_CLOUDY,
        ) && isDay

        val drawMoon = condition in listOf(
            WeatherCondition.CLEAR,
            WeatherCondition.PARTLY_CLOUDY,
        ) && !isDay

        if (drawSun) {
            val sunCenter = if (condition == WeatherCondition.PARTLY_CLOUDY) {
                Offset(cx + r * 0.45f, cy - r * 0.35f)
            } else {
                Offset(cx, cy)
            }
            val sunR = if (condition == WeatherCondition.PARTLY_CLOUDY) r * 0.68f else r * 0.95f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFB74D).copy(alpha = 0.45f), Color.Transparent),
                    center = sunCenter,
                    radius = sunR * 1.6f,
                ),
                radius = sunR * 1.6f,
                center = sunCenter,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFF59D), Color(0xFFFFA726), Color(0xFFE65100)),
                    center = Offset(sunCenter.x - sunR * 0.3f, sunCenter.y - sunR * 0.3f),
                    radius = sunR * 1.4f,
                ),
                radius = sunR,
                center = sunCenter,
            )
        }

        if (drawMoon) {
            val moonCenter = if (condition == WeatherCondition.PARTLY_CLOUDY) {
                Offset(cx + r * 0.45f, cy - r * 0.35f)
            } else {
                Offset(cx, cy)
            }
            val moonR = r * 0.8f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color(0xFFCBD5E1), Color(0xFF64748B)),
                    center = Offset(moonCenter.x - moonR * 0.3f, moonCenter.y - moonR * 0.3f),
                    radius = moonR * 1.4f,
                ),
                radius = moonR,
                center = moonCenter,
            )
        }

        if (condition != WeatherCondition.CLEAR) {
            val isStorm = condition in listOf(
                WeatherCondition.RAIN,
                WeatherCondition.HEAVY_RAIN,
                WeatherCondition.THUNDERSTORM,
            )
            val baseCol = if (isStorm) Color(0xFF94A3B8) else Color(0xFFF1F5F9)
            val shadowCol = if (isStorm) Color(0xFF475569) else Color(0xFF94A3B8)
            val puffs = listOf(
                Offset(cx - r * 0.45f, cy + r * 0.1f) to r * 0.55f,
                Offset(cx, cy - r * 0.15f) to r * 0.72f,
                Offset(cx + r * 0.48f, cy + r * 0.12f) to r * 0.52f,
            )
            puffs.forEach { (pCenter, pRad) ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, baseCol, shadowCol),
                        center = Offset(pCenter.x - pRad * 0.3f, pCenter.y - pRad * 0.35f),
                        radius = pRad * 1.45f,
                    ),
                    radius = pRad,
                    center = pCenter,
                )
            }
        }

        if (condition in listOf(
                WeatherCondition.DRIZZLE,
                WeatherCondition.RAIN,
                WeatherCondition.HEAVY_RAIN,
                WeatherCondition.THUNDERSTORM,
            )
        ) {
            listOf(-0.35f, 0f, 0.35f).forEach { dx ->
                drawLine(
                    color = Color(0xFF38BDF8),
                    start = Offset(cx + r * dx, cy + r * 0.55f),
                    end = Offset(cx + r * dx - r * 0.12f, cy + r * 0.95f),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}
