package com.example.dailytracker

import android.app.Application
import com.example.dailytracker.data.AppDatabase
import com.example.dailytracker.data.repository.ActivityRepository
import com.example.dailytracker.data.repository.ClassRepository
import com.example.dailytracker.data.repository.ExpenseRepository
import com.example.dailytracker.util.NotificationHelper

/**
 * Holds the single Room database instance and the repositories built on top
 * of it. Everything runs locally on-device; there is no network or AI
 * integration anywhere in this app.
 */
class DailyTrackerApplication : Application() {

    private val database by lazy { AppDatabase.getInstance(this) }

    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }
    val classRepository by lazy { ClassRepository(database.classDao()) }
    val activityRepository by lazy { ActivityRepository(database.activityDao()) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
