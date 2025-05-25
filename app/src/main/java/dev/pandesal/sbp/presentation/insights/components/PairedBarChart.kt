package dev.pandesal.sbp.presentation.insights.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.presentation.model.PairedBarChartEntry
import java.math.BigDecimal
import java.math.RoundingMode

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PairedBarChart(
    entries: List<PairedBarChartEntry>,
    title: String,
    firstLabel: String,
    secondLabel: String,
    modifier: Modifier = Modifier,
    barWidth: Dp = 16.dp,
    chartHeight: Dp = 100.dp,
    valueFormatter: (BigDecimal) -> String = { it.toPlainString() },
    firstColor: Color = MaterialTheme.colorScheme.primary,
    secondColor: Color = MaterialTheme.colorScheme.error
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
            var dialogEntry by remember { mutableStateOf<PairedBarChartEntry?>(null) }

            dialogEntry?.let { entry ->
                AlertDialog(
                    onDismissRequest = { dialogEntry = null },
                    confirmButton = {
                        TextButton(onClick = { dialogEntry = null }) { Text("OK") }
                    },
                    title = { Text(entry.label) },
                    text = { Text("$firstLabel: ${valueFormatter(entry.first)}\n$secondLabel: ${valueFormatter(entry.second)}") }
                )
            }

            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
            ) {
                if (entries.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No data available")
                    }
                } else {
                    val maxY = entries.maxOfOrNull { maxOf(it.first, it.second) } ?: BigDecimal.ZERO
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
                                Text(
                                    text = valueFormatter(label),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Spacer(Modifier.height(30.dp))
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            val lineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            val lineBackgroundColor = firstColor.copy(alpha = 0.1f)
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 30.dp)
                                    .matchParentSize()
                                    .drawBehind {
                                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                                        val spacing = barWidth.toPx()
                                        for (i in 0..4) {
                                            val y = size.height * (1f - i / 4f)
                                            drawLine(
                                                color = lineColor,
                                                start = androidx.compose.ui.geometry.Offset(spacing, y),
                                                end = androidx.compose.ui.geometry.Offset(size.width - spacing, y),
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
                                    }
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = barWidth)
                                    .pointerInput(entries) {
                                        detectTapGestures { offset ->
                                            val index = (offset.x / (size.width / entries.size)).toInt()
                                            if (index in entries.indices) {
                                                dialogEntry = entries[index]
                                            }
                                        }
                                    },
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                entries.forEach { entry ->
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Bottom
                                    ) {
                                        val firstPct = if (maxY == BigDecimal.ZERO) 0f else (entry.first.divide(maxY, 2, RoundingMode.HALF_UP)).toFloat()
                                        val secondPct = if (maxY == BigDecimal.ZERO) 0f else (entry.second.divide(maxY, 2, RoundingMode.HALF_UP)).toFloat()
                                        val firstHeight = animateDpAsState(targetValue = chartHeight * firstPct)
                                        val secondHeight = animateDpAsState(targetValue = chartHeight * secondPct)

                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.Bottom
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .height(firstHeight.value)
                                                    .width(barWidth)
                                                    .pointerInput(entry) { detectTapGestures { dialogEntry = entry } }
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(firstColor.copy(alpha = 0.8f), firstColor.copy(alpha = 0.4f))
                                                        ),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .height(secondHeight.value)
                                                    .width(barWidth)
                                                    .pointerInput(entry) { detectTapGestures { dialogEntry = entry } }
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(secondColor.copy(alpha = 0.8f), secondColor.copy(alpha = 0.4f))
                                                        ),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(entry.label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.height(26.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PairedBarChartPreview() {
    val entries = listOf(
        PairedBarChartEntry("Jan", BigDecimal(1000), BigDecimal(700)),
        PairedBarChartEntry("Feb", BigDecimal(1200), BigDecimal(500)),
        PairedBarChartEntry("Mar", BigDecimal(900), BigDecimal(1100))
    )
    MaterialTheme {
        PairedBarChart(
            entries = entries,
            title = "Example Chart",
            firstLabel = "First",
            secondLabel = "Second"
        )
    }
}
