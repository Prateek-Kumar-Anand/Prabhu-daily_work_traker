package com.example.dailytracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.dailytracker.ui.theme.PrimaryBlue

enum class BottomDestination(val label: String) {
    Dashboard("Home"),
    Add("Add"),
    History("History")
}

@Composable
fun BottomNavBar(
    selected: BottomDestination,
    onSelect: (BottomDestination) -> Unit
) {
    NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White) {
        NavigationBarItem(
            selected = selected == BottomDestination.Dashboard,
            onClick = { onSelect(BottomDestination.Dashboard) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue
            )
        )
        NavigationBarItem(
            selected = selected == BottomDestination.Add,
            onClick = { onSelect(BottomDestination.Add) },
            icon = { Icon(Icons.Filled.AddCircle, contentDescription = "Add") },
            label = { Text("Add") },
            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue
            )
        )
        NavigationBarItem(
            selected = selected == BottomDestination.History,
            onClick = { onSelect(BottomDestination.History) },
            icon = { Icon(Icons.Filled.History, contentDescription = "History") },
            label = { Text("History") },
            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue
            )
        )
    }
}
