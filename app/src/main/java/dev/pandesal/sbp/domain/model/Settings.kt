package dev.pandesal.sbp.domain.model

data class Settings(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val detectFinanceAppUsage: Boolean = false,
    val currency: String = "PHP",
    val isTravelMode: Boolean = false,
    val travelCurrency: String = "",
    val travelTag: String = "Travel",
    val exchangeRate: Float = 1f,
    val lastRateDate: String = ""
)
