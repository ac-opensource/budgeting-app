package dev.pandesal.sbp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.AccountType
import java.math.BigDecimal

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val balance: BigDecimal = BigDecimal.ZERO,
    val currency: String = "PHP",
    val contractValue: BigDecimal? = null,
    val monthlyPayment: BigDecimal? = null,
    val startDate: String? = null,
    val endDate: String? = null,
)

fun AccountEntity.toDomainModel(): Account {
    return Account(
        id = id,
        name = name,
        type = AccountType.valueOf(type),
        balance = balance,
        currency = currency,
        contractValue = contractValue,
        monthlyPayment = monthlyPayment,
        startDate = startDate,
        endDate = endDate,
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        name = name,
        type = type.name,
        balance = balance,
        currency = currency,
        contractValue = contractValue,
        monthlyPayment = monthlyPayment,
        startDate = startDate,
        endDate = endDate,
    )
}
