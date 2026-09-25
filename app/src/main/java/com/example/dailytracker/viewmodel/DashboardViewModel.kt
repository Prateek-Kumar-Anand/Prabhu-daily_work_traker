package com.example.dailytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytracker.data.model.ActivityEntity
import com.example.dailytracker.data.model.EntryType
import com.example.dailytracker.data.model.ExpenseEntity
import com.example.dailytracker.data.model.SaleEntity
import com.example.dailytracker.data.model.TrackEntry
import com.example.dailytracker.data.model.toTrackEntry
import com.example.dailytracker.data.repository.ActivityRepository
import com.example.dailytracker.data.repository.ExpenseRepository
import com.example.dailytracker.data.repository.SaleRepository
import com.example.dailytracker.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val todaySpending: Double = 0.0,
    val todaySales: Double = 0.0,
    val todayActivityCount: Int = 0,
    val monthSpending: Double = 0.0,
    val monthSales: Double = 0.0,
    val weekDayIndicators: Map<Long, Set<EntryType>> = emptyMap(),
    val recentTransactions: List<TrackEntry> = emptyList(),
    val recentActivities: List<TrackEntry> = emptyList()
)

private data class Totals(
    val todaySpending: Double,
    val todaySales: Double,
    val todayActivities: Int,
    val monthSpending: Double,
    val monthSales: Double
)

private data class Lists(
    val expenses: List<ExpenseEntity>,
    val sales: List<SaleEntity>,
    val activities: List<ActivityEntity>
)

class DashboardViewModel(
    private val expenseRepository: ExpenseRepository,
    private val saleRepository: SaleRepository,
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val now = DateUtils.now()
    private val todayStart = DateUtils.startOfDay(now)
    private val todayEnd = DateUtils.endOfDay(now)
    private val monthStart = DateUtils.startOfMonth(now)
    private val monthEnd = DateUtils.endOfMonth(now)

    val weekDays: List<Long> = DateUtils.weekOf(now)

    private val totalsFlow = combine(
        expenseRepository.totalBetween(todayStart, todayEnd),
        saleRepository.totalBetween(todayStart, todayEnd),
        activityRepository.countBetween(todayStart, todayEnd),
        expenseRepository.totalBetween(monthStart, monthEnd),
        saleRepository.totalBetween(monthStart, monthEnd)
    ) { todaySpending, todaySales, todayActivities, monthSpending, monthSales ->
        Totals(todaySpending, todaySales, todayActivities, monthSpending, monthSales)
    }

    private val listsFlow = combine(
        expenseRepository.allExpenses,
        saleRepository.allSales,
        activityRepository.allActivities
    ) { expenses, sales, activities ->
        Lists(expenses, sales, activities)
    }

    val uiState = combine(totalsFlow, listsFlow) { totals, lists ->
        val indicators = mutableMapOf<Long, MutableSet<EntryType>>()
        fun mark(dateMillis: Long, type: EntryType) {
            val dayStart = weekDays.firstOrNull { DateUtils.isSameDay(it, dateMillis) } ?: return
            indicators.getOrPut(dayStart) { mutableSetOf() }.add(type)
        }
        lists.expenses.forEach { mark(it.dateMillis, EntryType.SPENDING) }
        lists.sales.forEach { mark(it.dateMillis, EntryType.SALE) }
        lists.activities.forEach { mark(it.dateMillis, EntryType.ACTIVITY) }

        val transactions = (lists.expenses.map { it.toTrackEntry() } + lists.sales.map { it.toTrackEntry() })
            .sortedByDescending { it.dateMillis }
            .take(5)
        val recentActivities = lists.activities.map { it.toTrackEntry() }
            .sortedByDescending { it.dateMillis }
            .take(5)

        DashboardUiState(
            todaySpending = totals.todaySpending,
            todaySales = totals.todaySales,
            todayActivityCount = totals.todayActivities,
            monthSpending = totals.monthSpending,
            monthSales = totals.monthSales,
            weekDayIndicators = indicators,
            recentTransactions = transactions,
            recentActivities = recentActivities
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}
