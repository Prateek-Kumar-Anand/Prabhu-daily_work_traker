package com.example.dailytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.dailytracker.DailyTrackerApplication

/**
 * Simple manual-DI factory: reads the repositories off [DailyTrackerApplication]
 * and constructs whichever ViewModel is requested.
 */
class ViewModelFactory(private val app: DailyTrackerApplication) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(app.expenseRepository, app.saleRepository, app.activityRepository) as T

            modelClass.isAssignableFrom(ExpenseViewModel::class.java) ->
                ExpenseViewModel(app.expenseRepository) as T

            modelClass.isAssignableFrom(SaleViewModel::class.java) ->
                SaleViewModel(app.saleRepository) as T

            modelClass.isAssignableFrom(ActivityViewModel::class.java) ->
                ActivityViewModel(app.activityRepository) as T

            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(app.expenseRepository, app.saleRepository, app.activityRepository) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
