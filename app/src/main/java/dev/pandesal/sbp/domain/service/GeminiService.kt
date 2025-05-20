package dev.pandesal.sbp.domain.service

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dev.pandesal.sbp.BuildConfig
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.domain.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@Singleton
class GeminiService @Inject constructor() {
    private val model = GenerativeModel(
        modelName = "models/gemini-pro",
        apiKey = BuildConfig.GEMINI_API_KEY
    )
    private val json = Json { ignoreUnknownKeys = true }
    private val systemPrompt = """
        Extract the transaction amount and determine whether it is an inflow or outflow.
        Respond only with a JSON object matching the Transaction data class.
    """.trimIndent()

    suspend fun parseSms(text: String): Transaction {
        return try {
            val response = model.generateContent(content(systemPrompt + "\n" + text))
            val result = response.text ?: return fallbackParse(text)
            json.decodeFromString(result)
        } catch (_: Exception) {
            fallbackParse(text)
        }
    }

    private fun fallbackParse(text: String): Transaction {
        val amountRegex = Regex("(\\d+[.,]?\\d*)")
        val amountText = amountRegex.find(text)?.value?.replace(",", "") ?: "0"
        val amount = amountText.toBigDecimalOrNull() ?: BigDecimal.ZERO
        return Transaction(
            name = text.take(20),
            amount = amount,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now(),
            transactionType = if (
                text.contains("deposit", true) ||
                text.contains("credited", true) ||
                text.contains("received", true)
            ) {
                TransactionType.INFLOW
            } else {
                TransactionType.OUTFLOW
            }
        )
    }
}
