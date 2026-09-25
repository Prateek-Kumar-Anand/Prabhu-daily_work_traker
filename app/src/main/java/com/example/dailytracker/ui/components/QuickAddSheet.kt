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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dailytracker.ui.theme.ActivityPurple
import com.example.dailytracker.ui.theme.ActivityPurpleLight
import com.example.dailytracker.ui.theme.ClassTeal
import com.example.dailytracker.ui.theme.ClassTealLight
import com.example.dailytracker.ui.theme.SpendingRed
import com.example.dailytracker.ui.theme.SpendingRedLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    onDismiss: () -> Unit,
    onAddSpending: () -> Unit,
    onAddClass: () -> Unit,
    onAddActivity: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(
                text = "What would you like to add?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            QuickAddOption(
                icon = Icons.Filled.ShoppingCart,
                label = "Record Spending",
                tint = SpendingRed,
                container = SpendingRedLight,
                onClick = onAddSpending
            )
            QuickAddOption(
                icon = Icons.Filled.School,
                label = "Add a Class",
                tint = ClassTeal,
                container = ClassTealLight,
                onClick = onAddClass
            )
            QuickAddOption(
                icon = Icons.Filled.TaskAlt,
                label = "Log an Activity",
                tint = ActivityPurple,
                container = ActivityPurpleLight,
                onClick = onAddActivity
            )
        }
    }
}

@Composable
private fun QuickAddOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: androidx.compose.ui.graphics.Color,
    container: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(container, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint)
        }
        Text(text = label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}
