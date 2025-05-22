package dev.pandesal.sbp.presentation.model

/** Model for spending trends */
import java.math.BigDecimal

data class TrendUiModel(
    val label: String,
    val amount: BigDecimal,
    val isForecast: Boolean = false
)
