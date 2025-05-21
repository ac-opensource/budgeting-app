package dev.pandesal.sbp.presentation.model

import java.math.BigDecimal

data class AccountSummaryUiModel(
    val name: String,
    val balance: BigDecimal,
    val isSpendingWallet: Boolean,
    val isFundingWallet: Boolean,
    val currency: String
)
