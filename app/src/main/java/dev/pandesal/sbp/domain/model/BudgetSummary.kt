package dev.pandesal.sbp.domain.model

/**
 * Summary of assigned and unassigned funds for zero-based budgeting.
 */
import java.math.BigDecimal

data class BudgetSummary(
    val assigned: BigDecimal,
    val unassigned: BigDecimal
)
