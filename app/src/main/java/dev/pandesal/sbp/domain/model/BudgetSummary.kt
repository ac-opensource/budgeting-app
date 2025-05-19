package dev.pandesal.sbp.domain.model

/**
 * Summary of assigned and unassigned funds for zero-based budgeting.
 */
data class BudgetSummary(
    val assigned: Double,
    val unassigned: Double
)
