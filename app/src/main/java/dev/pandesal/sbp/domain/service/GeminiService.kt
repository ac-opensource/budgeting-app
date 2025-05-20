package dev.pandesal.sbp.domain.service

import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {
    fun parseSms(text: String): Transaction {
        val amountRegex = Regex("(\d+[.,]?\d*)")
        val amountText = amountRegex.find(text)?.value?.replace(",", "") ?: "0"
        val amount = amountText.toBigDecimalOrNull() ?: BigDecimal.ZERO
        return Transaction(
            name = text.take(20),
            amount = amount,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
            transactionType = if (text.contains("deposit", true) || text.contains("credited", true) || text.contains("received", true)) {
                TransactionType.INFLOW
            } else {
                TransactionType.OUTFLOW
            }
        )
    }
}
