package com.example.dailytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytracker.data.model.EntryType
import com.example.dailytracker.data.model.TrackEntry
import com.example.dailytracker.data.model.toTrackEntry
import com.example.dailytracker.data.repository.ActivityRepository
import com.example.dailytracker.data.repository.ExpenseRepository
import com.example.dailytracker.data.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HistoryUiState(
    val entries: List<TrackEntry> = emptyList(),
    val filter: EntryType? = null,
    val totalSpending: Double = 0.0,
    val totalSales: Double = 0.0
)

class HistoryViewModel(
    expenseRepository: ExpenseRepository,
    saleRepository: SaleRepository,
    activityRepository: ActivityRepository
) : ViewModel() {

    private val filter = MutableStateFlow<EntryType?>(null)

    private val allEntriesFlow = combine(
        expenseRepository.allExpenses,
        saleRepository.allSales,
        activityRepository.allActivities
    ) { expenses, sales, activities ->
        (expenses.map { it.toTrackEntry() } + sales.map { it.toTrackEntry() } + activities.map { it.toTrackEntry() })
            .sortedByDescending { it.dateMillis }
    }

    val uiState: StateFlow<HistoryUiState> = combine(allEntriesFlow, filter) { entries, currentFilter ->
        val filtered = if (currentFilter == null) entries else entries.filter { it.type == currentFilter }
        HistoryUiState(
            entries = filtered,
            filter = currentFilter,
            totalSpending = filtered.filter { it.type == EntryType.SPENDING }.sumOf { it.amount ?: 0.0 },
            totalSales = filtered.filter { it.type == EntryType.SALE }.sumOf { it.amount ?: 0.0 }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())

    fun setFilter(type: EntryType?) {
        filter.value = type
    }
}
