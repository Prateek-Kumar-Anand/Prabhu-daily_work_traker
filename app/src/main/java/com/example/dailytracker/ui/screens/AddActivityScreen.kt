package com.example.dailytracker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailytracker.data.model.activityCategories
import com.example.dailytracker.util.DateUtils
import com.example.dailytracker.viewmodel.ActivityViewModel
import com.example.dailytracker.viewmodel.ViewModelFactory

@Composable
fun AddActivityScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: ActivityViewModel = viewModel(factory = factory)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(activityCategories.first()) }
    var completed by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        Text(text = "Log an Activity", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = DateUtils.formatFullDate(DateUtils.now()),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 20.dp, top = 4.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                titleError = false
            },
            label = { Text("Activity title") },
            isError = titleError,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Category",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
        )
        CategoryChipRow(
            options = activityCategories,
            selected = category,
            onSelect = { category = it }
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Details (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Mark as completed", style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = completed,
                onCheckedChange = { completed = it },
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Button(
            onClick = {
                if (title.isBlank()) {
                    titleError = true
                } else {
                    viewModel.addActivity(title, description, category, DateUtils.now(), completed) { onBack() }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
        ) {
            Text("Save Activity")
        }
    }
}
