package dev.pandesal.sbp.presentation.model

import java.math.BigDecimal

/**
 * Represents a pair of asset and liability values for a given period
 */
data class NetWorthBarGroup(
    val label: String,
    val assets: BigDecimal,
    val liabilities: BigDecimal
)

