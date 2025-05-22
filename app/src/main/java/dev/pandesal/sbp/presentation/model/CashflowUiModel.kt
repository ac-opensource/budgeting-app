package dev.pandesal.sbp.presentation.model

import java.math.BigDecimal

data class CashflowUiModel(
    val label: String,
    val inflow: BigDecimal,
    val outflow: BigDecimal
)
