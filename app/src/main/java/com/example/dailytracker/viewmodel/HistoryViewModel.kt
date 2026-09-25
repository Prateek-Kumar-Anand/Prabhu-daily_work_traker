package com.example.dailytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytracker.data.model.EntryType
import com.example.dailytracker.data.model.TrackEntry
import com.example.dailytracker.data.model.toTrackEntry
import com.example.dailytracker.data.repository.ActivityRepository
import com.example.dailytracker.data.repository.ClassRepository
import com.example.dailytracker.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val entries: List<TrackEntry> = emptyList(),
    val filter: EntryType? = null,
    val totalSpending: Double = 0.0,
    val totalClasses: Int = 0
)

class HistoryViewModel(
    expenseRepository: ExpenseRepository,
    classRepository: ClassRepository,
    activityRepository: ActivityRepository
) : ViewModel() {

    private val filter = MutableStateFlow<EntryType?>(null)

    private val allEntriesFlow = combine(
        expenseRepository.allExpenses,
        classRepository.allClasses,
        activityRepository.allActivities
    ) { expenses, classes, activities ->
        (expenses.map { it.toTrackEntry() } + classes.map { it.toTrackEntry() } + activities.map { it.toTrackEntry() })
            .sortedByDescending { it.dateMillis }
    }

    val uiState: StateFlow<HistoryUiState> = combine(allEntriesFlow, filter) { entries, currentFilter ->
        val filtered = if (currentFilter == null) entries else entries.filter { it.type == currentFilter }
        HistoryUiState(
            entries = filtered,
            filter = currentFilter,
            totalSpending = filtered.filter { it.type == EntryType.SPENDING }.sumOf { it.amount ?: 0.0 },
            totalClasses = filtered.count { it.type == EntryType.CLASS }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun setFilter(type: EntryType?) {
        filter.value = type
    }
}
