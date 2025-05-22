package dev.pandesal.sbp.data.repository

import dev.pandesal.sbp.domain.model.AttendanceRecord
import dev.pandesal.sbp.domain.repository.AttendanceRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceRepository @Inject constructor() : AttendanceRepositoryInterface {
    private val _records = MutableStateFlow(
        listOf(
            AttendanceRecord(name = "Alice"),
            AttendanceRecord(name = "Bob"),
            AttendanceRecord(name = "Charlie")
        )
    )
    private val records = _records.asStateFlow()

    override fun getRecords(): Flow<List<AttendanceRecord>> = records

    override suspend fun markPresent(name: String, time: LocalTime) {
        val list = _records.value.toMutableList()
        val idx = list.indexOfFirst { it.name == name }
        val isLate = time.isAfter(LocalTime.of(9, 0))
        if (idx >= 0) {
            val rec = list[idx]
            list[idx] = rec.copy(
                checkIn = time,
                lateCount = rec.lateCount + if (isLate) 1 else 0
            )
        } else {
            list.add(
                AttendanceRecord(
                    name = name,
                    checkIn = time,
                    lateCount = if (isLate) 1 else 0
                )
            )
        }
        _records.value = list
    }
}
