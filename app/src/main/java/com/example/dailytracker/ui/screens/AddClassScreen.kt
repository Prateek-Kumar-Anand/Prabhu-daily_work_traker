package com.example.dailytracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailytracker.data.model.classCategories
import com.example.dailytracker.util.DateUtils
import com.example.dailytracker.viewmodel.ClassViewModel
import com.example.dailytracker.viewmodel.ViewModelFactory
import java.util.Calendar

private val repeatOptions = listOf("Just this day", "Weekdays (Mon\u2013Fri)", "Custom days")
private val weekdaySet = setOf(
    Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY
)
private val dayToggleLabels = listOf(
    "S" to Calendar.SUNDAY, "M" to Calendar.MONDAY, "T" to Calendar.TUESDAY,
    "W" to Calendar.WEDNESDAY, "T" to Calendar.THURSDAY, "F" to Calendar.FRIDAY, "S" to Calendar.SATURDAY
)
private const val DEFAULT_DURATION_MILLIS = 60 * 60 * 1000L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassScreen(factory: ViewModelFactory, classId: Long? = null, onBack: () -> Unit) {
    val viewModel: ClassViewModel = viewModel(factory = factory)
    val isEditMode = classId != null

    var subject by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(classCategories.first()) }
    var teacher by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var attended by remember { mutableStateOf(true) }
    var remindMe by remember { mutableStateOf(true) }
    var classTimeMillis by remember { mutableStateOf(DateUtils.now()) }
    var classEndTimeMillis by remember { mutableStateOf(DateUtils.now() + DEFAULT_DURATION_MILLIS) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var subjectError by remember { mutableStateOf(false) }
    var endTimeError by remember { mutableStateOf(false) }
    var repeatChoice by remember { mutableStateOf(repeatOptions[0]) }
    var customDays by remember { mutableStateOf(setOf<Int>()) }
    var customDaysError by remember { mutableStateOf(false) }
    var repeatUntilMillis by remember { mutableStateOf(DateUtils.addDays(DateUtils.startOfDay(DateUtils.now()), 8 * 7)) }
    var showRepeatUntilPicker by remember { mutableStateOf(false) }

    LaunchedEffect(classId) {
        if (classId != null) {
            viewModel.loadClass(classId) { loaded ->
                if (loaded != null) {
                    subject = loaded.subject
                    type = loaded.type
                    teacher = loaded.teacher
                    room = loaded.room
                    note = loaded.note
                    classTimeMillis = loaded.dateMillis
                    classEndTimeMillis = loaded.endDateMillis
                    attended = loaded.attended
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        Text(text = if (isEditMode) "Edit Class" else "Add a Class", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = DateUtils.formatFullDate(classTimeMillis),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp, top = 4.dp)
        )

        OutlinedTextField(
            value = subject,
            onValueChange = {
                subject = it
                subjectError = false
            },
            label = { Text("Subject") },
            isError = subjectError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Type",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
        )
        CategoryChipRow(
            options = classCategories,
            selected = type,
            onSelect = { type = it }
        )

        OutlinedTextField(
            value = teacher,
            onValueChange = { teacher = it },
            label = { Text("Teacher (optional)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = room,
            onValueChange = { room = it },
            label = { Text("Room (optional)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Text(
            text = "Class time",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { showTimePicker = true }) {
                Text(DateUtils.formatTime(classTimeMillis))
            }
            Text("to", style = MaterialTheme.typography.bodyMedium)
            OutlinedButton(onClick = { showEndTimePicker = true }) {
                Text(DateUtils.formatTime(classEndTimeMillis))
            }
        }
        if (endTimeError) {
            Text(
                text = "End time must be after the start time",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (!isEditMode) {
            Text(
                text = "Repeat",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )
            CategoryChipRow(
                options = repeatOptions,
                selected = repeatChoice,
                onSelect = {
                    repeatChoice = it
                    customDaysError = false
                }
            )
            if (repeatChoice == repeatOptions[2]) {
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    dayToggleLabels.forEach { (label, dayOfWeek) ->
                        val isSelected = dayOfWeek in customDays
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                )
                                .clickable {
                                    customDays = if (isSelected) customDays - dayOfWeek else customDays + dayOfWeek
                                    customDaysError = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                if (customDaysError) {
                    Text(
                        text = "Pick at least one day",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            if (repeatChoice != repeatOptions[0]) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Repeats until", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedButton(
                        onClick = { showRepeatUntilPicker = true },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(DateUtils.formatShortDate(repeatUntilMillis))
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Remind me at class time", style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = remindMe,
                onCheckedChange = { remindMe = it },
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Attended", style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = attended,
                onCheckedChange = { attended = it },
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Button(
            onClick = {
                val repeatDays = when (repeatChoice) {
                    repeatOptions[1] -> weekdaySet
                    repeatOptions[2] -> customDays
                    else -> emptySet()
                }
                when {
                    subject.isBlank() -> subjectError = true
                    classEndTimeMillis <= classTimeMillis -> endTimeError = true
                    !isEditMode && repeatChoice == repeatOptions[2] && customDays.isEmpty() -> customDaysError = true
                    isEditMode -> viewModel.updateClass(
                        classId!!, subject, type, teacher, room, note,
                        classTimeMillis, classEndTimeMillis, attended, remindMe
                    ) { onBack() }
                    else -> viewModel.addClass(
                        subject, type, teacher, room, note,
                        classTimeMillis, classEndTimeMillis, attended, remindMe,
                        repeatDays, repeatUntilMillis
                    ) { onBack() }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        ) {
            Text(if (isEditMode) "Save Changes" else "Save Class")
        }
    }

    if (showTimePicker) {
        val timeState = rememberTimePickerState(
            initialHour = DateUtils.hourOf(classTimeMillis),
            initialMinute = DateUtils.minuteOf(classTimeMillis),
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val pickedTimeToday = DateUtils.atTimeToday(timeState.hour, timeState.minute)
                    val duration = classEndTimeMillis - classTimeMillis
                    classTimeMillis = DateUtils.withTimeOf(classTimeMillis, pickedTimeToday)
                    if (duration > 0) classEndTimeMillis = classTimeMillis + duration
                    endTimeError = false
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timeState) }
        )
    }

    if (showEndTimePicker) {
        val endTimeState = rememberTimePickerState(
            initialHour = DateUtils.hourOf(classEndTimeMillis),
            initialMinute = DateUtils.minuteOf(classEndTimeMillis),
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val pickedTimeToday = DateUtils.atTimeToday(endTimeState.hour, endTimeState.minute)
                    classEndTimeMillis = DateUtils.withTimeOf(classTimeMillis, pickedTimeToday)
                    endTimeError = false
                    showEndTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = endTimeState) }
        )
    }

    if (showRepeatUntilPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = DateUtils.localDateToUtcMillis(repeatUntilMillis)
        )
        DatePickerDialog(
            onDismissRequest = { showRepeatUntilPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { utcMillis ->
                        repeatUntilMillis = DateUtils.utcMillisToLocalDate(utcMillis)
                    }
                    showRepeatUntilPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showRepeatUntilPicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
