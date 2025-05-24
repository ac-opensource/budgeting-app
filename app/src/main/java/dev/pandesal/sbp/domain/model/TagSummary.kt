package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class TagSummary(
    val tag: String,
    val total: BigDecimal
) : Parcelable
