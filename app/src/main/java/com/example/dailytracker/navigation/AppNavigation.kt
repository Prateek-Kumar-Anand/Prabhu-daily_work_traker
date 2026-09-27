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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dailytracker.ui.components.BottomDestination
import com.example.dailytracker.ui.components.BottomNavBar
import com.example.dailytracker.ui.components.QuickAddSheet
import com.example.dailytracker.ui.screens.AddActivityScreen
import com.example.dailytracker.ui.screens.AddClassScreen
import com.example.dailytracker.ui.screens.AddExpenseScreen
import com.example.dailytracker.ui.screens.AppUsageScreen
import com.example.dailytracker.ui.screens.DashboardScreen
import com.example.dailytracker.ui.screens.HistoryScreen
import com.example.dailytracker.viewmodel.ViewModelFactory

private object Routes {
    const val DASHBOARD = "dashboard"
    const val HISTORY = "history"
    const val ADD_EXPENSE = "add_expense"
    const val ADD_CLASS = "add_class"
    const val EDIT_CLASS = "edit_class/{classId}"
    const val ADD_ACTIVITY = "add_activity"
    const val APP_USAGE = "app_usage"
}

@Composable
fun AppNavigation(factory: ViewModelFactory) {
    val navController: NavHostController = rememberNavController()
    var showQuickAdd by remember { mutableStateOf(false) }

    // Derived from the actual back stack (not a separately-tracked var) so it
    // stays correct on system back-press or an Add screen's own back arrow,
    // not just on bottom-nav taps.
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val selectedTab = if (currentRoute == Routes.HISTORY) BottomDestination.History else BottomDestination.Dashboard

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selected = selectedTab,
                onSelect = { destination ->
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
                    onViewAllTransactions = { navController.navigate(Routes.HISTORY) },
                    onViewAllActivities = { navController.navigate(Routes.HISTORY) },
                    onAddSpending = { navController.navigate(Routes.ADD_EXPENSE) },
                    onAddClass = { navController.navigate(Routes.ADD_CLASS) },
                    onAddActivity = { navController.navigate(Routes.ADD_ACTIVITY) },
                    onEditClass = { id -> navController.navigate("edit_class/$id") },
                    onOpenUsage = { navController.navigate(Routes.APP_USAGE) }
                )
            }
            composable(Routes.APP_USAGE) {
                AppUsageScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.HISTORY) {
                HistoryScreen(factory = factory)
            }
            composable(Routes.ADD_EXPENSE) {
                AddExpenseScreen(factory = factory, onBack = { navController.popBackStack() })
            }
            composable(Routes.ADD_CLASS) {
                AddClassScreen(factory = factory, onBack = { navController.popBackStack() })
            }
            composable(
                Routes.EDIT_CLASS,
                arguments = listOf(navArgument("classId") { type = NavType.LongType })
            ) { backStackEntry ->
                val classId = backStackEntry.arguments?.getLong("classId")
                AddClassScreen(factory = factory, classId = classId, onBack = { navController.popBackStack() })
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
            onAddClass = {
                showQuickAdd = false
                navController.navigate(Routes.ADD_CLASS)
            },
            onAddActivity = {
                showQuickAdd = false
                navController.navigate(Routes.ADD_ACTIVITY)
            }
        )
    }
}
