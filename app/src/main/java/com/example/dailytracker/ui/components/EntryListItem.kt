package com.example.dailytracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dailytracker.data.model.EntryType
import com.example.dailytracker.data.model.TrackEntry
import com.example.dailytracker.ui.theme.ActivityPurple
import com.example.dailytracker.ui.theme.ActivityPurpleLight
import com.example.dailytracker.ui.theme.ClassTeal
import com.example.dailytracker.ui.theme.ClassTealLight
import com.example.dailytracker.ui.theme.SpendingRed
import com.example.dailytracker.ui.theme.SpendingRedLight
import com.example.dailytracker.util.DateUtils
import com.example.dailytracker.util.formatMoney

@Composable
fun SectionCard(
    title: String,
    onViewAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                if (onViewAll != null) {
                    TextButton(onClick = onViewAll) {
                        Text("View all", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            content()
        }
    }
}

@Composable
fun EntryListItem(entry: TrackEntry, modifier: Modifier = Modifier) {
    val (icon: ImageVector, tint, container) = when (entry.type) {
        EntryType.SPENDING -> Triple(Icons.Filled.ShoppingCart, SpendingRed, SpendingRedLight)
        EntryType.CLASS -> Triple(Icons.Filled.School, ClassTeal, ClassTealLight)
        EntryType.ACTIVITY -> Triple(Icons.Filled.TaskAlt, ActivityPurple, ActivityPurpleLight)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(container, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = entry.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            if (entry.amount != null) {
                Text(
                    text = (if (entry.type == EntryType.SPENDING) "-" else "+") + formatMoney(entry.amount),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = tint
                )
            } else {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
            }
            Text(
                text = DateUtils.formatShortDate(entry.dateMillis),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
