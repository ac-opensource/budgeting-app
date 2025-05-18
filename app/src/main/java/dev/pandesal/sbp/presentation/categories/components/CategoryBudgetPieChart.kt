package dev.pandesal.sbp.presentation.categories.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CategoryBudgetPieChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (data.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        var startAngle = -90f
                        data.forEachIndexed { index, (_, percent) ->
                            val sweep = (percent / 100f) * 360f
                            drawArc(
                                color = getCategoryColor(index),
                                startAngle = startAngle,
                                sweepAngle = sweep.toFloat(),
                                useCenter = true
                            )
                            startAngle += sweep.toFloat()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                data.forEachIndexed { index, (label, percent) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(getCategoryColor(index), RoundedCornerShape(50))
                        )
                        Text(
                            label,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .weight(1f)
                        )
                        Text("${percent.toInt()}%", style = MaterialTheme.typography.labelLarge)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Create categories and budgets to see breakdown")
                }
            }
        }
    }
}

private val categoryColors = listOf(
    Color(0xFF4B3B60), // Deep muted violet
    Color(0xFF6E8894), // Slate gray
    Color(0xFFAD6A6C), // Dusty rose
    Color(0xFF5E8D74), // Darker Cambridge green
    Color(0xFF837060)  // Warm taupe
)

fun getCategoryColor(index: Int): Color =
    categoryColors.getOrElse(index) { Color(0xFF999999) }

