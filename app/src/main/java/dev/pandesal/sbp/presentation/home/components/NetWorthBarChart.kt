package dev.pandesal.sbp.presentation.home.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
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
import dev.pandesal.sbp.presentation.model.NetWorthBarGroup
import java.math.BigDecimal

/**
 * Displays grouped bars for assets and liabilities over time.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun NetWorthBarChart(
    data: List<NetWorthBarGroup>,
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
            Text("Net Worth", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
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
                    val maxY = data.maxOf { maxOf(it.assets, it.liabilities) }
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        data.forEach { entry ->
                            val assetsHeight = animateDpAsState(
                                if (maxY > BigDecimal.ZERO) chartHeight * (entry.assets / maxY).toFloat() else 0.dp
                            ).value
                            val liabilitiesHeight = animateDpAsState(
                                if (maxY > BigDecimal.ZERO) chartHeight * (entry.liabilities / maxY).toFloat() else 0.dp
                            ).value
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(assetsHeight)
                                            .width(barWidth)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .height(liabilitiesHeight)
                                            .width(barWidth)
                                            .background(MaterialTheme.colorScheme.error)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(entry.label, style = MaterialTheme.typography.labelSmall)
                                val diff = entry.assets - entry.liabilities
                                Text(
                                    diff.toPlainString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

