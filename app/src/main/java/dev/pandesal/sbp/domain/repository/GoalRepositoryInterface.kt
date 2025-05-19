package dev.pandesal.sbp.domain.repository

import dev.pandesal.sbp.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepositoryInterface {
    fun getGoals(): Flow<List<Goal>>
    suspend fun insertGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
}
