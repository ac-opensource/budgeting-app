package dev.pandesal.sbp.presentation.categories

import android.util.Log
import androidx.collection.mutableIntListOf
import androidx.collection.mutableIntSetOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val useCase: CategoryUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<CategoriesUiState> = MutableStateFlow(CategoriesUiState.Initial)
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                useCase.getCategoryGroups(),
                useCase.getCategoriesWithLatestBudget()
            ) { groups, categories ->
                val filtered = categories.filter { it.category.categoryType != TransactionType.INFLOW }
                CategoriesUiState.Success(
                    categoryGroups = groups.filter { it.name.lowercase() != "inflow" && it.name.lowercase() != "transfers" },
                    categoriesWithBudget = filtered,
                    showTemplatePrompt = categories.none { it.category.categoryType == TransactionType.OUTFLOW }
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun createCategoryGroup(name: String) {
        viewModelScope.launch {
            val group = CategoryGroup(
                name = name,
                isFavorite = false,
                description = "",
                icon = ""
            )
            useCase.insertCategoryGroup(group)
        }
    }

    fun reorderGroup(from: Int, to: Int) {
        viewModelScope.launch {
            val current = uiState.value
            if (current is CategoriesUiState.Success) {
                val updatedGroups = current.categoryGroups.toMutableList().apply {
                    add(to, removeAt(from))
                }.mapIndexed { index, group ->
                    group.copy(weight = index)
                }

                updatedGroups.forEach { group ->
                    useCase.insertCategoryGroup(group)
                }
            }
        }
    }

    fun reorderCategory(from: Int, to: Int, groupId: Int) {
        viewModelScope.launch {
            val current = uiState.value
            if (current is CategoriesUiState.Success) {
                val filtered = current.categoriesWithBudget.filter { it.category.categoryGroupId == groupId }.map { it.category }
                val other = current.categoriesWithBudget.filterNot { it.category.categoryGroupId == groupId }.map { it.category }

                val updated = filtered.toMutableList().apply {
                    add(to, removeAt(from))
                }.mapIndexed { index, category ->
                    category.copy(weight = index)
                }

                (updated + other).forEach { category ->
                    useCase.insertCategory(category)
                }
            }
        }
    }


    fun createCategory(name: String, groupId: Int) {
        viewModelScope.launch {
            val category = Category(
                name = name,
                categoryGroupId = groupId,
                isFavorite = false,
                description = "",
                icon = "",
                categoryType = TransactionType.OUTFLOW,
                weight = 0
            )
            useCase.insertCategory(category)
        }
    }

    fun setBudgetForCategory(categoryId: Int, targetAmount: BigDecimal, yearMonth: YearMonth = YearMonth.now()) {
        viewModelScope.launch {
            val budget = MonthlyBudget(
                categoryId = categoryId,
                month = yearMonth,
                allocated = targetAmount,
                spent = BigDecimal.ZERO // assume zero initially when allocating
            )
            useCase.insertMonthlyBudget(budget)
        }
    }

    fun updateCategoryGroup(group: CategoryGroup, name: String) {
        viewModelScope.launch {
            useCase.insertCategoryGroup(group.copy(name = name))
        }
    }

    fun deleteCategoryGroup(group: CategoryGroup) {
        viewModelScope.launch {
            useCase.deleteCategoryGroup(group)
        }
    }

    fun updateCategory(category: Category, name: String) {
        viewModelScope.launch {
            useCase.insertCategory(category.copy(name = name))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            useCase.deleteCategory(category)
        }
    }

    fun seedDefaultTemplate() {
        viewModelScope.launch {
            useCase.seedDefaultOutflow()
            dismissTemplatePrompt()
        }
    }

    fun dismissTemplatePrompt() {
        val current = _uiState.value
        if (current is CategoriesUiState.Success) {
            _uiState.value = current.copy(showTemplatePrompt = false)
        }
    }


}
