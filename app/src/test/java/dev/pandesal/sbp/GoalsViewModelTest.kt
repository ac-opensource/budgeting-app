package dev.pandesal.sbp

import dev.pandesal.sbp.domain.model.Goal
import dev.pandesal.sbp.domain.usecase.GoalUseCase
import dev.pandesal.sbp.fakes.FakeGoalRepository
import dev.pandesal.sbp.presentation.goals.GoalsUiState
import dev.pandesal.sbp.presentation.goals.GoalsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class GoalsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = FakeGoalRepository()
    private val useCase = GoalUseCase(repository)

    @Test
    fun uiStateEmitsGoals() = runTest {
        val goal = Goal(name = "Travel", target = BigDecimal.TEN)
        repository.goalsFlow.value = listOf(goal)

        val vm = GoalsViewModel(useCase)
        advanceUntilIdle()

        val state = vm.uiState.value as GoalsUiState.Success
        assertEquals(listOf(goal), state.goals)
    }

    @Test
    fun addGoalInsertsGoal() = runTest {
        val vm = GoalsViewModel(useCase)
        vm.addGoal("Save", BigDecimal.ONE)
        advanceUntilIdle()
        assertEquals(1, repository.insertedGoals.size)
        assertEquals("Save", repository.insertedGoals[0].name)
    }
}
