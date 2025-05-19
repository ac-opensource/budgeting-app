package dev.pandesal.sbp.presentation.theme

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import dev.pandesal.sbp.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AppFont {
    val ManRope = FontFamily(
        Font(R.font.manrope),
        Font(R.font.manrope_light, style = FontStyle.Italic),
        Font(R.font.manrope_medium, FontWeight.Medium),
        Font(R.font.manrope_light, FontWeight.Medium, style = FontStyle.Italic),
        Font(R.font.manrope_bold, FontWeight.Bold),
        Font(R.font.manrope_extrabold, FontWeight.ExtraBold),
        Font(R.font.manrope_semibold, FontWeight.SemiBold),
        Font(R.font.manrope_light, FontWeight.Light),
        Font(R.font.manrope_semibold, FontWeight.Bold, style = FontStyle.Italic)
    )
}


private val defaultTypography = Typography()
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge
        .copy(fontFamily = AppFont.ManRope),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.ManRope),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.ManRope),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.ManRope),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.ManRope),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.ManRope),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFont.ManRope),
    titleLargeEmphasized = defaultTypography.titleLargeEmphasized.copy(fontFamily = AppFont.ManRope, fontWeight = FontWeight.Bold),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.ManRope),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.ManRope),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.ManRope),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.ManRope),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.ManRope),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.ManRope),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.ManRope),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.ManRope)
)