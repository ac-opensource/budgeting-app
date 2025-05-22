package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.Settings
import dev.pandesal.sbp.domain.repository.SettingsRepositoryInterface
import dev.pandesal.sbp.domain.repository.TransactionRepositoryInterface
import dev.pandesal.sbp.domain.service.ExchangeRateService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class TravelModeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepositoryInterface,
    private val transactionRepository: TransactionRepositoryInterface,
    private val exchangeRateService: ExchangeRateService
) {
    fun getSettings(): Flow<Settings> = settingsRepository.getSettings()

    suspend fun setTravelMode(enabled: Boolean) =
        settingsRepository.setTravelMode(enabled)

    suspend fun setTravelCurrency(currency: String) =
        settingsRepository.setTravelCurrency(currency)

    suspend fun setTravelTag(tag: String) =
        settingsRepository.setTravelTag(tag)

    suspend fun refreshRateIfNeeded() {
        val settings = settingsRepository.getSettings().first()
        val today = LocalDate.now().toString()
        if (settings.isTravelMode && settings.lastRateDate != today && settings.travelCurrency.isNotBlank()) {
            val rate = exchangeRateService.fetchRate(settings.travelCurrency, settings.currency)
            settingsRepository.setExchangeRate(rate, today)
        }
    }

    fun travelSpendHome(): Flow<BigDecimal> = combine(
        settingsRepository.getSettings(),
        transactionRepository.getAllTransactions()
    ) { settings, transactions ->
        val total = transactions.filter { it.tags.contains(settings.travelTag) }
            .fold(BigDecimal.ZERO) { acc, tx -> acc + tx.amount }
        total.multiply(BigDecimal(settings.exchangeRate.toString()))
    }
}
