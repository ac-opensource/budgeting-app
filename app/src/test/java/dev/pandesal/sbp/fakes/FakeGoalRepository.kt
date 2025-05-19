package dev.pandesal.sbp.fakes

import dev.pandesal.sbp.domain.model.Goal
import dev.pandesal.sbp.domain.repository.GoalRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeGoalRepository : GoalRepositoryInterface {
    val goalsFlow = MutableStateFlow<List<Goal>>(emptyList())
    val insertedGoals = mutableListOf<Goal>()

    override fun getGoals(): Flow<List<Goal>> = goalsFlow

    override suspend fun insertGoal(goal: Goal) {
        insertedGoals.add(goal)
    }

    override suspend fun deleteGoal(goal: Goal) { /* no-op */ }
}
