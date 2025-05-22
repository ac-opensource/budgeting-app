package dev.pandesal.sbp.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Currency

fun BigDecimal.toLargeValueCurrency(currency: Currency): String {
    val symbol = currency.symbol
    val abs = this.abs()
    val sign = if (this < BigDecimal.ZERO) "-" else ""
    val thousand = BigDecimal(1_000)
    val million = BigDecimal(1_000_000)
    val billion = BigDecimal(1_000_000_000)

    val value = when {
        abs >= billion -> abs.divide(billion, 1, RoundingMode.HALF_UP).toPlainString() + "B"
        abs >= million -> abs.divide(million, 1, RoundingMode.HALF_UP).toPlainString() + "M"
        abs >= thousand -> abs.divide(thousand, 1, RoundingMode.HALF_UP).toPlainString() + "K"
        else -> {
            val nf = NumberFormat.getNumberInstance()
            nf.minimumFractionDigits = 2
            nf.maximumFractionDigits = 2
            nf.format(abs)
        }
    }
    return sign + symbol + value
}
