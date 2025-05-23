package dev.pandesal.sbp.presentation.model

import java.math.BigDecimal

data class NetWorthUiModel(
    val label: String,
    val start: BigDecimal,
    val end: BigDecimal,
    val min: BigDecimal,
    val max: BigDecimal
)