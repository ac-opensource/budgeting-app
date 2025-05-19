package dev.pandesal.sbp.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pandesal.sbp.domain.model.RecurringInterval
import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

@Entity(tableName = "recurring_transactions")
data class RecurringTransactionEntity(
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
    val transactionType: String,
    val interval: String,
    val cutoffDays: Int = 21,
    val startDate: String,
)

fun RecurringTransactionEntity.toDomainModel(): RecurringTransaction {
    val transaction = Transaction(
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
        location = location,
    )
    return RecurringTransaction(
        transaction = transaction,
        interval = RecurringInterval.valueOf(interval),
        cutoffDays = cutoffDays,
        startDate = LocalDate.parse(startDate)
    )
}

fun RecurringTransaction.toEntity(): RecurringTransactionEntity {
    return RecurringTransactionEntity(
        id = transaction.id,
        name = transaction.name,
        note = transaction.note,
        category = transaction.category?.toEntity(),
        amount = transaction.amount,
        createdAt = transaction.createdAt.toString(),
        updatedAt = transaction.updatedAt.toString(),
        currency = transaction.currency,
        tags = transaction.tags,
        from = transaction.from,
        to = transaction.to,
        merchantName = transaction.merchantName,
        attachment = transaction.attachment,
        isDeleted = transaction.isDeleted,
        isArchived = transaction.isArchived,
        location = transaction.location,
        accountId = transaction.accountId,
        transactionType = transaction.transactionType.name,
        interval = interval.name,
        cutoffDays = cutoffDays,
        startDate = startDate.toString(),
    )
}
