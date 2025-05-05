package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

@Parcelize
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val categoryId: String,
    val amount: Double,
    val date: LocalDate,
    val note: String? = null,
    val accountId: String, // Track which account was used
    val transactionType: TransactionType
): Parcelable

@Parcelize
enum class TransactionType : Parcelable {
    INFLOW,
    OUTFLOW,
    TRANSFER,
    ADJUSTMENT
}