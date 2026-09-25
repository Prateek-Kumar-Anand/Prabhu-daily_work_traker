package com.example.dailytracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dailytracker.util.NotificationHelper

/**
 * Not exported (see AndroidManifest.xml) - only ever triggered by the
 * explicit PendingIntent this app schedules for itself in
 * [com.example.dailytracker.util.ClassReminderScheduler], never by another
 * app or an implicit broadcast.
 */
class ClassReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val classId = intent.getLongExtra(EXTRA_CLASS_ID, -1L)
        if (classId == -1L) return
        val subject = intent.getStringExtra(EXTRA_SUBJECT) ?: "Class"
        val detail = intent.getStringExtra(EXTRA_DETAIL) ?: ""
        NotificationHelper.showClassStarting(context, classId, subject, detail)
    }

    companion object {
        const val EXTRA_CLASS_ID = "extra_class_id"
        const val EXTRA_SUBJECT = "extra_subject"
        const val EXTRA_DETAIL = "extra_detail"
    }
}
