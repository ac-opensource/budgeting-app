package dev.pandesal.sbp.extensions

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
data class BigDecimalWrapper(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal
)

@Serializable
data class LocalDateWrapper(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate
)

class SerializersTest {
    @Test
    fun `big decimal round trip`() {
        val wrapper = BigDecimalWrapper(BigDecimal("1234.56"))
        val json = Json.encodeToString(wrapper)
        assertEquals("{\"value\":\"1234.56\"}", json)
        val back = Json.decodeFromString<BigDecimalWrapper>(json)
        assertEquals(wrapper, back)
    }

    @Test
    fun `local date round trip`() {
        val wrapper = LocalDateWrapper(LocalDate.of(2024, 1, 2))
        val json = Json.encodeToString(wrapper)
        assertEquals("{\"date\":\"2024-01-02\"}", json)
        val back = Json.decodeFromString<LocalDateWrapper>(json)
        assertEquals(wrapper, back)
    }
}
