package com.example.dailytracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailytracker.ui.components.AppTopBar
import com.example.dailytracker.ui.components.ClassScheduleBlock
import com.example.dailytracker.ui.components.EntryListItem
import com.example.dailytracker.ui.components.SectionCard
import com.example.dailytracker.ui.components.SummaryCard
import com.example.dailytracker.ui.components.WeekCalendarStrip
import com.example.dailytracker.ui.theme.ActivityPurple
import com.example.dailytracker.ui.theme.ActivityPurpleLight
import com.example.dailytracker.ui.theme.ClassTeal
import com.example.dailytracker.ui.theme.ClassTealLight
import com.example.dailytracker.ui.theme.PrimaryBlue
import com.example.dailytracker.ui.theme.PrimaryBlueLight
import com.example.dailytracker.ui.theme.SpendingRed
import com.example.dailytracker.ui.theme.SpendingRedLight
import com.example.dailytracker.util.DateUtils
import com.example.dailytracker.util.formatMoney
import com.example.dailytracker.viewmodel.DashboardViewModel
import com.example.dailytracker.viewmodel.ViewModelFactory

@Composable
fun DashboardScreen(
    factory: ViewModelFactory,
    onViewAllTransactions: () -> Unit,
    onViewAllActivities: () -> Unit,
    onAddSpending: () -> Unit,
    onAddClass: () -> Unit,
    onAddActivity: () -> Unit
) {
    val viewModel: DashboardViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()
    var selectedDay by remember { mutableStateOf(DateUtils.startOfDay(DateUtils.now())) }

    val selectedDayClasses = state.weekClasses.filter { DateUtils.isSameDay(it.dateMillis, selectedDay) }
    val isToday = DateUtils.isSameDay(selectedDay, DateUtils.now())
    val scheduleTitle = if (isToday) "Today's Schedule" else DateUtils.formatFullDate(selectedDay)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            AppTopBar(title = "Overview")
        }

        item {
            WeekCalendarStrip(
                days = viewModel.weekDays,
                indicators = state.weekDayIndicators,
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        label = "Today's Spending",
                        value = formatMoney(state.todaySpending),
                        icon = Icons.Filled.ShoppingCart,
                        accentColor = SpendingRed,
                        accentContainer = SpendingRedLight,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "Today's Classes",
                        value = state.todayClassCount.toString(),
                        icon = Icons.Filled.School,
                        accentColor = ClassTeal,
                        accentContainer = ClassTealLight,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        label = "Activities Today",
                        value = state.todayActivityCount.toString(),
                        icon = Icons.Filled.TaskAlt,
                        accentColor = ActivityPurple,
                        accentContainer = ActivityPurpleLight,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "This Month's Spending",
                        value = formatMoney(state.monthSpending),
                        icon = Icons.Filled.CalendarMonth,
                        accentColor = PrimaryBlue,
                        accentContainer = PrimaryBlueLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                SectionCard(title = "Recent Spending", onViewAll = onViewAllTransactions) {
                    if (state.recentSpending.isEmpty()) {
                        Text(
                            text = "No spending recorded yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        state.recentSpending.forEach { entry ->
                            EntryListItem(entry)
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                SectionCard(title = scheduleTitle) {
                    if (selectedDayClasses.isEmpty()) {
                        Text(
                            text = if (isToday) "No classes logged for today." else "No classes logged for this day.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedDayClasses.forEach { entry ->
                                ClassScheduleBlock(entry)
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                SectionCard(title = "Today's Activities", onViewAll = onViewAllActivities) {
                    if (state.recentActivities.isEmpty()) {
                        Text(
                            text = "No activities logged yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        state.recentActivities.forEach { entry ->
                            EntryListItem(entry)
                        }
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                SectionCard(title = "Quick Add") {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Button(
                            onClick = onAddSpending,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Record Spending") }
                        OutlinedButton(
                            onClick = onAddClass,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) { Text("Add a Class") }
                        OutlinedButton(
                            onClick = onAddActivity,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) { Text("Log an Activity") }
                    }
                }
            }
        }
    }
}
