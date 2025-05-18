package dev.pandesal.sbp.extensions

import dev.pandesal.sbp.domain.model.AccountType

fun AccountType.label(): String = name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercaseChar() }
