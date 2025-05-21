package dev.pandesal.sbp.domain.service

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class ExchangeRateService @Inject constructor() {
    @Serializable
    private data class Response(val rates: Map<String, Float>)

    suspend fun fetchRate(from: String, to: String): Float {
        val url = URL("https://api.frankfurter.dev/v1/latest?from=$from&to=$to")
        val connection = url.openConnection() as HttpURLConnection
        return connection.inputStream.use { stream ->
            val text = stream.bufferedReader().readText()
            val resp = Json.decodeFromString<Response>(text)
            resp.rates[to] ?: 1f
        }
    }
}
