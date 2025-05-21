package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.repository.TransactionRepositoryInterface
import java.time.YearMonth
import java.time.temporal.WeekFields
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrendUseCase @Inject constructor(
    private val repository: TransactionRepositoryInterface
) {

    fun getWeeklySpending(): Flow<List<Pair<String, Double>>> =
        repository.getAllTransactions().map { txs ->
            txs.filter { it.transactionType == TransactionType.OUTFLOW }
                .groupBy { Pair(it.createdAt.year, it.createdAt.get(WeekFields.ISO.weekOfWeekBasedYear())) }
                .toSortedMap(compareBy({ it.first }, { it.second }))
                .map { (pair, list) ->
                    "W${pair.second}" to list.sumOf { it.amount.toDouble() }
                }
        }

    fun getMonthlySpending(): Flow<List<Pair<String, Double>>> =
        repository.getAllTransactions().map { txs ->
            txs.filter { it.transactionType == TransactionType.OUTFLOW }
                .groupBy { YearMonth.from(it.createdAt) }
                .toSortedMap()
                .map { (month, list) ->
                    month.toString() to list.sumOf { it.amount.toDouble() }
                }
        }

    fun getYearlySpending(): Flow<List<Pair<String, Double>>> =
        repository.getAllTransactions().map { txs ->
            txs.filter { it.transactionType == TransactionType.OUTFLOW }
                .groupBy { it.createdAt.year }
                .toSortedMap()
                .map { (year, list) ->
                    year.toString() to list.sumOf { it.amount.toDouble() }
                }
        }

    fun forecastNextMonth(): Flow<Double> =
        getMonthlySpending().map { months ->
            if (months.isEmpty()) 0.0 else months.takeLast(3).map { it.second }.average()
        }
}
