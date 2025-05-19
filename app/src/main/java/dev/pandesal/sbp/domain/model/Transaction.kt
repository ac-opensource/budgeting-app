package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import dev.pandesal.sbp.extensions.BigDecimalSerializer
import dev.pandesal.sbp.extensions.LocalDateSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Serializable
@Parcelize
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val note: String? = null,
    val category: Category? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val amount: BigDecimal,
    @Serializable(with = LocalDateSerializer::class)
    val createdAt: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val updatedAt: LocalDate,
    val currency: String = "PHP",
    val tags: List<String> = emptyList(),
    val from: Int? = null,
    val fromAccountName: String? = null,
    val to: Int? = null,
    val toAccountName: String? = null,
    val merchantName: String? = null,
    val attachment: String? = null,
    val isDeleted: Boolean = false,
    val isArchived: Boolean = false,
    val location: String? = null,
    val transactionType: TransactionType
): Parcelable

@Parcelize
enum class TransactionType : Parcelable {
    INFLOW,
    OUTFLOW,
    TRANSFER,
    ADJUSTMENT
}