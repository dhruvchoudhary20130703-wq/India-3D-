package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.data.india.IndiaGeoData
import com.example.data.model.CitySummaryTemp
import com.example.data.model.IndianCity
import com.example.data.model.TemperatureUnit
import com.example.data.model.formatTemp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

private data class MapVec3(val x: Float, val y: Float, val z: Float)

private data class MapProj(val x: Float, val y: Float, val zDepth: Float, val scale: Float)

private data class PinHitTarget(
    val city: IndianCity,
    val screenPos: Offset,
)

private fun latLonToMapCoords(lat: Double, lon: Double): Pair<Float, Float> {
    // India bounds approx: lat 6.5..36.5, lon 68.0..97.5
    val centerLat = 22.0
    val centerLon = 82.0
    val x = ((lon - centerLon) / 15.0).toFloat()
    // Flip lat because screen Y increases downward; map lies on XZ plane when tilted
    val z = ((centerLat - lat) / 15.0).toFloat()
    return x to z
}

private fun projectMapPoint(
    v: MapVec3,
    yawRad: Float,
    pitchRad: Float,
    center: Offset,
    focal: Float,
    unitScale: Float,
): MapProj {
    val cosY = cos(yawRad)
    val sinY = sin(yawRad)
    val x1 = v.x * cosY - v.z * sinY
    val z1 = v.x * sinY + v.z * cosY

    val cosP = cos(pitchRad)
    val sinP = sin(pitchRad)
    val y2 = v.y * cosP - z1 * sinP
    val z2 = v.y * sinP + z1 * cosP

    val camZ = (focal + z2).coerceAtLeast(120f)
    val persp = focal / camZ
    return MapProj(
        x = center.x + x1 * unitScale * persp,
        y = center.y + y2 * unitScale * persp,
        zDepth = z2,
        scale = persp,
    )
}

@Composable
fun India3DMapCanvas(
    selectedCity: IndianCity,
    citySummaries: Map<String, CitySummaryTemp>,
    unit: TemperatureUnit,
    onSelectCity: (IndianCity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val yawAnim = remember { Animatable(-0.14f) }
    val pitchAnim = remember { Animatable(0.74f) }
    var lastPinTargets by remember { mutableStateOf<List<PinHitTarget>>(emptyList()) }

    val pulseTransition = rememberInfiniteTransition(label = "india_map_pulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
        ),
        label = "pulse",
    )

    val mapMarkers: List<IndianCity> = remember(selectedCity.id) {
        val baseIds = listOf(
            "jodhpur_rj",
            "barmer_rj",
            "mumbai_mh",
            "jaipur_rj",
            "new_delhi_dl",
            "srinagar_jk",
            "leh_la",
            "ahmedabad_gj",
            "bengaluru_ka",
            "chennai_tn",
            "hyderabad_ts",
            "kolkata_wb",
            "guwahati_as",
            "kochi_kl",
            "bhopal_mp",
            "lucknow_up",
            "bhubaneswar_od",
            "panaji_ga",
        )
        val base = baseIds.mapNotNull { IndiaGeoData.findCityById(it) }
        if (base.any { it.id == selectedCity.id }) base else base + selectedCity
    }

    val labelPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(6f, 0f, 2f, android.graphics.Color.argb(180, 0, 0, 0))
        }
    }
    val tempPaint = remember {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.argb(245, 253, 224, 71)
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(4f, 0f, 1f, android.graphics.Color.argb(190, 0, 0, 0))
        }
    }

    Box(
        modifier = modifier
            .semantics { contentDescription = "Interactive 3D weather map of India" }
            .pointerInput(mapMarkers) {
                detectTapGestures(
                    onDoubleTap = {
                        scope.launch {
                            launch { yawAnim.animateTo(-0.14f, spring(stiffness = Spring.StiffnessMediumLow)) }
                            launch { pitchAnim.animateTo(0.74f, spring(stiffness = Spring.StiffnessMediumLow)) }
                        }
                    },
                    onTap = { tapOffset ->
                        val hit = lastPinTargets
                            .minByOrNull { hypot(it.screenPos.x - tapOffset.x, it.screenPos.y - tapOffset.y) }
                        if (hit != null && hypot(hit.screenPos.x - tapOffset.x, hit.screenPos.y - tapOffset.y) < 72f) {
                            onSelectCity(hit.city)
                        }
                    },
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            yawAnim.animateTo(
                                yawAnim.value.coerceIn(-0.65f, 0.65f),
                                spring(stiffness = Spring.StiffnessLow),
                            )
                            pitchAnim.animateTo(
                                pitchAnim.value.coerceIn(0.45f, 1.18f),
                                spring(stiffness = Spring.StiffnessLow),
                            )
                        }
                    },
                ) { change, dragAmount ->
                    change.consume()
                    scope.launch {
                        yawAnim.snapTo((yawAnim.value + dragAmount.x * 0.0045f).coerceIn(-0.9f, 0.9f))
                        pitchAnim.snapTo((pitchAnim.value + dragAmount.y * 0.0045f).coerceIn(0.38f, 1.32f))
                    }
                }
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w * 0.5f, h * 0.50f)
            val unitScale = minOf(w, h) * 0.44f
            val focal = 520f
            val yaw = yawAnim.value
            val pitch = pitchAnim.value

            // 1. Subtle 3D radar coordinate grid under India
            for (g in -4..4) {
                val offset = g * 0.28f
                val hStart = projectMapPoint(MapVec3(-1.2f, 0.08f, offset), yaw, pitch, center, focal, unitScale)
                val hEnd = projectMapPoint(MapVec3(1.2f, 0.08f, offset), yaw, pitch, center, focal, unitScale)
                drawLine(
                    color = Color(0xFF38BDF8).copy(alpha = 0.12f),
                    start = Offset(hStart.x, hStart.y),
                    end = Offset(hEnd.x, hEnd.y),
                    strokeWidth = 1f,
                )
                val vStart = projectMapPoint(MapVec3(offset, 0.08f, -1.2f), yaw, pitch, center, focal, unitScale)
                val vEnd = projectMapPoint(MapVec3(offset, 0.08f, 1.2f), yaw, pitch, center, focal, unitScale)
                drawLine(
                    color = Color(0xFF38BDF8).copy(alpha = 0.12f),
                    start = Offset(vStart.x, vStart.y),
                    end = Offset(vEnd.x, vEnd.y),
                    strokeWidth = 1f,
                )
            }

            // 2. Extruded 3D Mainland India Silhouette (bottom shadow slab + top surface)
            val topPts = IndiaGeoData.mainlandIndiaOutlineLatLon.map { (lat, lon) ->
                val (mx, mz) = latLonToMapCoords(lat, lon)
                projectMapPoint(MapVec3(mx, -0.04f, mz), yaw, pitch, center, focal, unitScale)
            }
            val botPts = IndiaGeoData.mainlandIndiaOutlineLatLon.map { (lat, lon) ->
                val (mx, mz) = latLonToMapCoords(lat, lon)
                projectMapPoint(MapVec3(mx, 0.07f, mz), yaw, pitch, center, focal, unitScale)
            }

            // Draw extruded 3D side skirt segments
            for (i in topPts.indices) {
                val next = (i + 1) % topPts.size
                val segPath = Path().apply {
                    moveTo(topPts[i].x, topPts[i].y)
                    lineTo(topPts[next].x, topPts[next].y)
                    lineTo(botPts[next].x, botPts[next].y)
                    lineTo(botPts[i].x, botPts[i].y)
                    close()
                }
                drawPath(segPath, color = Color(0xFF0C4A6E).copy(alpha = 0.85f))
            }

            val topIndiaPath = Path().apply {
                topPts.forEachIndexed { idx, p ->
                    if (idx == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
            drawPath(
                path = topIndiaPath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E3A5F).copy(alpha = 0.94f),
                        Color(0xFF0F294A).copy(alpha = 0.94f),
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h),
                ),
            )
            drawPath(
                path = topIndiaPath,
                color = Color(0xFF38BDF8).copy(alpha = 0.62f),
                style = Stroke(width = 2.2f),
            )

            // Lakshadweep & Andaman island clusters
            val islandClusters = listOf(
                10.6 to 72.6, // Lakshadweep
                11.7 to 92.7, // Andaman
            )
            islandClusters.forEach { (lat, lon) ->
                val (mx, mz) = latLonToMapCoords(lat, lon)
                val p = projectMapPoint(MapVec3(mx, -0.04f, mz), yaw, pitch, center, focal, unitScale)
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 0.55f),
                    radius = 5f * p.scale,
                    center = Offset(p.x, p.y),
                )
            }

            // 3. Project City Beacons and sort far-to-near
            data class ProjectedCityPin(
                val city: IndianCity,
                val base: MapProj,
                val head: MapProj,
                val summary: CitySummaryTemp?,
            )

            val projectedPins = mapMarkers.map { city ->
                val (mx, mz) = latLonToMapCoords(city.lat, city.lon)
                val isSelected = city.id == selectedCity.id
                val pinHeight = if (isSelected) -0.26f else -0.17f
                val baseProj = projectMapPoint(MapVec3(mx, -0.04f, mz), yaw, pitch, center, focal, unitScale)
                val headProj = projectMapPoint(MapVec3(mx, pinHeight, mz), yaw, pitch, center, focal, unitScale)
                ProjectedCityPin(city, baseProj, headProj, citySummaries[city.id])
            }.sortedByDescending { it.base.zDepth }

            val newHitTargets = mutableListOf<PinHitTarget>()
            labelPaint.textSize = (w * 0.029f).coerceIn(22f, 34f)
            tempPaint.textSize = (w * 0.027f).coerceIn(20f, 31f)

            projectedPins.forEach { pin ->
                val isSelected = pin.city.id == selectedCity.id
                val isHighlight = pin.city.id in setOf("jodhpur_rj", "barmer_rj", "mumbai_mh")
                val tempC = pin.summary?.tempCelsius
                val pinColor = when {
                    tempC == null -> Color(0xFF38BDF8)
                    tempC >= 35.0 -> Color(0xFFFF6B4A)
                    tempC >= 27.0 -> Color(0xFFFFB340)
                    tempC >= 17.0 -> Color(0xFF34D399)
                    else -> Color(0xFF7DD3FC)
                }

                // Pulsing halo for selected or Jodhpur/Barmer/Mumbai
                if (isSelected || isHighlight) {
                    val pulseR = (if (isSelected) 28f else 18f) * (0.6f + pulse * 0.8f)
                    drawCircle(
                        color = pinColor.copy(alpha = (1f - pulse) * if (isSelected) 0.55f else 0.30f),
                        radius = pulseR,
                        center = Offset(pin.base.x, pin.base.y),
                    )
                }

                // 3D Vertical Stem
                drawLine(
                    color = if (isSelected) Color.White else pinColor.copy(alpha = 0.85f),
                    start = Offset(pin.base.x, pin.base.y),
                    end = Offset(pin.head.x, pin.head.y),
                    strokeWidth = if (isSelected) 3.4f else 2.2f,
                )

                // Base Anchor Dot
                drawCircle(
                    color = pinColor,
                    radius = if (isSelected) 5.5f else 3.8f,
                    center = Offset(pin.base.x, pin.base.y),
                )

                // 3D Shaded Floating Orb
                val orbR = (if (isSelected) 11.5f else if (isHighlight) 8.5f else 6.5f) * pin.head.scale
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, pinColor, pinColor.copy(alpha = 0.65f)),
                        center = Offset(pin.head.x - orbR * 0.3f, pin.head.y - orbR * 0.3f),
                        radius = orbR * 1.4f,
                    ),
                    radius = orbR,
                    center = Offset(pin.head.x, pin.head.y),
                )

                if (isSelected) {
                    drawCircle(
                        color = Color.White,
                        radius = orbR + 3f,
                        center = Offset(pin.head.x, pin.head.y),
                        style = Stroke(width = 2f),
                    )
                }

                // Label prioritized cities + selected city cleanly
                val showLabel = isSelected || pin.city.id in setOf(
                    "jodhpur_rj",
                    "barmer_rj",
                    "mumbai_mh",
                    "new_delhi_dl",
                    "bengaluru_ka",
                    "kolkata_wb",
                    "chennai_tn",
                    "leh_la",
                    "guwahati_as",
                )
                if (showLabel) {
                    val labelY = pin.head.y - orbR - 10f
                    drawContext.canvas.nativeCanvas.drawText(
                        pin.city.name,
                        pin.head.x,
                        labelY,
                        labelPaint,
                    )
                    if (tempC != null) {
                        drawContext.canvas.nativeCanvas.drawText(
                            tempC.formatTemp(unit),
                            pin.head.x,
                            labelY - labelPaint.textSize * 0.95f,
                            tempPaint,
                        )
                    }
                }

                newHitTargets.add(PinHitTarget(pin.city, Offset(pin.head.x, pin.head.y)))
            }
            lastPinTargets = newHitTargets
        }

        Surface(
            color = Color.Black.copy(alpha = 0.35f),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
        ) {
            Text(
                text = "Tap any 3D pin · Drag to tilt India",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            )
        }
    }
}
