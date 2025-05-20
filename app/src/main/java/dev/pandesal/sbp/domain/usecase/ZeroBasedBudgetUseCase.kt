package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.BudgetSummary
import dev.pandesal.sbp.domain.repository.AccountRepositoryInterface
import dev.pandesal.sbp.domain.repository.CategoryRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth
import javax.inject.Inject

/**
 * Provides zero-based budgeting summaries for a given month.
 */
class ZeroBasedBudgetUseCase @Inject constructor(
    private val accountRepository: AccountRepositoryInterface,
    private val categoryRepository: CategoryRepositoryInterface
) {
    fun getBudgetSummary(month: YearMonth = YearMonth.now()): Flow<BudgetSummary> =
        combine(
            categoryRepository.getMonthlyBudgetsByYearMonth(month),
            accountRepository.getAccounts()
        ) { budgets, accounts ->
            val assigned = budgets.sumOf { it.allocated.toDouble() }
            val spent = budgets.sumOf { it.spent.toDouble() }
            val totalFunds = accounts.sumOf { it.balance.toDouble() }
            BudgetSummary(
                assigned = assigned,
                unassigned = totalFunds + spent - assigned
            )
        }
}
