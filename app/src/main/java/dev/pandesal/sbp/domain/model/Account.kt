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
    val balance: BigDecimal = BigDecimal.ZERO
) : Parcelable

@Serializable
@Parcelize
enum class AccountType : Parcelable {
    CASH_WALLET,
    MOBILE_DIGITAL_WALLET,
    BANK_ACCOUNT,
    CREDIT_CARD
}
