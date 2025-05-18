package dev.pandesal.sbp.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun SquigglyDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.LightGray,
    amplitude: Float = 8f,
    wavelength: Float = 40f
) {
    Canvas(modifier = modifier) {
        val path = Path()
        val midY = size.height / 2f
        var x = 0f
        while (x <= size.width) {
            path.quadraticBezierTo(
                x + wavelength / 4f,
                midY - amplitude,
                x + wavelength / 2f,
                midY
            )
            path.quadraticBezierTo(
                x + wavelength * 3f / 4f,
                midY + amplitude,
                x + wavelength,
                midY
            )
            x += wavelength
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
