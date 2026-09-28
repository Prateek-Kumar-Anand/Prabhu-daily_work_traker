package com.prabhu.app.ui.screens

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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prabhu.app.ui.components.AppTopBar
import com.prabhu.app.ui.components.ClassScheduleBlock
import com.prabhu.app.ui.components.EntryListItem
import com.prabhu.app.ui.components.MonthCalendarGrid
import com.prabhu.app.ui.components.SectionCard
import com.prabhu.app.ui.components.SummaryCard
import com.prabhu.app.ui.theme.ActivityPurple
import com.prabhu.app.ui.theme.ActivityPurpleLight
import com.prabhu.app.ui.theme.ClassTeal
import com.prabhu.app.ui.theme.ClassTealLight
import com.prabhu.app.ui.theme.PrimaryBlue
import com.prabhu.app.ui.theme.PrimaryBlueLight
import com.prabhu.app.ui.theme.SpendingRed
import com.prabhu.app.ui.theme.SpendingRedLight
import com.prabhu.app.util.DateUtils
import com.prabhu.app.util.formatMoney
import com.prabhu.app.viewmodel.DashboardViewModel
import com.prabhu.app.viewmodel.ViewModelFactory

@Composable
fun DashboardScreen(
    factory: ViewModelFactory,
    onViewAllTransactions: () -> Unit,
    onViewAllActivities: () -> Unit,
    onAddSpending: () -> Unit,
    onAddClass: () -> Unit,
    onAddActivity: () -> Unit,
    onEditClass: (Long) -> Unit,
    onOpenUsage: () -> Unit
) {
    val viewModel: DashboardViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()
    var selectedDay by remember { mutableStateOf(DateUtils.startOfDay(DateUtils.now())) }

    val selectedDayClasses = remember(state.calendarClasses, selectedDay) {
        state.calendarClasses.filter { DateUtils.isSameDay(it.dateMillis, selectedDay) }
    }
    val isToday = remember(selectedDay) { DateUtils.isSameDay(selectedDay, DateUtils.now()) }
    val scheduleTitle = remember(selectedDay, isToday) {
        if (isToday) "Today's Schedule" else DateUtils.formatFullDate(selectedDay)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            AppTopBar(title = "Overview", onUsageClick = onOpenUsage)
        }

        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.previousMonth() }) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
                    }
                    Text(
                        text = if (state.calendarMonthAnchor != 0L) DateUtils.formatMonthYear(state.calendarMonthAnchor) else "",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    )
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
                    }
                }
                MonthCalendarGrid(
                    days = state.calendarDays,
                    indicators = state.calendarDayIndicators,
                    monthAnchor = state.calendarMonthAnchor,
                    selectedDay = selectedDay,
                    onDaySelected = { day -> selectedDay = day }
                )
            }
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
                                ClassScheduleBlock(entry, onClick = { onEditClass(entry.id) })
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
