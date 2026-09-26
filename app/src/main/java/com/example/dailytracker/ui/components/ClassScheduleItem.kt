package com.example.dailytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dailytracker.data.model.TrackEntry
import com.example.dailytracker.ui.theme.ClassTeal
import com.example.dailytracker.ui.theme.ClassTealLight
import com.example.dailytracker.util.DateUtils

/**
 * A single class rendered as a colored, time-labeled block - the "Add a
 * Class" equivalent of the reference dashboard's Paid Leave / Vacation bars.
 */
@Composable
fun ClassScheduleBlock(entry: TrackEntry, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ClassTealLight, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(ClassTeal, CircleShape)
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F4C46)
            )
            if (entry.subtitle.isNotBlank()) {
                Text(
                    text = entry.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF0F4C46).copy(alpha = 0.7f)
                )
            }
        }
        Text(
            text = DateUtils.formatTime(entry.dateMillis),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0F4C46),
            modifier = Modifier
                .padding(start = 12.dp)
                .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
