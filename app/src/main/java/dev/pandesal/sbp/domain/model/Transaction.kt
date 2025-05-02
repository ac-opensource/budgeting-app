package dev.pandesal.sbp.domain.model

import java.time.LocalDate
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val categoryId: String,
    val amount: Double,
    val date: LocalDate,
    val note: String? = null,
    val accountId: String, // Track which account was used
    val transactionType: TransactionType
)

enum class TransactionType {
    INFLOW,
    OUTFLOW,
    TRANSFER,
    ADJUSTMENT
}