package dev.pandesal.sbp.presentation.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.zIndex
import dev.pandesal.sbp.presentation.model.CalendarEvent
import dev.pandesal.sbp.presentation.model.CalendarEventType
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarView(
    events: List<CalendarEvent>,
    month: YearMonth,
    selectedDate: LocalDate?,
    onMonthChange: (YearMonth) -> Unit,
    onDateClick: (LocalDate, IntOffset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val monthStart = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()
    val firstDayOffset = monthStart.dayOfWeek.ordinal
    val grouped = events.groupBy { it.date }

    val scope = rememberCoroutineScope()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CardDefaults.shape,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Transaction Calendar", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onMonthChange(month.minusMonths(1)) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
                Text(
                    text = month.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + month.year,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = { onMonthChange(month.plusMonths(1)) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DayOfWeek.entries.forEach { dow ->
                    Text(dow.name.take(3), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

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
                            val bring = BringIntoViewRequester()
                            var coords: LayoutCoordinates? = null
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .bringIntoViewRequester(bring)
                                    .onGloballyPositioned { coords = it }
                                    .clickable {
                                        scope.launch { bring.bringIntoView() }
                                        val pos = coords?.positionInRoot() ?: Offset.Zero
                                        onDateClick(date, IntOffset(pos.x.toInt(), pos.y.toInt()))
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val isToday = date == LocalDate.now()
                                val isSelected = selectedDate == date
                                Box(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.primaryContainer
                                                isToday -> MaterialTheme.colorScheme.secondaryContainer
                                                else -> Color.Transparent
                                            },
                                            CircleShape
                                        )
                                        .border(
                                            width = if (isSelected) 1.dp else 0.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        day.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center
                                    )
                                }
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
                            Box(Modifier.weight(1f))
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
