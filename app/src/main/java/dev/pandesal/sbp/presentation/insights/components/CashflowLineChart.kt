package dev.pandesal.sbp.presentation.insights.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.extensions.toLargeValueCurrency
import dev.pandesal.sbp.presentation.model.CashflowUiModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency
import kotlin.math.max

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CashflowLineChart(
    entries: List<CashflowUiModel>,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 2.dp
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
            val primaryColor = MaterialTheme.colorScheme.primary
            val errorColor = MaterialTheme.colorScheme.error
            Text("Cashflow", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Inflow vs Outflow", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                if (entries.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No data available")
                    }
                } else {
                    val maxY = entries.maxOfOrNull { maxOf(it.inflow, it.outflow) } ?: BigDecimal.ZERO
                    Row(Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .width(50.dp)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (i in 4 downTo 0) {
                                val label = maxY.setScale(0, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal(i))
                                    .divide(BigDecimal(4))
                                    .toLargeValueCurrency(
                                        Currency.getInstance("PHP")
                                    )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            val lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            val elements = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val spacing = 16.dp.toPx()
                                if (maxY == BigDecimal.ZERO) return@Canvas
                                val chartHeight = size.height - spacing
                                val stepX = if (entries.size > 1) (size.width - spacing * 2) / (entries.size - 1) else 0f

                                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                                for (i in 0..4) {
                                    val y = chartHeight * (1f - i / 4f)

                                    drawLine(
                                        color = lineColor,
                                        start = Offset(spacing, y),
                                        end = Offset(size.width - spacing, y),
                                        pathEffect = dashEffect,
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

                                val inflowPath = Path()
                                val outflowPath = Path()
                                entries.forEachIndexed { index, entry ->
                                    val x = spacing + stepX * index
                                    val inflowY = chartHeight * (1f - (entry.inflow / maxY).toFloat())
                                    val outflowY = chartHeight * (1f - (entry.outflow / maxY).toFloat())
                                    if (index == 0) {
                                        inflowPath.moveTo(x, inflowY)
                                        outflowPath.moveTo(x, outflowY)
                                    } else {
                                        inflowPath.lineTo(x, inflowY)
                                        outflowPath.lineTo(x, outflowY)
                                    }
                                }
                                val inflowFillPath = Path().apply {
                                    addPath(inflowPath)
                                    lineTo(spacing + stepX * (entries.lastIndex), chartHeight)
                                    lineTo(spacing, chartHeight)
                                    close()
                                }
                                val outflowFillPath = Path().apply {
                                    addPath(outflowPath)
                                    lineTo(spacing + stepX * (entries.lastIndex), chartHeight)
                                    lineTo(spacing, chartHeight)
                                    close()
                                }
                                drawPath(
                                    path = inflowFillPath,
                                    brush = Brush.verticalGradient(
                                        listOf(primaryColor.copy(alpha = 0.4f), primaryColor.copy(alpha = 0f))
                                    ),
                                    style = Fill
                                )
                                drawPath(
                                    path = inflowPath,
                                    color = primaryColor,
                                    style = Stroke(width = strokeWidth.toPx())
                                )
                                drawPath(
                                    path = outflowFillPath,
                                    brush = Brush.verticalGradient(
                                        listOf(errorColor.copy(alpha = 0.4f), errorColor.copy(alpha = 0f))
                                    ),
                                    style = Fill
                                )
                                drawPath(
                                    path = outflowPath,
                                    color = errorColor,
                                    style = Stroke(width = strokeWidth.toPx())
                                )
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
    }
}
