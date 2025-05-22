package dev.pandesal.sbp.presentation.trends.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pandesal.sbp.presentation.model.TrendUiModel
import kotlin.math.roundToInt

@Composable
fun SpendingTrendLineChart(
    data: List<TrendUiModel>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp,
    onPointClick: (TrendUiModel) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val color = MaterialTheme.colorScheme.primary
            Text("Spending Trend", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.weight(1f))
            val maxY = data.maxOfOrNull { it.amount } ?: java.math.BigDecimal.ZERO
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .width(40.dp)
                        .height(100.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 4 downTo 0) {
                        Text(
                            text = "${"%.0f".format(maxY.multiply(java.math.BigDecimal(i).divide(java.math.BigDecimal(4), java.math.MathContext.DECIMAL64)).toDouble())}",
                            fontSize = 10.sp,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                ) {
                    if (data.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No data available")
                        }
                    } else {
                        val density = LocalDensity.current
                        val spacing = with(density) { 16.dp.toPx() }
                        if (maxY > 0) {
                            val chartHeight = constraints.maxHeight.toFloat() - spacing
                            val stepX =
                                if (data.size > 1) (constraints.maxWidth.toFloat() - spacing * 2) / (data.size - 1) else 0f

                            val linePath = Path()
                            data.forEachIndexed { index, entry ->
                                val x = spacing + stepX * index
                                val y = chartHeight * (1f - (entry.amount.divide(maxY, java.math.MathContext.DECIMAL64).toFloat()))
                                if (index == 0) {
                                    linePath.moveTo(x, y)
                                } else {
                                    linePath.lineTo(x, y)
                                }
                            }

                            val fillPath = Path().apply {
                                addPath(linePath)
                                lineTo(spacing + stepX * (data.size - 1), chartHeight)
                                lineTo(spacing, chartHeight)
                                close()
                            }

                            val color1 = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            val elements = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

                            Canvas(Modifier.fillMaxSize()) {
                                val gridDashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                                for (i in 0..4) {
                                    val y = chartHeight * (1f - i / 4f)
                                    drawLine(
                                        color = color1,
                                        start = Offset(spacing, y),
                                        end = Offset(size.width - spacing, y),
                                        pathEffect = gridDashEffect,
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }

                                drawRect(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            elements,
                                            Color.Transparent
                                        )
                                    )
                                )

                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        listOf(color.copy(alpha = 0.4f), color.copy(alpha = 0f))
                                    ),
                                    style = Fill
                                )

                                val actualCount = data.count { !it.isForecast }
                                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))

                                var lastX = 0f
                                var lastY = 0f
                                data.forEachIndexed { index, entry ->
                                    val x = spacing + stepX * index
                                    val y = chartHeight * (1f - (entry.amount.divide(maxY, java.math.MathContext.DECIMAL64).toFloat()))
                                    if (index > 0) {
                                        val path = Path().apply {
                                            moveTo(lastX, lastY)
                                            lineTo(x, y)
                                        }
                                        drawPath(
                                            path = path,
                                            color = color,
                                            style = Stroke(
                                                width = strokeWidth.toPx(),
                                                pathEffect = if (index >= actualCount) dashEffect else null
                                            )
                                        )
                                    }
                                    lastX = x
                                    lastY = y
                                }
                            }

                            val dotSize = 8.dp
                            val offsetRadius = with(density) { dotSize.toPx() / 2 }
                            data.forEachIndexed { index, entry ->
                                val x = spacing + stepX * index - offsetRadius
                                val y =
                                    chartHeight * (1f - (entry.amount.divide(maxY, java.math.MathContext.DECIMAL64).toFloat())) - offsetRadius
                                Box(
                                    modifier = Modifier
                                        .offset { IntOffset(x.roundToInt(), y.roundToInt()) }
                                        .size(dotSize)
                                        .align(Alignment.TopStart)
                                        .run {
                                            if (!entry.isForecast) this else this
                                        }
                                        .clickable { onPointClick(entry) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(top = 4.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            data.forEach { entry ->
                                Text(entry.label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

