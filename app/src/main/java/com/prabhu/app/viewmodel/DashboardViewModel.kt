package com.prabhu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prabhu.app.data.model.ActivityEntity
import com.prabhu.app.data.model.ClassEntity
import com.prabhu.app.data.model.EntryType
import com.prabhu.app.data.model.ExpenseEntity
import com.prabhu.app.data.model.TrackEntry
import com.prabhu.app.data.model.toTrackEntry
import com.prabhu.app.data.repository.ActivityRepository
import com.prabhu.app.data.repository.ClassRepository
import com.prabhu.app.data.repository.ExpenseRepository
import com.prabhu.app.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val todaySpending: Double = 0.0,
    val todayClassCount: Int = 0,
    val todayActivityCount: Int = 0,
    val monthSpending: Double = 0.0,
    val calendarMonthAnchor: Long = 0L,
    val calendarDays: List<Long> = emptyList(),
    val calendarDayIndicators: Map<Long, Set<EntryType>> = emptyMap(),
    val recentSpending: List<TrackEntry> = emptyList(),
    val calendarClasses: List<TrackEntry> = emptyList(),
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

    // Any date within the currently displayed calendar month - navigable via
    // previousMonth()/nextMonth(), independent of "today"'s totals above.
    private val monthAnchor = MutableStateFlow(DateUtils.startOfMonth(now))

    fun previousMonth() {
        monthAnchor.value = DateUtils.addMonths(monthAnchor.value, -1)
    }

    fun nextMonth() {
        monthAnchor.value = DateUtils.addMonths(monthAnchor.value, 1)
    }

    fun currentMonth() {
        monthAnchor.value = DateUtils.startOfMonth(DateUtils.now())
    }

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

    val uiState = combine(totalsFlow, listsFlow, monthAnchor) { totals, lists, anchor ->
        val calendarDays = DateUtils.monthGrid(anchor)
        val calendarDaySet = calendarDays.toHashSet()

        val indicators = mutableMapOf<Long, MutableSet<EntryType>>()
        fun mark(dateMillis: Long, type: EntryType) {
            val dayStart = DateUtils.startOfDay(dateMillis)
            if (dayStart !in calendarDaySet) return
            indicators.getOrPut(dayStart) { mutableSetOf() }.add(type)
        }
        lists.expenses.forEach { mark(it.dateMillis, EntryType.SPENDING) }
        lists.classes.forEach { mark(it.dateMillis, EntryType.CLASS) }
        lists.activities.forEach { mark(it.dateMillis, EntryType.ACTIVITY) }

        val recentSpending = lists.expenses.map { it.toTrackEntry() }
            .sortedByDescending { it.dateMillis }
            .take(5)
        val calendarClasses = lists.classes
            .filter { c -> DateUtils.startOfDay(c.dateMillis) in calendarDaySet }
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
            calendarMonthAnchor = anchor,
            calendarDays = calendarDays,
            calendarDayIndicators = indicators,
            recentSpending = recentSpending,
            calendarClasses = calendarClasses,
            recentActivities = recentActivities
        )
    }.flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}
