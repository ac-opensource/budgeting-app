package dev.pandesal.sbp.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

fun Double.format(): String = "%,.2f".format(this)
fun BigDecimal.format(): String {
    val nf = NumberFormat.getNumberInstance()
    nf.minimumFractionDigits = 2
    nf.maximumFractionDigits = 2
    return nf.format(this)
}
