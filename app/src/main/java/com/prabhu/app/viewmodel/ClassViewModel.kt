package com.prabhu.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prabhu.app.data.model.ClassEntity
import com.prabhu.app.data.repository.ClassRepository
import com.prabhu.app.util.ClassReminderScheduler
import com.prabhu.app.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassViewModel(
    private val repository: ClassRepository,
    private val appContext: Context
) : ViewModel() {

    fun loadClass(id: Long, onLoaded: (ClassEntity?) -> Unit) {
        viewModelScope.launch {
            onLoaded(repository.getById(id))
        }
    }

    /**
     * Creates a new class. When [repeatDays] is non-empty, one row is
     * generated for every matching weekday from [dateMillis] through
     * [repeatUntilMillis] (inclusive), each keeping the same start/end
     * time-of-day and duration.
     */
    fun addClass(
        subject: String,
        type: String,
        teacher: String,
        room: String,
        note: String,
        dateMillis: Long,
        endDateMillis: Long,
        attended: Boolean,
        remindMe: Boolean,
        repeatDays: Set<Int>,
        repeatUntilMillis: Long,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val occurrenceStarts = DateUtils.occurrencesOnDays(dateMillis, repeatDays, repeatUntilMillis)
            occurrenceStarts.forEach { occurrenceStart ->
                val occurrenceEnd = DateUtils.withTimeOf(occurrenceStart, endDateMillis)
                // "Attended" only makes sense for a class that already happened;
                // for a repeating series only apply the toggle to today's own
                // occurrence (if any) and default the rest to false.
                val attendedForOccurrence = if (repeatDays.isEmpty()) {
                    attended
                } else {
                    attended && DateUtils.isSameDay(occurrenceStart, DateUtils.now())
                }
                val id = repository.add(
                    ClassEntity(
                        subject = subject,
                        type = type,
                        teacher = teacher,
                        room = room,
                        note = note,
                        dateMillis = occurrenceStart,
                        endDateMillis = occurrenceEnd,
                        attended = attendedForOccurrence
                    )
                )
                if (remindMe) {
                    val detail = listOf(type, room, teacher).filter { it.isNotBlank() }.joinToString(" · ")
                    ClassReminderScheduler.schedule(appContext, id, subject, detail, occurrenceStart)
                }
            }
            onDone()
        }
    }

    /** Updates a single existing class (never regenerates a series). */
    fun updateClass(
        id: Long,
        subject: String,
        type: String,
        teacher: String,
        room: String,
        note: String,
        dateMillis: Long,
        endDateMillis: Long,
        attended: Boolean,
        remindMe: Boolean,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.update(
                ClassEntity(
                    id = id,
                    subject = subject,
                    type = type,
                    teacher = teacher,
                    room = room,
                    note = note,
                    dateMillis = dateMillis,
                    endDateMillis = endDateMillis,
                    attended = attended
                )
            )
            ClassReminderScheduler.cancel(appContext, id)
            if (remindMe) {
                val detail = listOf(type, room, teacher).filter { it.isNotBlank() }.joinToString(" · ")
                ClassReminderScheduler.schedule(appContext, id, subject, detail, dateMillis)
            }
            onDone()
        }
    }

    /**
     * Every day this class occurs on - same subject and same start time of
     * day, oldest first (includes the class with [id] itself).
     */
    fun loadRelatedDays(id: Long, onLoaded: (List<ClassEntity>) -> Unit) {
        viewModelScope.launch {
            val base = repository.getById(id)
            if (base == null) {
                onLoaded(emptyList())
                return@launch
            }
            val related = withContext(Dispatchers.Default) {
                val hour = DateUtils.hourOf(base.dateMillis)
                val minute = DateUtils.minuteOf(base.dateMillis)
                repository.getBySubject(base.subject).filter {
                    DateUtils.hourOf(it.dateMillis) == hour && DateUtils.minuteOf(it.dateMillis) == minute
                }
            }
            onLoaded(related)
        }
    }

    /** Deletes one or many classes (single day or multiple days) and their reminders. */
    fun deleteClasses(ids: List<Long>, onDone: () -> Unit) {
        viewModelScope.launch {
            ids.forEach { ClassReminderScheduler.cancel(appContext, it) }
            repository.removeByIds(ids)
            onDone()
        }
    }

    fun toggleAttended(classEntry: ClassEntity) {
        viewModelScope.launch {
            repository.update(classEntry.copy(attended = !classEntry.attended))
        }
    }
}
