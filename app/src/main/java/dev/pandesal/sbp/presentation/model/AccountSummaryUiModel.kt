package dev.pandesal.sbp.presentation.model

import dev.pandesal.sbp.domain.model.AccountType
import java.math.BigDecimal

data class AccountSummaryUiModel(
    val name: String,
    val balance: BigDecimal,
    val type: AccountType,
    val isSpendingWallet: Boolean,
    val isFundingWallet: Boolean,
    val currency: String
)
