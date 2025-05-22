package dev.pandesal.sbp.presentation.insights.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.extensions.toLargeValueCurrency
import dev.pandesal.sbp.presentation.model.BudgetOutflowUiModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Currency
import kotlin.math.max

@SuppressLint("UnusedBoxWithConstraintsScope")
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
                    val maxY = entries.maxOfOrNull { maxOf(it.budget, it.outflow) } ?: BigDecimal.ZERO
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
                            var groupWidth by remember { mutableStateOf(0f) }
                            var spacingPx by remember { mutableStateOf(0f) }
                            val lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            val lineBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
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
                                if (maxY == BigDecimal.ZERO) return@Canvas
                                val chartHeight = size.height - spacing
                                groupWidth = (size.width - spacing * 2) / entries.size

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
                                            lineBackgroundColor,
                                            Color.Transparent
                                        )
                                    )
                                )

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
    }
}
