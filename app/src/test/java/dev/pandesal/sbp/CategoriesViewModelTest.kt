package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.CategoryWithBudget
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.ZeroBasedBudgetUseCase
import dev.pandesal.sbp.fakes.FakeAccountRepository
import dev.pandesal.sbp.fakes.FakeCategoryRepository
import dev.pandesal.sbp.presentation.categories.CategoriesUiState
import dev.pandesal.sbp.presentation.categories.CategoriesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = FakeCategoryRepository()
    private val accountRepository = FakeAccountRepository()
    private val useCase = CategoryUseCase(repository)
    private val zeroBasedBudgetUseCase = ZeroBasedBudgetUseCase(accountRepository, repository)

    @Test
    fun uiStateEmitsGroupsAndCategories() = runTest {
        val group = CategoryGroup(id = 1, name = "G", description = "", icon = "")
        val category = Category(id = 1, name = "C", description = "", icon = "", categoryGroupId = 1, categoryType = TransactionType.OUTFLOW, weight = 0)
        val budget = MonthlyBudget(1,1,YearMonth.now(), BigDecimal.ZERO, BigDecimal.ZERO)
        repository.groupsFlow.value = listOf(group)
        repository.categoriesWithBudgetFlow.value = listOf(CategoryWithBudget(category, budget))

        val vm = CategoriesViewModel(useCase, zeroBasedBudgetUseCase)
        advanceUntilIdle()

        val state = vm.uiState.value as CategoriesUiState.Success
        assertEquals(listOf(group), state.categoryGroups)
        assertEquals(1, state.categoriesWithBudget.size)
    }

    @Test
    fun createCategoryGroupInsertsGroup() = runTest {
        val vm = CategoriesViewModel(useCase, zeroBasedBudgetUseCase)
        vm.createCategoryGroup("New")
        advanceUntilIdle()
        assertEquals("New", repository.insertedGroups[0].name)
    }
}
