package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Goal
import dev.pandesal.sbp.domain.repository.GoalRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GoalUseCase @Inject constructor(
    private val repository: GoalRepositoryInterface
) {
    fun getGoals(): Flow<List<Goal>> = repository.getGoals()

    suspend fun insertGoal(goal: Goal) = repository.insertGoal(goal)

    suspend fun deleteGoal(goal: Goal) = repository.deleteGoal(goal)
}
