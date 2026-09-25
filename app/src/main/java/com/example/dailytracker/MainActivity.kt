package com.example.dailytracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.dailytracker.navigation.AppNavigation
import com.example.dailytracker.ui.theme.DailyTrackerTheme
import com.example.dailytracker.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = ViewModelFactory(application as DailyTrackerApplication)

        setContent {
            DailyTrackerApp(factory = factory)
        }
    }
}

@Composable
fun DailyTrackerApp(factory: ViewModelFactory) {
    DailyTrackerTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavigation(factory = factory)
        }
    }
}
