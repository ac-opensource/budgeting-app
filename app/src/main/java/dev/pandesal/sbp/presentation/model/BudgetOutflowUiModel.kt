package dev.pandesal.sbp.presentation.model

import java.math.BigDecimal

data class BudgetOutflowUiModel(
    val label: String,
    val budget: BigDecimal,
    val outflow: BigDecimal
)
