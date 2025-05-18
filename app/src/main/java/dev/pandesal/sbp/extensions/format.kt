package dev.pandesal.sbp.extensions

import java.math.BigDecimal

fun Double.format(): String = "%,.2f".format(this)
fun BigDecimal.format(): String = toDouble().format()
