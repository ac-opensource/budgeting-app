package dev.pandesal.sbp.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFC9B4DC), // A lighter shade of Chinese Violet for contrast
    secondary = Color(0xFFB5A8C8), // A lighter shade of English Violet
    tertiary = Color(0xFFA3C5D2), // A lighter shade of Slate Gray
    background = Color(0xFF1F2D2C), // A dark, desaturated color (based on Cambridge Blue)
    surface = Color(0xFF2D3B3A), // A slightly lighter dark color for surfaces
    onPrimary = Color.Black, // Dark text/icons on the lighter primary
    onSecondary = Color.Black, // Dark text/icons on the lighter secondary
    onTertiary = Color.Black, // Dark text/icons on the lighter tertiary
    onBackground = Color(0xFFE0E0E0), // Light text/icons on the dark background
    onSurface = Color(0xFFF0F0F0), // Light text/icons on the dark surface
    inverseOnSurface = Color(0xFF000000), // Light text/icons on the dark surface
    onSurfaceVariant = Color(0xFFC0C0C0), // A slightly less bright light color for surface variants
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF67597A), // Chinese Violet
    secondary = Color(0xFF544E61), // English Violet
    tertiary = Color(0xFF6E8894), // Slate Gray
    background = Color(0xFF85BAA1), // Cambridge Blue
    surface = Color(0xFFF0F0F0), // Honeydew
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF000000),
    inverseOnSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFF212121),
    primaryContainer = Color.White
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StopBeingPoorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val customTypography = Typography.copy(
        displayLarge = Typography.displayLarge.copy(color = colorScheme.onBackground),
        displayMedium = Typography.displayMedium.copy(color = colorScheme.onBackground),
        displaySmall = Typography.displaySmall.copy(color = colorScheme.onBackground),
        headlineLarge = Typography.headlineLarge.copy(color = colorScheme.onBackground),
        headlineMedium = Typography.headlineMedium.copy(color = colorScheme.onBackground),
        headlineSmall = Typography.headlineSmall.copy(color = colorScheme.onBackground),
        titleLarge = Typography.titleLarge.copy(color = colorScheme.onSurface),
        titleMedium = Typography.titleMedium.copy(color = colorScheme.onSurface),
        titleSmall = Typography.titleSmall.copy(color = colorScheme.onSurface),
        bodyLarge = Typography.bodyLarge.copy(color = colorScheme.onSurface),
        bodyMedium = Typography.bodyMedium.copy(color = colorScheme.onSurface),
        bodySmall = Typography.bodySmall.copy(color = colorScheme.onSurface),
        labelLarge = Typography.labelLarge.copy(color = colorScheme.onSurface),
        labelMedium = Typography.labelMedium.copy(color = colorScheme.onSurface),
        labelSmall = Typography.labelSmall.copy(color = colorScheme.onSurface),
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = customTypography,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}