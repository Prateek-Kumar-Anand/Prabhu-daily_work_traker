package com.example.dailytracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailytracker.data.model.ClassEntity
import com.example.dailytracker.data.repository.ClassRepository
import com.example.dailytracker.util.ClassReminderScheduler
import com.example.dailytracker.util.DateUtils
import kotlinx.coroutines.launch

/** How many weeks ahead a recurring class is generated for. */
private const val REPEAT_WEEKS = 8

class ClassViewModel(
    private val repository: ClassRepository,
    private val appContext: Context
) : ViewModel() {

    /**
     * @param repeatDays Calendar.DAY_OF_WEEK values (SUNDAY=1..SATURDAY=7) to
     * repeat this class on. Empty means just the one date/time picked.
     */
    fun addClass(
        subject: String,
        type: String,
        teacher: String,
        room: String,
        note: String,
        dateMillis: Long,
        attended: Boolean,
        remindMe: Boolean,
        repeatDays: Set<Int>,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val occurrences = DateUtils.occurrencesOnDays(dateMillis, repeatDays, REPEAT_WEEKS)
            occurrences.forEach { occurrenceMillis ->
                // "Attended" only makes sense for a class that already happened;
                // for a repeating series only apply the toggle to today's own
                // occurrence (if any) and default the rest to false.
                val attendedForOccurrence = if (repeatDays.isEmpty()) {
                    attended
                } else {
                    attended && DateUtils.isSameDay(occurrenceMillis, DateUtils.now())
                }
                val id = repository.add(
                    ClassEntity(
                        subject = subject,
                        type = type,
                        teacher = teacher,
                        room = room,
                        note = note,
                        dateMillis = occurrenceMillis,
                        attended = attendedForOccurrence
                    )
                )
                if (remindMe) {
                    val detail = listOf(type, room, teacher).filter { it.isNotBlank() }.joinToString(" · ")
                    ClassReminderScheduler.schedule(appContext, id, subject, detail, occurrenceMillis)
                }
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
