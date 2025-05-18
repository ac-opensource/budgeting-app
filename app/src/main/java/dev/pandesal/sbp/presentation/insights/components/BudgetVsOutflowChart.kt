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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

data class BudgetOutflowEntry(val label: String, val budget: Double, val outflow: Double)

@Composable
fun BudgetVsOutflowChart(
    entries: List<BudgetOutflowEntry>,
    modifier: Modifier = Modifier,
    barWidth: Dp = 16.dp
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
            Text("Budget vs Outflow", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("This Month", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val spacing = barWidth.toPx()
                    val maxY = entries.maxOf { max(it.budget, it.outflow) }
                    val chartHeight = size.height - spacing
                    val groupWidth = (size.width - spacing * 2) / entries.size
                    entries.forEachIndexed { index, entry ->
                        val budgetHeight = chartHeight * (entry.budget / maxY).toFloat()
                        val outflowHeight = chartHeight * (entry.outflow / maxY).toFloat()
                        val xOffset = spacing + index * groupWidth
                        drawRect(
                            color = MaterialTheme.colorScheme.primary,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                xOffset,
                                size.height - budgetHeight
                            ),
                            size = androidx.compose.ui.geometry.Size(barWidth.toPx(), budgetHeight)
                        )
                        drawRect(
                            color = MaterialTheme.colorScheme.error,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                xOffset + barWidth.toPx() + 4.dp.toPx(),
                                size.height - outflowHeight
                            ),
                            size = androidx.compose.ui.geometry.Size(barWidth.toPx(), outflowHeight)
                        )
                    }
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

