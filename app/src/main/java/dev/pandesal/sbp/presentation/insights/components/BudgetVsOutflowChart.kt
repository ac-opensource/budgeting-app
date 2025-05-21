package dev.pandesal.sbp.presentation.insights.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.presentation.model.BudgetOutflowUiModel
import kotlin.math.max

@Composable
fun BudgetVsOutflowChart(
    entries: List<BudgetOutflowUiModel>,
    subtitle: String = "This Month",
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
            val primaryColor = MaterialTheme.colorScheme.primary
            val errorColor = MaterialTheme.colorScheme.error
            val dialogEntry = remember { mutableStateOf<BudgetOutflowUiModel?>(null) }

            dialogEntry.value?.let { entry ->
                AlertDialog(
                    onDismissRequest = { dialogEntry.value = null },
                    confirmButton = {
                        TextButton(onClick = { dialogEntry.value = null }) { Text("OK") }
                    },
                    title = { Text(entry.label) },
                    text = { Text("Budget: ${'$'}{entry.budget}\nOutflow: ${'$'}{entry.outflow}") }
                )
            }

            Text("Budget vs Outflow", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                if (entries.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No data available")
                    }
                } else {
                    var groupWidth by remember { mutableStateOf(0f) }
                    var spacingPx by remember { mutableStateOf(0f) }
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(entries) {
                                detectTapGestures { offset ->
                                    val index = ((offset.x - spacingPx) / groupWidth).toInt()
                                    if (index in entries.indices) {
                                        dialogEntry.value = entries[index]
                                    }
                                }
                            }
                    ) {
                        val spacing = barWidth.toPx()
                        spacingPx = spacing
                        val maxY = entries.maxOfOrNull { max(it.budget, it.outflow) } ?: 0.0
                        if (maxY == 0.0) return@Canvas
                        val chartHeight = size.height - spacing
                        groupWidth = (size.width - spacing * 2) / entries.size
                        entries.forEachIndexed { index, entry ->
                            val budgetHeight = chartHeight * (entry.budget / maxY).toFloat()
                            val outflowHeight = chartHeight * (entry.outflow / maxY).toFloat()
                            val xOffset = spacing + index * groupWidth
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    listOf(primaryColor.copy(alpha = 0.8f), primaryColor.copy(alpha = 0.4f)),
                                    startY = size.height - budgetHeight,
                                    endY = size.height
                                ),
                                topLeft = Offset(xOffset, size.height - budgetHeight),
                                size = Size(barWidth.toPx(), budgetHeight),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    listOf(errorColor.copy(alpha = 0.8f), errorColor.copy(alpha = 0.4f)),
                                    startY = size.height - outflowHeight,
                                    endY = size.height
                                ),
                                topLeft = Offset(xOffset + barWidth.toPx() + 4.dp.toPx(), size.height - outflowHeight),
                                size = Size(barWidth.toPx(), outflowHeight),
                                cornerRadius = CornerRadius(4.dp.toPx())
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
                        entries.forEach { entry ->
                            Text(entry.label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
