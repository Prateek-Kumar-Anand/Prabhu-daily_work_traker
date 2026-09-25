package com.example.dailytracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytracker.data.model.ActivityEntity
import com.example.dailytracker.data.repository.ActivityRepository
import kotlinx.coroutines.launch

class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {

    fun addActivity(
        title: String,
        description: String,
        category: String,
        dateMillis: Long,
        isCompleted: Boolean,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.add(
                ActivityEntity(
                    title = title,
                    description = description,
                    category = category,
                    dateMillis = dateMillis,
                    isCompleted = isCompleted
                )
            )
            onDone()
        }
    }

    fun toggleCompleted(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.update(activity.copy(isCompleted = !activity.isCompleted))
        }
    }
}
