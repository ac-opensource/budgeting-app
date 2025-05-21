package dev.pandesal.sbp.domain.model

import java.math.BigDecimal
import java.time.LocalDate

data class ReceiptData(
    val merchantName: String? = null,
    val amount: BigDecimal? = null,
    val date: LocalDate? = null
)
