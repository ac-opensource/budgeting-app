package dev.pandesal.sbp.presentation.model

import java.math.BigDecimal

/**
 * Generic entry for a paired bar chart.
 */
data class PairedBarChartEntry(
    val label: String,
    val first: BigDecimal,
    val second: BigDecimal
)
