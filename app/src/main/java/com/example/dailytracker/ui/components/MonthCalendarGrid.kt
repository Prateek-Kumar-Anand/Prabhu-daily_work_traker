package com.example.dailytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dailytracker.data.model.EntryType
import com.example.dailytracker.ui.theme.ActivityPurple
import com.example.dailytracker.ui.theme.ClassTeal
import com.example.dailytracker.ui.theme.SpendingRed
import com.example.dailytracker.util.DateUtils

private val weekdayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")
private const val CELL_HEIGHT_DP = 44

@Composable
fun MonthCalendarGrid(
    days: List<Long>,
    indicators: Map<Long, Set<EntryType>>,
    monthAnchor: Long,
    selectedDay: Long?,
    onDaySelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = DateUtils.now()
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                weekdayHeaders.forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            days.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        DayCell(
                            day = day,
                            inCurrentMonth = DateUtils.isSameMonth(day, monthAnchor),
                            isToday = DateUtils.isSameDay(day, today),
                            isSelected = selectedDay != null && DateUtils.isSameDay(day, selectedDay),
                            types = indicators[day].orEmpty(),
                            onClick = { onDaySelected(day) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DayCell(
    day: Long,
    inCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    types: Set<EntryType>,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(CELL_HEIGHT_DP.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .background(
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else -> Color.Transparent
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = DateUtils.dayNumber(day),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isSelected -> Color.White
                        !inCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                        isToday -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (types.contains(EntryType.SPENDING)) Dot(SpendingRed)
                if (types.contains(EntryType.CLASS)) Dot(ClassTeal)
                if (types.contains(EntryType.ACTIVITY)) Dot(ActivityPurple)
            }
        }
    }
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(4.dp)
            .background(color, RoundedCornerShape(50))
    )
}
