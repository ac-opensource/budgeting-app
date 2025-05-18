package dev.pandesal.sbp.presentation.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

data class CashflowEntry(val label: String, val inflow: Double, val outflow: Double)

@Composable
fun CashflowLineChart(
    entries: List<CashflowEntry>,
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val spacing = 16.dp.toPx()
                    val maxY = entries.maxOf { max(it.inflow, it.outflow) }
                    val chartHeight = size.height - spacing
                    val stepX = (size.width - spacing * 2) / (entries.size - 1)

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
                    drawPath(
                        path = inflowPath,
                        color = primaryColor,
                        style = Stroke(width = strokeWidth.toPx())
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
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceAround
                ) {
                    entries.forEach { entry ->
                        Text(entry.label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

