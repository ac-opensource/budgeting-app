package dev.pandesal.sbp.extensions

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.format(): String = "%,.2f".format(this)
fun BigDecimal.format(): String = setScale(2, RoundingMode.HALF_EVEN).toPlainString()
