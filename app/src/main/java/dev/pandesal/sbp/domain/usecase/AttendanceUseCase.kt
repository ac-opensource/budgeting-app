package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.AttendanceRecord
import dev.pandesal.sbp.domain.repository.AttendanceRepositoryInterface
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime
import javax.inject.Inject

class AttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepositoryInterface
) {
    fun getRecords(): Flow<List<AttendanceRecord>> = repository.getRecords()

    suspend fun markPresent(name: String, time: LocalTime = LocalTime.now()) {
        repository.markPresent(name, time)
    }
}
