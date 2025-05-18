package dev.pandesal.sbp.local

import dev.pandesal.sbp.data.local.CategoryEntity
import dev.pandesal.sbp.data.local.CategoryGroupEntity
import dev.pandesal.sbp.data.local.toDomainModel
import dev.pandesal.sbp.data.local.toEntity
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.CategoryGroup
import dev.pandesal.sbp.domain.model.TransactionType
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
}
