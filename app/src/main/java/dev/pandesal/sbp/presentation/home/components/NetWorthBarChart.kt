package dev.pandesal.sbp.presentation.home.components

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
            Text("Net Worth", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Assets vs Liabilities", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val spacing = barWidth.toPx()
                    val maxY = data.maxOf { max(it.assets, it.liabilities) }
                    val chartHeight = size.height - spacing
                    val groupWidth = (size.width - spacing * 2) / data.size
                    data.forEachIndexed { index, entry ->
                        val assetsHeight = chartHeight * (entry.assets / maxY).toFloat()
                        val liabilitiesHeight = chartHeight * (entry.liabilities / maxY).toFloat()
                        val xOffset = spacing + index * groupWidth
                        drawRect(
                            color = primaryColor,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                xOffset,
                                size.height - assetsHeight
                            ),
                            size = androidx.compose.ui.geometry.Size(barWidth.toPx(), assetsHeight)
                        )
                        drawRect(
                            color = errorColor,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                xOffset + barWidth.toPx() + 4.dp.toPx(),
                                size.height - liabilitiesHeight
                            ),
                            size = androidx.compose.ui.geometry.Size(barWidth.toPx(), liabilitiesHeight)
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
                    data.forEach { entry ->
                        Text(entry.label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}