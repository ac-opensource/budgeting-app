package dev.pandesal.sbp.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import dev.pandesal.sbp.presentation.model.DailySpend
import dev.pandesal.sbp.presentation.model.DailySpendUiModel
import dev.pandesal.sbp.presentation.theme.StopBeingPoorTheme
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun DailySpendBarChart(
    dailySpendUiModel: DailySpendUiModel,
    modifier: Modifier = Modifier,
    barWidth: Dp = 64.dp,
    chartHeight: Dp = 200.dp,
    onBarClick: (Int, IntOffset) -> Unit = { _, _ -> }
) {
    val primary = Color(0xFF4BE263)// MaterialTheme.colorScheme.primary
    val secondary = Color(0xFF4BE263) //MaterialTheme.colorScheme.secondary
    val outline = MaterialTheme.colorScheme.outline
    val maxY = dailySpendUiModel.entries.maxOfOrNull { it.amount } ?: BigDecimal.ZERO

    Column(
        modifier = modifier
    ) {
        val headerText = if (dailySpendUiModel.hasData) {
            val arrow = if (dailySpendUiModel.changeFromLastWeek >= 0) "\u2191" else "\u2193"
            val changeText = String.format("%.0f", kotlin.math.abs(dailySpendUiModel.changeFromLastWeek))
            "Your spending this week $arrow$changeText%"
        } else {
            "Your data will show here"
        }
        Text(
            headerText,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        var rowCoords: LayoutCoordinates? = null
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .onGloballyPositioned { rowCoords = it }
                .pointerInput(dailySpendUiModel.entries) {
                    detectTapGestures { offset ->
                        val index = (offset.x / (size.width / dailySpendUiModel.entries.size)).toInt()
                        if (index in dailySpendUiModel.entries.indices) {
                            val global = (rowCoords?.positionInRoot() ?: Offset.Zero) + offset
                            onBarClick(index, IntOffset(global.x.toInt(), global.y.toInt()))
                        }
                    }
                },
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dailySpendUiModel.entries.forEachIndexed { index, entry ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    val percentage = if (maxY == BigDecimal.ZERO) 0f else (entry.amount.divide(maxY, 2, RoundingMode.HALF_UP)).toFloat()
                    val barFillHeight = chartHeight * percentage

                    if (dailySpendUiModel.hasData && index == dailySpendUiModel.entries.lastIndex) {
                        val prev = dailySpendUiModel.entries.getOrNull(index - 1)?.amount ?: BigDecimal.ZERO
                        val diff = if (prev == BigDecimal.ZERO) 0.0 else ((entry.amount - prev)
                            .divide(prev, java.math.MathContext.DECIMAL64)
                            .toDouble() * 100)
                        val arrowToday = if (diff >= 0) "\u2191" else "\u2193"
                        val diffText = String.format("%.0f", kotlin.math.abs(diff))
                        Text(
                            "$arrowToday $diffText%",
                            color = primary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black
                        )
                    }

                    val barModifier = Modifier
                        .height(barFillHeight)
                        .width(barWidth)
                        .clip(RoundedCornerShape(12.dp))

                    val finalModifier = if (dailySpendUiModel.hasData) {
                        barModifier.background(
                            brush = if (index == dailySpendUiModel.entries.lastIndex) {
                                if (entry.amount > BigDecimal.ZERO) {
                                    Brush.verticalGradient(listOf(primary, secondary))
                                } else {
                                    Brush.verticalGradient(listOf(outline, outline))
                                }
                            } else {
                                Brush.verticalGradient(listOf(Color.LightGray, Color.LightGray))
                            }
                        )
                    } else {
                        barModifier.drawBehind {
                            drawRoundRect(
                                color = outline,
                                cornerRadius = CornerRadius(12.dp.toPx()),
                                style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
                            )
                        }
                    }

                    Box(modifier = finalModifier)

                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // X-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dailySpendUiModel.entries.forEachIndexed { index, entry ->
                Text(
                    text = if (index == dailySpendUiModel.entries.lastIndex) "Today" else entry.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview
fun DailySpendBarChartPreview() {
    StopBeingPoorTheme {
        DailySpendBarChart(
            dailySpendUiModel = DailySpendUiModel(
                entries = listOf(
                    DailySpend("MON", BigDecimal("10.0")),
                    DailySpend("TUE", BigDecimal("20.0")),
                    DailySpend("WED", BigDecimal("0.0")),
                    DailySpend("THU", BigDecimal("15.0")),
                    DailySpend("FRI", BigDecimal("5.0"))
                ),
                changeFromLastWeek = 0.0,
                hasData = true
            )
        )
    }
}
