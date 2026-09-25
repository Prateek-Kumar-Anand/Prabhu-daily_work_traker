package com.example.dailytracker.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.dailytracker.receiver.ClassReminderReceiver

/**
 * Schedules a best-effort, near-exact alarm (setAndAllowWhileIdle) that fires
 * when a logged class starts. Deliberately avoids the exact-alarm APIs so the
 * feature works without asking the user to grant the separate "Alarms &
 * reminders" special permission - a class reminder doesn't need
 * to-the-second precision.
 */
object ClassReminderScheduler {

    fun schedule(context: Context, classId: Long, subject: String, detail: String, atMillis: Long) {
        if (atMillis <= System.currentTimeMillis()) return // don't schedule for the past

        val intent = Intent(context, ClassReminderReceiver::class.java).apply {
            putExtra(ClassReminderReceiver.EXTRA_CLASS_ID, classId)
            putExtra(ClassReminderReceiver.EXTRA_SUBJECT, subject)
            putExtra(ClassReminderReceiver.EXTRA_DETAIL, detail)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            classId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, atMillis, pendingIntent)
    }

    fun cancel(context: Context, classId: Long) {
        val intent = Intent(context, ClassReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            classId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}
