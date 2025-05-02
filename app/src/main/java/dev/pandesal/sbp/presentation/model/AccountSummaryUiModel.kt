package dev.pandesal.sbp.presentation.model

data class AccountSummaryUiModel(
    val name: String,
    val balance: Double,
    val isSpendingWallet: Boolean,
    val isFundingWallet: Boolean
)
