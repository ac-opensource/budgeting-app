package dev.pandesal.sbp.extensions

import android.icu.text.CompactDecimalFormat
import android.os.Build
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Currency
import java.util.Locale

fun String.currencySymbol(): String = Currency.getInstance(this).symbol

fun BigDecimal.toLargeValueCurrency(
    currency: Currency,
    roundMode: RoundingMode = RoundingMode.HALF_UP
): String {
    return try {
        val formatted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val format = CompactDecimalFormat.getInstance(
                Locale.getDefault(),
                CompactDecimalFormat.CompactStyle.SHORT
            ).apply {
                maximumFractionDigits = 1
                minimumFractionDigits = 1
                roundingMode = roundMode.ordinal
            }
            format.format(this)
        } else {
            fallbackCompactFormat(roundMode)
        }

        "${currency.symbol} $formatted"
    } catch (e: Exception) {
        ""
    }
}

// Fallback formatter for pre-N devices
fun BigDecimal.fallbackCompactFormat(roundMode: RoundingMode): String {
    val suffixes = listOf(
        "" to BigDecimal(1),
        "k" to BigDecimal(1_000),
        "m" to BigDecimal(1_000_000),
        "b" to BigDecimal(1_000_000_000),
        "t" to BigDecimal(1_000_000_000_000),
        "q" to BigDecimal(1_000_000_000_000_000)
    )

    val absValue = this.abs()
    val (suffix, divisor) = suffixes.lastOrNull { absValue >= it.second } ?: suffixes.first()

    val divided = this.divide(divisor, 1, roundMode)
    val formatted = DecimalFormat("#.#").format(divided)

    return "$formatted$suffix"
}