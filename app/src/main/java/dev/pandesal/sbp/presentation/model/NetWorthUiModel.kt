package dev.pandesal.sbp.presentation.model

import java.math.BigDecimal

data class NetWorthUiModel(
    val label: String,
    val assets: BigDecimal,
    val liabilities: BigDecimal
)