package dev.pandesal.sbp.presentation.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import java.math.BigDecimal
import kotlin.math.abs

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun NetWorthBarChart(
    data: List<NetWorthUiModel>,
    barWidth: Dp = 12.dp,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 100.dp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = CardDefaults.shape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val dialogEntry = remember { mutableStateOf<NetWorthUiModel?>(null) }

            Text("Net Worth", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Start/End/Range", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
            ) {
                if (data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No data available")
                    }
                } else {
                    val maxY = data.maxOf { it.max }
                    val minY = data.minOf { it.min }
                    val range = maxOf(maxY - minY, BigDecimal.ONE)
                    val barSpace = this.maxWidth / data.size
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        data.forEach { entry ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                val onSurface = MaterialTheme.colorScheme.onSurface
                                val outline = MaterialTheme.colorScheme.outline
                                androidx.compose.foundation.Canvas(
                                    modifier = Modifier
                                        .height(chartHeight)
                                        .fillMaxWidth()
                                ) {
                                    val barCenter = size.width / 2
                                    val maxPos = size.height * (BigDecimal.ONE - (entry.max - minY) / range).toFloat()
                                    val minPos = size.height * (BigDecimal.ONE - (entry.min - minY) / range).toFloat()
                                    val startPos = size.height * (BigDecimal.ONE - (entry.start - minY) / range).toFloat()
                                    val endPos = size.height * (BigDecimal.ONE - (entry.end - minY) / range).toFloat()
                                    drawLine(
                                        color = onSurface,
                                        start = Offset(barCenter, maxPos),
                                        end = Offset(barCenter, minPos),
                                        strokeWidth = 2f
                                    )
                                    val color = when {
                                        entry.end > entry.start -> Color(0xFF4CAF50)
                                        entry.end < entry.start -> Color(0xFFF44336)
                                        else -> outline
                                    }
                                    drawRect(
                                        color = color,
                                        topLeft = Offset(barCenter - barWidth.toPx() / 2, minOf(startPos, endPos)),
                                        size = androidx.compose.ui.geometry.Size(
                                            barWidth.toPx(),
                                            abs(startPos - endPos)
                                        )
                                    )
                                    drawRect(
                                        color = color,
                                        topLeft = Offset(barCenter - barWidth.toPx() / 2, minOf(startPos, endPos)),
                                        size = androidx.compose.ui.geometry.Size(
                                            barWidth.toPx(),
                                            abs(startPos - endPos)
                                        ),
                                        style = Stroke(width = 1.dp.toPx())
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(entry.label, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
