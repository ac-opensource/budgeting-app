package dev.pandesal.sbp.extensions

import java.util.Currency

fun String.currencySymbol(): String = Currency.getInstance(this).symbol
