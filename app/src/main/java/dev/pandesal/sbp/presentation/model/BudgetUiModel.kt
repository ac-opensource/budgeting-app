package dev.pandesal.sbp.presentation.model

import java.math.BigDecimal

data class BudgetCategoryUiModel(
    val name: String,
    val allocated: BigDecimal,
    val spent: BigDecimal,
    val currency: String
)