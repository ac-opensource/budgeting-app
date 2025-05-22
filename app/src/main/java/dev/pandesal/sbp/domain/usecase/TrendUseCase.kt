package dev.pandesal.sbp.domain.usecase

import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.repository.TransactionRepositoryInterface
import java.math.BigDecimal
import java.math.MathContext
import java.time.YearMonth
import java.time.temporal.WeekFields
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TrendUseCase @Inject constructor(
    private val repository: TransactionRepositoryInterface
) {

    fun getWeeklySpending(): Flow<List<Pair<String, BigDecimal>>> =
        repository.getAllTransactions().map { txs ->
            txs.filter { it.transactionType == TransactionType.OUTFLOW }
                .groupBy { Pair(it.createdAt.year, it.createdAt.get(WeekFields.ISO.weekOfWeekBasedYear())) }
                .toSortedMap(compareBy({ it.first }, { it.second }))
                .map { (pair, list) ->
                    "W${pair.second}" to list.fold(BigDecimal.ZERO) { acc, tx -> acc + tx.amount }
                }
        }

    fun getMonthlySpending(): Flow<List<Pair<String, BigDecimal>>> =
        repository.getAllTransactions().map { txs ->
            txs.filter { it.transactionType == TransactionType.OUTFLOW }
                .groupBy { YearMonth.from(it.createdAt) }
                .toSortedMap()
                .map { (month, list) ->
                    month.toString() to list.fold(BigDecimal.ZERO) { acc, tx -> acc + tx.amount }
                }
        }

    fun getYearlySpending(): Flow<List<Pair<String, BigDecimal>>> =
        repository.getAllTransactions().map { txs ->
            txs.filter { it.transactionType == TransactionType.OUTFLOW }
                .groupBy { it.createdAt.year }
                .toSortedMap()
                .map { (year, list) ->
                    year.toString() to list.fold(BigDecimal.ZERO) { acc, tx -> acc + tx.amount }
                }
        }

    fun forecastNextMonth(): Flow<BigDecimal> =
        getMonthlySpending().map { months ->
            if (months.isEmpty()) BigDecimal.ZERO
            else {
                val recent = months.takeLast(3).map { it.second }
                recent.fold(BigDecimal.ZERO) { acc, amt -> acc + amt }
                    .divide(BigDecimal.valueOf(recent.size.toLong()), MathContext.DECIMAL64)
            }
        }
}
