package dev.pandesal.sbp.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AnimatedGradientBackground(modifier: Modifier) {
    val size = remember { mutableStateOf(IntSize.Zero) }

    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val radians = Math.toRadians(angle.toDouble())
    val x = cos(radians).toFloat()
    val y = sin(radians).toFloat()

    val width = size.value.width.toFloat()
    val height = size.value.height.toFloat()
    val radius = max(width, height)

    val softWhites = listOf(
        Color(0xFFFDFDFD),
        Color(0xFFF7F7F7),
        Color(0xFFFAFAFA),
        Color(0xFFF0F0F0),
        Color(0xFFF9F9F9),
        Color(0xFFF2F2F2),
        Color(0xFFF5F5F5),
        Color(0xFFEDEDED),
    )

    val shuffled = remember { softWhites.shuffled().take(2) }

    Box(
        modifier = modifier
            .onSizeChanged { size.value = it }
            .background(
                brush = Brush.linearGradient(
                    colors = shuffled,
                    start = Offset(width / 2f - x * radius, height / 2f - y * radius),
                    end = Offset(width / 2f + x * radius, height / 2f + y * radius)
                )
            )
    )
}