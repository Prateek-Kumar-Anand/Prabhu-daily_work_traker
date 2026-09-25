package com.example.dailytracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytracker.data.model.ClassEntity
import com.example.dailytracker.data.repository.ClassRepository
import com.example.dailytracker.util.ClassReminderScheduler
import kotlinx.coroutines.launch

class ClassViewModel(
    private val repository: ClassRepository,
    private val appContext: Context
) : ViewModel() {

    fun addClass(
        subject: String,
        type: String,
        teacher: String,
        room: String,
        note: String,
        dateMillis: Long,
        attended: Boolean,
        remindMe: Boolean,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val id = repository.add(
                ClassEntity(
                    subject = subject,
                    type = type,
                    teacher = teacher,
                    room = room,
                    note = note,
                    dateMillis = dateMillis,
                    attended = attended
                )
            )
            if (remindMe) {
                val detail = listOf(type, room, teacher).filter { it.isNotBlank() }.joinToString(" · ")
                ClassReminderScheduler.schedule(appContext, id, subject, detail, dateMillis)
            }
            onDone()
        }
    }

    fun toggleAttended(classEntry: ClassEntity) {
        viewModelScope.launch {
            repository.update(classEntry.copy(attended = !classEntry.attended))
        }
    }
}
