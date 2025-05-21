package dev.pandesal.sbp.presentation.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.pandesal.sbp.presentation.model.DailySpendUiModel
import dev.pandesal.sbp.presentation.theme.StopBeingPoorTheme

@Composable
fun DailySpendBarChart(
    entries: List<DailySpendUiModel>,
    modifier: Modifier = Modifier,
    barWidth: Dp = 16.dp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val primary = MaterialTheme.colorScheme.primary
            val secondary = MaterialTheme.colorScheme.secondary
            Text("Last 5 Days", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            val maxY = entries.maxOfOrNull { it.amount } ?: 0.0
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .width(40.dp)
                        .height(100.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 4 downTo 0) {
                        Text(
                            text = "${"%.0f".format(maxY * i / 4)}",
                            fontSize = 10.sp,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val spacing = barWidth.toPx()
                        val chartHeight = size.height - spacing
                        val groupWidth = (size.width - spacing * 2) / entries.size

                        drawRect(
                            brush = Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surface
                                )
                            ),
                            size = size
                        )

                        entries.forEachIndexed { index, entry ->
                            val xOffset = spacing + index * groupWidth + (groupWidth - barWidth.toPx()) / 2
                            if (entry.amount > 0) {
                                val barHeight = if (maxY == 0.0) 0f else chartHeight * (entry.amount / maxY).toFloat()
                                drawRoundRect(
                                    brush = Brush.verticalGradient(listOf(primary, secondary)),
                                    topLeft = Offset(xOffset, size.height - barHeight),
                                    size = Size(barWidth.toPx(), barHeight),
                                    cornerRadius = CornerRadius(4.dp.toPx())
                                )
                            } else {
                                val dashHeight = chartHeight * 0.2f
                                drawLine(
                                    color = MaterialTheme.colorScheme.outline,
                                    start = Offset(xOffset + barWidth.toPx() / 2, size.height),
                                    end = Offset(xOffset + barWidth.toPx() / 2, size.height - dashHeight),
                                    strokeWidth = barWidth.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(top = 4.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        entries.forEach { entry ->
                            Text(entry.label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
fun DailySpendBarChartPreview() {
    StopBeingPoorTheme {
        DailySpendBarChart(
            entries = listOf(
                DailySpendUiModel("MON", 10.0),
                DailySpendUiModel("TUE", 20.0),
                DailySpendUiModel("WED", 0.0),
                DailySpendUiModel("THU", 15.0),
                DailySpendUiModel("FRI", 5.0)
            )
        )
    }
}
