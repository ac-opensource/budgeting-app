package dev.pandesal.sbp.presentation.model

import java.time.LocalDate

enum class CalendarEventType {
    INFLOW,
    OUTFLOW,
    BILL
}

data class CalendarEvent(
    val date: LocalDate,
    val type: CalendarEventType
)
