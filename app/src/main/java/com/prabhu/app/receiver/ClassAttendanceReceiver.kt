package com.prabhu.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.prabhu.app.DailyTrackerApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Handles the "Yes" / "No" buttons on the "class is starting" notification
 * (see [com.prabhu.app.util.NotificationHelper]). Not exported - only ever
 * triggered by the PendingIntents this app creates for itself.
 */
class ClassAttendanceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val classId = intent.getLongExtra(EXTRA_CLASS_ID, -1L)
        if (classId == -1L) return

        // Tapping either button should dismiss the notification immediately.
        NotificationManagerCompat.from(context).cancel(classId.toInt())

        val attended = when (intent.action) {
            ACTION_MARK_ATTENDED -> true
            ACTION_MARK_MISSED -> false
            else -> return
        }

        // BroadcastReceiver.onReceive must return quickly, but writing to Room is
        // suspending - goAsync() keeps the receiver (and process) alive long enough
        // for the coroutine below to finish.
        val pendingResult = goAsync()
        val repository = (context.applicationContext as DailyTrackerApplication).classRepository
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.getById(classId)?.let { classEntry ->
                    repository.update(classEntry.copy(attended = attended))
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val EXTRA_CLASS_ID = "extra_class_id"
        const val ACTION_MARK_ATTENDED = "com.prabhu.app.action.MARK_ATTENDED"
        const val ACTION_MARK_MISSED = "com.prabhu.app.action.MARK_MISSED"
    }
}
