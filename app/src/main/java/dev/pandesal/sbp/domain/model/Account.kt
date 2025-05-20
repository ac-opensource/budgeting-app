package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import dev.pandesal.sbp.extensions.BigDecimalSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
@Parcelize
data class Account(
    val id: Int = 0,
    val name: String,
    val type: AccountType,
    @Serializable(with = BigDecimalSerializer::class)
    val balance: BigDecimal = BigDecimal.ZERO,
    val currency: String = "PHP",
    @Serializable(with = BigDecimalSerializer::class)
    val contractValue: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val monthlyPayment: BigDecimal? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val creditLimit: BigDecimal? = null,
    val lenderType: LenderType? = null,
    val startDate: String? = null,
    val endDate: String? = null,
) : Parcelable

@Serializable
@Parcelize
enum class AccountType : Parcelable {
    CASH_WALLET,
    MOBILE_DIGITAL_WALLET,
    BANK_ACCOUNT,
    CREDIT_CARD,
    LOAN_FOR_ASSET,
    LOAN_FOR_SPENDING,
}

@Serializable
@Parcelize
enum class LenderType : Parcelable {
    INSTITUTION,
    PERSON,
}

