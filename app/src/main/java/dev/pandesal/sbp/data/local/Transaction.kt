package dev.pandesal.sbp.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val note: String? = null,
    @Embedded(prefix = "category_")
    val category: CategoryEntity? = null,
    val amount: BigDecimal,
    val createdAt: String,
    val updatedAt: String,
    val currency: String = "PHP",
    val tags: List<String> = emptyList(),
    val from: String? = null,
    val to: String? = null,
    val merchantName: String? = null,
    val attachment: String? = null,
    val isDeleted: Boolean = false,
    val isArchived: Boolean = false,
    val location: String? = null,
    val accountId: String,
    val transactionType: String
)

fun TransactionEntity.toDomainModel(): Transaction {
    return Transaction(
        id = id,
        name = name,
        note = note,
        category = category?.toDomainModel(),
        amount = amount,
        createdAt = LocalDate.parse(createdAt),
        updatedAt = LocalDate.parse(updatedAt),
        accountId = accountId,
        transactionType = TransactionType.valueOf(transactionType),
        currency = currency,
        tags = tags,
        from = from,
        to = to,
        merchantName = merchantName,
        attachment = attachment,
        isDeleted = isDeleted,
        isArchived = isArchived,
        location = location
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        name = name,
        note = note,
        category = category?.toEntity(),
        amount = amount,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
        currency = currency,
        tags = tags,
        from = from,
        to = to,
        merchantName = merchantName,
        attachment = attachment,
        isDeleted = isDeleted,
        isArchived = isArchived,
        location = location,
        accountId = accountId,
        transactionType = transactionType.name
    )
}