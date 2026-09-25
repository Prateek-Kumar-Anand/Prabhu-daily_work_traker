package com.example.dailytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytracker.data.model.ExpenseEntity
import com.example.dailytracker.data.repository.ExpenseRepository
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    fun addExpense(amount: Double, category: String, note: String, dateMillis: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.add(
                ExpenseEntity(
                    amount = amount,
                    category = category,
                    note = note,
                    dateMillis = dateMillis
                )
            )
            onDone()
        }
    }
}
