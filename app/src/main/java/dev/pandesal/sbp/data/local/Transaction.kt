package dev.pandesal.sbp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pandesal.sbp.domain.model.Category
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val amount: BigDecimal,
    val date: String,
    val note: String? = null,
    val accountId: String,
    val transactionType: String
)

fun TransactionEntity.toDomainModel(): Transaction {
    return Transaction(
        id = id,
        categoryId = categoryId,
        amount = amount,
        date = LocalDate.parse(date),
        note = note,
        accountId = accountId,
        transactionType = TransactionType.valueOf(transactionType)
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        categoryId = categoryId,
        amount = amount,
        date = date.toString(),
        note = note,
        accountId = accountId,
        transactionType = transactionType.name
    )
}