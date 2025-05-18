package dev.pandesal.sbp.domain.model

data class Settings(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val currency: String = "PHP"
)
