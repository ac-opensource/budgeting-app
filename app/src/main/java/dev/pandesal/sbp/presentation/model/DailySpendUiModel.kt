package dev.pandesal.sbp.presentation.model

import java.math.BigDecimal

/** Model for daily spending amounts */
data class DailySpendUiModel(
    val entries: List<DailySpend>,
    val changeFromLastWeek: Double

)

data class DailySpend(
    val label: String,
    val amount: BigDecimal
)