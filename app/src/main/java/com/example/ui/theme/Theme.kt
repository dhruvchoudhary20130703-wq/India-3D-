package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val MausamDarkColorScheme = darkColorScheme(
    primary = MonsoonCyan,
    onPrimary = DeepSkyNavy,
    primaryContainer = GlassSurfaceElevated,
    onPrimaryContainer = SkyTextPrimary,
    secondary = SolarAmber,
    onSecondary = DeepSkyNavy,
    secondaryContainer = Color(0xFF3A2B12),
    onSecondaryContainer = Color(0xFFFFDFB0),
    tertiary = KonkanTeal,
    onTertiary = DeepSkyNavy,
    background = DeepSkyNavy,
    onBackground = SkyTextPrimary,
    surface = MidnightAtmosphere,
    onSurface = SkyTextPrimary,
    surfaceVariant = GlassSurfaceDark,
    onSurfaceVariant = SkyTextSecondary,
    outline = Color(0x33FFFFFF),
    outlineVariant = Color(0x1FFFFFFF),
)

private val MausamLightColorScheme = lightColorScheme(
    primary = AzurePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9E8FF),
    onPrimaryContainer = Color(0xFF001C3B),
    secondary = SaffronSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE6C2),
    onSecondaryContainer = Color(0xFF2D1600),
    tertiary = TealTertiary,
    onTertiary = Color.White,
    background = CrispSkyWhite,
    onBackground = InkPrimary,
    surface = DaySurface,
    onSurface = InkPrimary,
    surfaceVariant = DaySurfaceVariant,
    onSurfaceVariant = InkSecondary,
    outline = Color(0x260F172A),
    outlineVariant = Color(0x140F172A),
)

val MausamShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp),
)

@Composable
fun Mausam3DTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> MausamDarkColorScheme
        else -> MausamLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MausamTypography,
        shapes = MausamShapes,
        content = content,
    )
}
