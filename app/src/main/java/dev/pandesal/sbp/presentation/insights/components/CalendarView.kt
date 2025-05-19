package dev.pandesal.sbp.presentation.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.pandesal.sbp.presentation.model.CalendarEvent
import dev.pandesal.sbp.presentation.model.CalendarEventType
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun CalendarView(
    events: List<CalendarEvent>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val monthStart = today.withDayOfMonth(1)
    val daysInMonth = today.lengthOfMonth()
    val firstDayOffset = (monthStart.dayOfWeek.value % 7)
    val grouped = events.groupBy { it.date }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardDefaults.shape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DayOfWeek.values().forEach { dow ->
                    Text(dow.name.take(3))
                }
            }
            for (week in 0..5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (dow in 0..6) {
                        val day = week * 7 + dow - firstDayOffset + 1
                        if (day in 1..daysInMonth) {
                            val date = monthStart.plusDays((day - 1).toLong())
                            val dayEvents = grouped[date].orEmpty()
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(day.toString(), style = MaterialTheme.typography.bodySmall)
                                Row {
                                    dayEvents.forEach { event ->
                                        Box(
                                            modifier = Modifier
                                                .padding(1.dp)
                                                .background(colorFor(event.type), CircleShape)
                                                .padding(3.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Box(Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun colorFor(type: CalendarEventType): Color = when (type) {
    CalendarEventType.INFLOW -> Color(0xFF2E7D32)
    CalendarEventType.OUTFLOW -> Color(0xFFC62828)
    CalendarEventType.BILL -> Color.Gray
}
