package com.example.dailytracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailytracker.data.model.EntryType
import com.example.dailytracker.ui.components.AppTopBar
import com.example.dailytracker.ui.components.EntryListItem
import com.example.dailytracker.ui.theme.ClassTeal
import com.example.dailytracker.ui.theme.SpendingRed
import com.example.dailytracker.util.formatMoney
import com.example.dailytracker.viewmodel.HistoryViewModel
import com.example.dailytracker.viewmodel.ViewModelFactory

private data class FilterOption(val label: String, val type: EntryType?)

private val filterOptions = listOf(
    FilterOption("All", null),
    FilterOption("Spending", EntryType.SPENDING),
    FilterOption("Classes", EntryType.CLASS),
    FilterOption("Activities", EntryType.ACTIVITY)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(factory: ViewModelFactory) {
    val viewModel: HistoryViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = "History", subtitle = "All your records in one place")

        LazyRow(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { option ->
                FilterChip(
                    selected = state.filter == option.type,
                    onClick = { viewModel.setFilter(option.type) },
                    label = { Text(option.label) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Total Spending", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        formatMoney(state.totalSpending),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SpendingRed
                    )
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Classes Logged", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        state.totalClasses.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ClassTeal
                    )
                }
            }
        }

        if (state.entries.isEmpty()) {
            Text(
                text = "No records yet. Use the Add tab to log spending, classes, or activities.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(20.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(state.entries, key = { "${it.type}-${it.id}" }) { entry ->
                    EntryListItem(entry)
                }
            }
        }
    }
}
