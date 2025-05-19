package dev.pandesal.sbp.domain.model

import android.os.Parcelable
import dev.pandesal.sbp.extensions.BigDecimalSerializer
import dev.pandesal.sbp.extensions.LocalDateSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.time.LocalDate

@Serializable
@Parcelize
data class Goal(
    val id: Int = 0,
    val name: String,
    @Serializable(with = BigDecimalSerializer::class)
    val target: BigDecimal,
    @Serializable(with = BigDecimalSerializer::class)
    val current: BigDecimal = BigDecimal.ZERO,
    @Serializable(with = LocalDateSerializer::class)
    val dueDate: LocalDate? = null,
    val categoryId: Int? = null
) : Parcelable
