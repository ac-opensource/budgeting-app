package dev.pandesal.sbp.presentation.model

/** UI model representing budget summary used in the home screen. */
import java.math.BigDecimal

data class BudgetSummaryUiModel(
    val assigned: BigDecimal,
    val unassigned: BigDecimal
)
