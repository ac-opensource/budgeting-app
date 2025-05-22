package dev.pandesal.sbp.domain.model

import java.math.BigDecimal

data class NetWorthRecord(
    val label: String,
    val assets: BigDecimal,
    val liabilities: BigDecimal
)