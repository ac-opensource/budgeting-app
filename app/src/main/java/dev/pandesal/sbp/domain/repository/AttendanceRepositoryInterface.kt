package dev.pandesal.sbp.domain.repository

import dev.pandesal.sbp.domain.model.AttendanceRecord
import java.time.LocalTime
import kotlinx.coroutines.flow.Flow

interface AttendanceRepositoryInterface {
    fun getRecords(): Flow<List<AttendanceRecord>>
    suspend fun markPresent(name: String, time: LocalTime = LocalTime.now())
}
