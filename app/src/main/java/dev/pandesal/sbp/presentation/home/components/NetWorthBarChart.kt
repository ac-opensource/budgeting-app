package dev.pandesal.sbp.presentation.home.components

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import kotlin.math.max

@Composable
fun NetWorthBarChart(
    data: List<NetWorthUiModel>,
    barWidth: Dp = 16.dp,
    modifier: Modifier = Modifier
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
            val dialogEntry = remember { mutableStateOf<NetWorthUiModel?>(null) }

            dialogEntry.value?.let { entry ->
                AlertDialog(
                    onDismissRequest = { dialogEntry.value = null },
                    confirmButton = {
                        TextButton(onClick = { dialogEntry.value = null }) { Text("OK") }
                    },
                    title = { Text(entry.label) },
                    text = { Text("Assets: ${'$'}{entry.assets}\nLiabilities: ${'$'}{entry.liabilities}") }
                )
            }

            Text("Net Worth", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Assets vs Liabilities", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                if (data.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No data available")
                    }
                } else {
                    var groupWidth by remember { mutableStateOf(0f) }
                    var spacingPx by remember { mutableStateOf(0f) }
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(data) {
                                detectTapGestures { offset ->
                                    val index = ((offset.x - spacingPx) / groupWidth).toInt()
                                    if (index in data.indices) {
                                        dialogEntry.value = data[index]
                                    }
                                }
                            }
                    ) {
                        val spacing = barWidth.toPx()
                        spacingPx = spacing
                        val maxY = data.maxOfOrNull { max(it.assets, it.liabilities) } ?: 0.0
                        if (maxY == 0.0) return@Canvas
                        val chartHeight = size.height - spacing
                        groupWidth = (size.width - spacing * 2) / data.size
                        data.forEachIndexed { index, entry ->
                            val assetsHeight = chartHeight * (entry.assets / maxY).toFloat()
                            val liabilitiesHeight = chartHeight * (entry.liabilities / maxY).toFloat()
                            val xOffset = spacing + index * groupWidth
                            drawRoundRect(
                                color = primaryColor,
                                topLeft = Offset(xOffset, size.height - assetsHeight),
                                size = Size(barWidth.toPx(), assetsHeight),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                            drawRoundRect(
                                color = errorColor,
                                topLeft = Offset(xOffset + barWidth.toPx() + 4.dp.toPx(), size.height - liabilitiesHeight),
                                size = Size(barWidth.toPx(), liabilitiesHeight),
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
                        data.forEach { entry ->
                            Text(entry.label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
