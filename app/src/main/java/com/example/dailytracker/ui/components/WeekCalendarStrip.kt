package com.example.dailytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dailytracker.data.model.EntryType
import com.example.dailytracker.ui.theme.ActivityPurple
import com.example.dailytracker.ui.theme.PrimaryBlue
import com.example.dailytracker.ui.theme.SaleGreen
import com.example.dailytracker.ui.theme.SpendingRed
import com.example.dailytracker.util.DateUtils

@Composable
fun WeekCalendarStrip(
    days: List<Long>,
    indicators: Map<Long, Set<EntryType>>,
    selectedDay: Long?,
    onDaySelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = DateUtils.now()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            days.forEach { day ->
                val isToday = DateUtils.isSameDay(day, today)
                val isSelected = selectedDay != null && DateUtils.isSameDay(day, selectedDay)
                val dayTypes = indicators[day].orEmpty()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onDaySelected(day) }
                ) {
                    Text(
                        text = DateUtils.dayLetter(day),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .size(34.dp)
                            .background(
                                color = when {
                                    isSelected -> PrimaryBlue
                                    isToday -> PrimaryBlue.copy(alpha = 0.12f)
                                    else -> androidx.compose.ui.graphics.Color.Transparent
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = DateUtils.dayNumber(day),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) androidx.compose.ui.graphics.Color.White
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (dayTypes.contains(EntryType.SPENDING)) DayDot(SpendingRed)
                        if (dayTypes.contains(EntryType.SALE)) DayDot(SaleGreen)
                        if (dayTypes.contains(EntryType.ACTIVITY)) DayDot(ActivityPurple)
                    }
                }
            }
        }
    }
}

@Composable
private fun DayDot(color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .size(5.dp)
            .background(color, CircleShape)
    )
}
