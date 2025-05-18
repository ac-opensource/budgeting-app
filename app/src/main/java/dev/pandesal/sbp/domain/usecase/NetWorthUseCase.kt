package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.NetWorthRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class NetWorthUseCase @Inject constructor() {
    fun getCurrentNetWorth(): Flow<List<NetWorthRecord>> = flowOf(emptyList())
}
