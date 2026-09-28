package com.prabhu.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prabhu.app.data.model.ClassEntity
import com.prabhu.app.util.DateUtils

/**
 * Delete a class for just this day, or pick several days (every day the same
 * class - same subject and start time - occurs on) and delete them together.
 */
@Composable
fun DeleteClassDialog(
    classId: Long,
    loadDays: (Long, (List<ClassEntity>) -> Unit) -> Unit,
    onConfirm: (List<Long>) -> Unit,
    onDismiss: () -> Unit
) {
    var multiple by remember { mutableStateOf(false) }
    var days by remember { mutableStateOf<List<ClassEntity>>(emptyList()) }
    var selected by remember { mutableStateOf(setOf(classId)) }

    LaunchedEffect(classId) { loadDays(classId) { days = it } }

    val idsToDelete = if (multiple) selected.toList() else listOf(classId)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete class") },
        text = {
            Column {
                ChoiceRow("Only this day", selected = !multiple) { multiple = false }
                ChoiceRow("Multiple days", selected = multiple) { multiple = true }

                if (multiple) {
                    if (days.size <= 1) {
                        Text(
                            text = "No other days found for this class.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Row(modifier = Modifier.padding(top = 4.dp)) {
                            TextButton(onClick = { selected = days.map { it.id }.toSet() }) { Text("Select all") }
                            TextButton(onClick = { selected = emptySet() }) { Text("Clear") }
                        }
                        LazyColumn(modifier = Modifier.heightIn(max = 260.dp)) {
                            items(days, key = { it.id }) { day ->
                                val checked = day.id in selected
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selected = if (checked) selected - day.id else selected + day.id
                                        }
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(checked = checked, onCheckedChange = null)
                                    Text(
                                        text = DateUtils.formatWeekdayDate(day.dateMillis) +
                                            if (day.id == classId) " (this class)" else "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = idsToDelete.isNotEmpty(),
                onClick = { onConfirm(idsToDelete) },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(if (multiple) "Delete (${idsToDelete.size})" else "Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ChoiceRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
        )
    }
}
