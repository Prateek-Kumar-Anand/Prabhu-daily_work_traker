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
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: ClassViewModel = viewModel(factory = factory)

    var subject by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(classCategories.first()) }
    var teacher by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var attended by remember { mutableStateOf(true) }
    var remindMe by remember { mutableStateOf(true) }
    var classTimeMillis by remember { mutableStateOf(DateUtils.now()) }
    var showTimePicker by remember { mutableStateOf(false) }
    var subjectError by remember { mutableStateOf(false) }
    var repeatChoice by remember { mutableStateOf(repeatOptions[0]) }
    var customDays by remember { mutableStateOf(setOf<Int>()) }
    var customDaysError by remember { mutableStateOf(false) }

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
        Text(text = "Add a Class", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = DateUtils.formatFullDate(DateUtils.now()),
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
        OutlinedButton(onClick = { showTimePicker = true }) {
            Text(DateUtils.formatTime(classTimeMillis))
        }

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
            Text(
                text = "Repeats for the next 8 weeks",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else if (repeatChoice == repeatOptions[1]) {
            Text(
                text = "Repeats for the next 8 weeks",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
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
                    repeatChoice == repeatOptions[2] && customDays.isEmpty() -> customDaysError = true
                    else -> viewModel.addClass(
                        subject, type, teacher, room, note,
                        classTimeMillis, attended, remindMe, repeatDays
                    ) { onBack() }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        ) {
            Text("Save Class")
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
                    classTimeMillis = DateUtils.atTimeToday(timeState.hour, timeState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timeState) }
        )
    }
}
