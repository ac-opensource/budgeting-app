package dev.pandesal.sbp.presentation.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.pandesal.sbp.domain.model.Account
import dev.pandesal.sbp.domain.model.AccountType
import dev.pandesal.sbp.domain.model.MonthlyBudget
import dev.pandesal.sbp.domain.model.TransactionType
import dev.pandesal.sbp.domain.usecase.AccountUseCase
import dev.pandesal.sbp.domain.usecase.CategoryUseCase
import dev.pandesal.sbp.domain.usecase.TransactionUseCase
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import dev.pandesal.sbp.domain.usecase.ReminderUseCase
import dev.pandesal.sbp.domain.model.RecurringTransaction
import dev.pandesal.sbp.domain.model.Reminder
import dev.pandesal.sbp.domain.model.Transaction
import dev.pandesal.sbp.presentation.model.BudgetOutflowUiModel
import dev.pandesal.sbp.presentation.model.CashflowUiModel
import dev.pandesal.sbp.presentation.model.CalendarEvent
import dev.pandesal.sbp.presentation.model.CalendarEventType
import dev.pandesal.sbp.presentation.model.NetWorthBarGroup
import dev.pandesal.sbp.presentation.model.NetWorthUiModel
import dev.pandesal.sbp.domain.model.TagSummary
import dev.pandesal.sbp.presentation.insights.TimePeriod
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.WeekFields
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val transactionUseCase: TransactionUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val accountUseCase: AccountUseCase,
    private val recurringUseCase: RecurringTransactionUseCase,
    private val reminderUseCase: ReminderUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<InsightsUiState>(InsightsUiState.Initial)
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    private val _period = MutableStateFlow(TimePeriod.MONTHLY)
    val period: StateFlow<TimePeriod> = _period.asStateFlow()

    private val _tooltipState = MutableStateFlow<DayTooltipUiState>(DayTooltipUiState.Loading)
    val tooltipState: StateFlow<DayTooltipUiState> = _tooltipState.asStateFlow()

    private val _calendarMonth = MutableStateFlow(YearMonth.now())
    val calendarMonth: StateFlow<YearMonth> = _calendarMonth.asStateFlow()

    private val _tagSummary = MutableStateFlow<List<TagSummary>>(emptyList())
    val tagSummary: StateFlow<List<TagSummary>> = _tagSummary.asStateFlow()

    init {
        observeData()
        viewModelScope.launch {
            transactionUseCase.getTotalAmountByTag(TransactionType.OUTFLOW).collect {
                _tagSummary.value = it
            }
        }
    }

    fun setPeriod(period: TimePeriod) {
        _period.value = period
    }

    fun setCalendarMonth(month: YearMonth) {
        _calendarMonth.value = month
    }

    private fun observeData() {
        _uiState.value = InsightsUiState.Loading
        viewModelScope.launch {
            combine(
                transactionUseCase.getAllTransactions(),
                categoryUseCase.getMonthlyBudgetsByYearMonth(YearMonth.now()),
                accountUseCase.getAccounts(),
                recurringUseCase.getRecurringTransactions(),
                reminderUseCase.getReminders(),
                _calendarMonth
            ) { args ->

                val transactions = args[0] as List<Transaction>
                val budgets = args[1] as List<MonthlyBudget>
                val accounts = args[2] as List<Account>
                val recurring = args[3] as List<RecurringTransaction>
                val reminders = args[4] as List<Reminder>
                val month = args[5] as YearMonth

                val cashflowByPeriod = TimePeriod.values().associateWith { p ->
                    groupTransactionsByPeriod(transactions, p)
                }

                val budgetByPeriod = TimePeriod.values().associateWith { p ->
                    val ranges = buildRanges(p)

                    val entries = ranges.map { r ->
                        val outflow = transactions
                            .filter {
                                it.transactionType == TransactionType.OUTFLOW &&
                                    !it.createdAt.isBefore(r.start) &&
                                    !it.createdAt.isAfter(r.end)
                            }
                            .fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }

                        val allocated = budgets
                            .filter {
                                        !it.month.atDay(r.start.dayOfMonth) .isBefore(r.start) &&
                                        !it.month.atDay(r.end.dayOfMonth).isAfter(r.end)
                            }
                            .fold(BigDecimal.ZERO) { acc, b -> acc + b.allocated }
                        BudgetOutflowUiModel(r.label, allocated, outflow)
                    }
                    if (p == TimePeriod.DAILY || p == TimePeriod.WEEKLY) {
                        entries.filter { it.budget != BigDecimal.ZERO || it.outflow != BigDecimal.ZERO }
                    } else {
                        entries
                    }
                }

                val netWorthByPeriod = TimePeriod.values().associateWith { p ->
                    buildNetWorthByRanges(transactions, accounts, buildRanges(p))
                }

                val monthStart = month.atDay(1)
                val monthEnd = month.atEndOfMonth()

                val calendarEvents = transactions
                    .filter { !it.createdAt.isBefore(monthStart) && !it.createdAt.isAfter(monthEnd) }
                    .mapNotNull { tx ->
                        val type = when (tx.transactionType) {
                            TransactionType.INFLOW -> CalendarEventType.INFLOW
                            TransactionType.OUTFLOW -> CalendarEventType.OUTFLOW
                            else -> null
                        }
                        type?.let { CalendarEvent(tx.createdAt, it) }
                    }.toMutableList()

                calendarEvents += getRecurringInstancesForMonth(month, recurring)

                reminders.filter { it.message.contains("bill", ignoreCase = true) }
                    .filter { !it.date.isBefore(monthStart) && !it.date.isAfter(monthEnd) }
                    .forEach { rem -> calendarEvents.add(CalendarEvent(rem.date, CalendarEventType.BILL)) }

                InsightsUiState.Success(
                    cashflowByPeriod = cashflowByPeriod,
                    budgetVsOutflow = budgetByPeriod,
                    netWorthByPeriod = netWorthByPeriod,
                    calendarEvents = calendarEvents
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun buildNetWorthByRanges(
        transactions: List<dev.pandesal.sbp.domain.model.Transaction>,
        accounts: List<dev.pandesal.sbp.domain.model.Account>,
        ranges: List<Range>
    ): List<NetWorthBarGroup> {
        val assetAccounts = accounts.filter { it.type != AccountType.CREDIT_CARD }
        val liabilityAccounts = accounts.filter { it.type == AccountType.CREDIT_CARD }

        val assetBalances = assetAccounts.associate { it.id to it.balance }.toMutableMap()
        val liabilityBalances = liabilityAccounts.associate { it.id to it.balance }.toMutableMap()

        val sortedTx = transactions.sortedByDescending { it.createdAt }

        fun valuesAt(date: LocalDate): Pair<BigDecimal, BigDecimal> {
            val assetsMap = assetBalances.toMutableMap()
            val liabilitiesMap = liabilityBalances.toMutableMap()
            for (tx in sortedTx) {
                if (tx.createdAt > date) {
                    when (tx.transactionType) {
                        TransactionType.INFLOW -> {
                            tx.to?.let { id ->
                                assetsMap[id]?.let { assetsMap[id] = it - tx.amount }
                                liabilitiesMap[id]?.let { liabilitiesMap[id] = it - tx.amount }
                            }
                        }
                        TransactionType.OUTFLOW -> {
                            tx.from?.let { id ->
                                assetsMap[id]?.let { assetsMap[id] = it + tx.amount }
                                liabilitiesMap[id]?.let { liabilitiesMap[id] = it + tx.amount }
                            }
                        }
                        TransactionType.TRANSFER, TransactionType.ADJUSTMENT -> {
                            tx.from?.let { id ->
                                assetsMap[id]?.let { assetsMap[id] = it + tx.amount }
                                liabilitiesMap[id]?.let { liabilitiesMap[id] = it + tx.amount }
                            }
                            tx.to?.let { id ->
                                assetsMap[id]?.let { assetsMap[id] = it - tx.amount }
                                liabilitiesMap[id]?.let { liabilitiesMap[id] = it - tx.amount }
                            }
                        }
                    }
                } else {
                    break
                }
            }
            val assets = assetsMap.values.fold(BigDecimal.ZERO) { acc, v -> acc + v }
            val liabilities = liabilitiesMap.values.fold(BigDecimal.ZERO) { acc, v -> acc + v }
            return assets to liabilities
        }

        return ranges.map { range ->
            val (assets, liabilities) = valuesAt(range.end)
            NetWorthBarGroup(range.label, assets, liabilities)
        }
    }

    private fun groupTransactionsByPeriod(
        transactions: List<dev.pandesal.sbp.domain.model.Transaction>,
        period: TimePeriod
    ): List<CashflowUiModel> {
        val ranges = buildRanges(period)
        return ranges.map { range ->
            val txs = transactions.filter {
                !it.createdAt.isBefore(range.start) && !it.createdAt.isAfter(range.end)
            }
            val inflow = txs.filter { it.transactionType == TransactionType.INFLOW }
                .fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
            val outflow = txs.filter { it.transactionType == TransactionType.OUTFLOW }
                .fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }
            CashflowUiModel(range.label, inflow, outflow)
        }
    }

    private data class Range(val label: String, val start: LocalDate, val end: LocalDate)

    private fun buildRanges(period: TimePeriod): List<Range> {
        val today = LocalDate.now()
        return when (period) {
            TimePeriod.DAILY -> (4 downTo 0).map { i ->
                val date = today.minusDays(i.toLong())
                Range("${date.month.name.take(3)} ${date.dayOfMonth}", date, date)
            }
            TimePeriod.WEEKLY -> {
                val weekFields = WeekFields.ISO
                val startOfWeek = today.with(weekFields.dayOfWeek(), 1)
                (4 downTo 0).map { i ->
                    val start = startOfWeek.minusWeeks(i.toLong())
                    val end = start.plusDays(6)
                    val label = "W${start.get(weekFields.weekOfWeekBasedYear())}"
                    Range(label, start, end)
                }
            }
            TimePeriod.MONTHLY -> {
                (4 downTo 0).map { i ->
                    val ym = YearMonth.now().minusMonths(i.toLong())
                    Range(ym.month.name.take(3), ym.atDay(1), ym.atEndOfMonth())
                }
            }
            TimePeriod.THREE_MONTH -> {
                val current = YearMonth.now()
                val currentQuarterStartMonth = ((current.monthValue - 1) / 3) * 3 + 1
                val startYm = YearMonth.of(current.year, currentQuarterStartMonth)
                (4 downTo 0).map { i ->
                    val ym = startYm.minusMonths((i * 3).toLong())
                    val quarter = ((ym.monthValue - 1) / 3) + 1
                    Range("Q$quarter", ym.atDay(1), ym.plusMonths(2).atEndOfMonth())
                }
            }
            TimePeriod.SIX_MONTH -> {
                val current = YearMonth.now()
                val halfStartMonth = if (current.monthValue <= 6) 1 else 7
                val startYm = YearMonth.of(current.year, halfStartMonth)
                (4 downTo 0).map { i ->
                    val ym = startYm.minusMonths((i * 6).toLong())
                    val half = if (ym.monthValue <= 6) 1 else 2
                    Range("H$half", ym.atDay(1), ym.plusMonths(5).atEndOfMonth())
                }
            }
            TimePeriod.YEARLY ->
                (4 downTo 0).map { i ->
                    val year = today.year - i
                    Range(year.toString(), LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31))
                }
        }
    }

    private fun getRecurringInstancesForMonth(
        month: YearMonth,
        recurring: List<RecurringTransaction>
    ): List<CalendarEvent> {
        val start = month.atDay(1)
        val end = month.atEndOfMonth()
        val events = mutableListOf<CalendarEvent>()
        recurring.forEach { rec ->
            recurringUseCase.occurrencesInRange(rec, start, end).forEach { date ->
                val type = if (rec.transaction.transactionType == TransactionType.OUTFLOW) {
                    CalendarEventType.BILL
                } else {
                    CalendarEventType.RECURRING
                }
                events.add(CalendarEvent(date, type))
            }
        }
        return events
    }

    fun loadDayDetails(date: java.time.LocalDate) {
        _tooltipState.value = DayTooltipUiState.Loading
        viewModelScope.launch {
            combine(
                transactionUseCase.getTransactionsByDateRange(date, date),
                recurringUseCase.getRecurringTransactionsOn(date),
                reminderUseCase.getRemindersByDate(date)
            ) { txs, recs, rems ->
                DayTooltipUiState.Ready(txs, recs, rems)
            }.collect { state ->
                _tooltipState.value = state
            }
        }
    }
}
