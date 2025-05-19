package dev.pandesal.sbp.local

import dev.pandesal.sbp.data.local.CategoryEntity
import dev.pandesal.sbp.data.local.CategoryGroupEntity
import dev.pandesal.sbp.data.local.GoalEntity
import dev.pandesal.sbp.data.local.toDomainModel
import dev.pandesal.sbp.data.local.toEntity
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.Goal
import dev.pandesal.sbp.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class MappingTests {
    @Test
    fun `category group mapping preserves flags`() {
        val group = CategoryGroup(
            id = 1,
            name = "Test",
            description = "desc",
            icon = "icon",
            weight = 5,
            isSystemSet = true,
            isFavorite = true,
            isArchived = true
        )

        val entity = group.toEntity()
        val back = entity.toDomainModel()

        assertEquals(group.isArchived, entity.isArchived)
        assertEquals(group.isSystemSet, entity.isSystemSet)
        assertEquals(group.isArchived, back.isArchived)
        assertEquals(group.isSystemSet, back.isSystemSet)
    }

    @Test
    fun `category mapping preserves flags`() {
        val category = Category(
            id = 1,
            name = "Test",
            description = "desc",
            icon = "icon",
            categoryGroupId = 2,
            categoryType = TransactionType.OUTFLOW,
            weight = 1,
            isSystemSet = true,
            isFavorite = true,
            isArchived = true
        )

        val entity = category.toEntity()
        val back = entity.toDomainModel()

        assertEquals(category.isArchived, entity.isArchived)
        assertEquals(category.isSystemSet, entity.isSystemSet)
        assertEquals(category.isArchived, back.isArchived)
        assertEquals(category.isSystemSet, back.isSystemSet)
    }

    @Test
    fun `goal mapping round trips`() {
        val goal = Goal(
            id = 1,
            name = "Trip",
            target = BigDecimal.TEN,
            current = BigDecimal.ONE,
            dueDate = LocalDate.now()
        )

        val entity = goal.toEntity()
        val back = entity.toDomainModel()

        assertEquals(goal, back)
    }

    @Test
    fun `recurring transaction mapping round trips`() {
        val domain = dev.pandesal.sbp.domain.model.RecurringTransaction(
            transaction = dev.pandesal.sbp.domain.model.Transaction(
                name = "Bill",
                amount = BigDecimal.TEN,
                createdAt = LocalDate.now(),
                updatedAt = LocalDate.now(),
                accountId = "1",
                transactionType = TransactionType.OUTFLOW
            ),
            interval = dev.pandesal.sbp.domain.model.RecurringInterval.MONTHLY,
            cutoffDays = 15,
            startDate = LocalDate.now()
        )

        val entity = domain.toEntity()
        val back = entity.toDomainModel()

        assertEquals(domain.interval, back.interval)
        assertEquals(domain.transaction.name, back.transaction.name)
        assertEquals(domain.cutoffDays, back.cutoffDays)
    }
}
