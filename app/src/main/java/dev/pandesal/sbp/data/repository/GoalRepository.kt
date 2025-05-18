package dev.pandesal.sbp.data.repository

import dev.pandesal.sbp.data.dao.GoalDao
import dev.pandesal.sbp.data.local.toDomainModel
import dev.pandesal.sbp.data.local.toEntity
import dev.pandesal.sbp.domain.model.Goal
import dev.pandesal.sbp.domain.repository.GoalRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepository @Inject constructor(
    private val dao: GoalDao
) : GoalRepositoryInterface {

    override fun getGoals(): Flow<List<Goal>> =
        dao.getGoals().map { it.map { entity -> entity.toDomainModel() } }

    override suspend fun insertGoal(goal: Goal) =
        dao.insert(goal.toEntity())

    override suspend fun deleteGoal(goal: Goal) =
        dao.delete(goal.toEntity())
}
