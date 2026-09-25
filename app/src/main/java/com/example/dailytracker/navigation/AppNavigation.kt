package com.example.dailytracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dailytracker.ui.components.BottomDestination
import com.example.dailytracker.ui.components.BottomNavBar
import com.example.dailytracker.ui.components.QuickAddSheet
import com.example.dailytracker.ui.screens.AddActivityScreen
import com.example.dailytracker.ui.screens.AddExpenseScreen
import com.example.dailytracker.ui.screens.AddSaleScreen
import com.example.dailytracker.ui.screens.DashboardScreen
import com.example.dailytracker.ui.screens.HistoryScreen
import com.example.dailytracker.viewmodel.ViewModelFactory

private object Routes {
    const val DASHBOARD = "dashboard"
    const val HISTORY = "history"
    const val ADD_EXPENSE = "add_expense"
    const val ADD_SALE = "add_sale"
    const val ADD_ACTIVITY = "add_activity"
}

@Composable
fun AppNavigation(factory: ViewModelFactory) {
    val navController: NavHostController = rememberNavController()
    var selectedTab by remember { mutableStateOf(BottomDestination.Dashboard) }
    var showQuickAdd by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selected = selectedTab,
                onSelect = { destination ->
                    selectedTab = destination
                    when (destination) {
                        BottomDestination.Dashboard -> navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.DASHBOARD) { inclusive = true }
                        }
                        BottomDestination.History -> navController.navigate(Routes.HISTORY) {
                            popUpTo(Routes.DASHBOARD)
                        }
                        BottomDestination.Add -> showQuickAdd = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    factory = factory,
                    onViewAllTransactions = {
                        selectedTab = BottomDestination.History
                        navController.navigate(Routes.HISTORY)
                    },
                    onViewAllActivities = {
                        selectedTab = BottomDestination.History
                        navController.navigate(Routes.HISTORY)
                    },
                    onAddSpending = { navController.navigate(Routes.ADD_EXPENSE) },
                    onAddSale = { navController.navigate(Routes.ADD_SALE) },
                    onAddActivity = { navController.navigate(Routes.ADD_ACTIVITY) }
                )
            }
            composable(Routes.HISTORY) {
                HistoryScreen(factory = factory)
            }
            composable(Routes.ADD_EXPENSE) {
                AddExpenseScreen(factory = factory, onBack = { navController.popBackStack() })
            }
            composable(Routes.ADD_SALE) {
                AddSaleScreen(factory = factory, onBack = { navController.popBackStack() })
            }
            composable(Routes.ADD_ACTIVITY) {
                AddActivityScreen(factory = factory, onBack = { navController.popBackStack() })
            }
        }
    }

    if (showQuickAdd) {
        QuickAddSheet(
            onDismiss = { showQuickAdd = false },
            onAddSpending = {
                showQuickAdd = false
                navController.navigate(Routes.ADD_EXPENSE)
            },
            onAddSale = {
                showQuickAdd = false
                navController.navigate(Routes.ADD_SALE)
            },
            onAddActivity = {
                showQuickAdd = false
                navController.navigate(Routes.ADD_ACTIVITY)
            }
        )
    }
}
