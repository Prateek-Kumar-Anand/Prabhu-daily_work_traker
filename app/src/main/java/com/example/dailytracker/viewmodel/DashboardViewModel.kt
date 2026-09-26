package com.example.dailytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytracker.data.model.ActivityEntity
import com.example.dailytracker.data.model.ClassEntity
import com.example.dailytracker.data.model.EntryType
import com.example.dailytracker.data.model.ExpenseEntity
import com.example.dailytracker.data.model.TrackEntry
import com.example.dailytracker.data.model.toTrackEntry
import com.example.dailytracker.data.repository.ActivityRepository
import com.example.dailytracker.data.repository.ClassRepository
import com.example.dailytracker.data.repository.ExpenseRepository
import com.example.dailytracker.util.DateUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val todaySpending: Double = 0.0,
    val todayClassCount: Int = 0,
    val todayActivityCount: Int = 0,
    val monthSpending: Double = 0.0,
    val weekDayIndicators: Map<Long, Set<EntryType>> = emptyMap(),
    val recentSpending: List<TrackEntry> = emptyList(),
    val weekClasses: List<TrackEntry> = emptyList(),
    val recentActivities: List<TrackEntry> = emptyList()
)

private data class Totals(
    val todaySpending: Double,
    val todayClasses: Int,
    val todayActivities: Int,
    val monthSpending: Double
)

private data class Lists(
    val expenses: List<ExpenseEntity>,
    val classes: List<ClassEntity>,
    val activities: List<ActivityEntity>
)

class DashboardViewModel(
    private val expenseRepository: ExpenseRepository,
    private val classRepository: ClassRepository,
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
        classRepository.countBetween(todayStart, todayEnd),
        activityRepository.countBetween(todayStart, todayEnd),
        expenseRepository.totalBetween(monthStart, monthEnd)
    ) { todaySpending, todayClasses, todayActivities, monthSpending ->
        Totals(todaySpending, todayClasses, todayActivities, monthSpending)
    }

    private val listsFlow = combine(
        expenseRepository.allExpenses,
        classRepository.allClasses,
        activityRepository.allActivities
    ) { expenses, classes, activities ->
        Lists(expenses, classes, activities)
    }

    val uiState = combine(totalsFlow, listsFlow) { totals, lists ->
        val indicators = mutableMapOf<Long, MutableSet<EntryType>>()
        fun mark(dateMillis: Long, type: EntryType) {
            val dayStart = weekDays.firstOrNull { DateUtils.isSameDay(it, dateMillis) } ?: return
            indicators.getOrPut(dayStart) { mutableSetOf() }.add(type)
        }
        lists.expenses.forEach { mark(it.dateMillis, EntryType.SPENDING) }
        lists.classes.forEach { mark(it.dateMillis, EntryType.CLASS) }
        lists.activities.forEach { mark(it.dateMillis, EntryType.ACTIVITY) }

        val recentSpending = lists.expenses.map { it.toTrackEntry() }
            .sortedByDescending { it.dateMillis }
            .take(5)
        val weekClasses = lists.classes
            .filter { c -> weekDays.any { DateUtils.isSameDay(it, c.dateMillis) } }
            .map { it.toTrackEntry() }
            .sortedBy { it.dateMillis }
        val recentActivities = lists.activities.map { it.toTrackEntry() }
            .sortedByDescending { it.dateMillis }
            .take(5)

        DashboardUiState(
            todaySpending = totals.todaySpending,
            todayClassCount = totals.todayClasses,
            todayActivityCount = totals.todayActivities,
            monthSpending = totals.monthSpending,
            weekDayIndicators = indicators,
            recentSpending = recentSpending,
            weekClasses = weekClasses,
            recentActivities = recentActivities
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}
