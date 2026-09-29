package com.prabhu.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.prabhu.app.MainActivity
import com.prabhu.app.receiver.ClassAttendanceReceiver
import com.prabhu.app.R

object NotificationHelper {
    const val CHANNEL_ID = "class_reminders"
    private const val CHANNEL_NAME = "Class reminders"
    private const val CHANNEL_DESCRIPTION = "Alerts you when a logged class is starting"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun showClassStarting(context: Context, classId: Long, subject: String, detail: String) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            classId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        fun actionIntent(action: String, requestCode: Int): PendingIntent {
            val intent = Intent(context, ClassAttendanceReceiver::class.java).apply {
                this.action = action
                putExtra(ClassAttendanceReceiver.EXTRA_CLASS_ID, classId)
            }
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val yesAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_send,
            "Yes",
            actionIntent(ClassAttendanceReceiver.ACTION_MARK_ATTENDED, classId.toInt() * 2)
        ).build()
        val noAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_close_clear_cancel,
            "No",
            actionIntent(ClassAttendanceReceiver.ACTION_MARK_MISSED, classId.toInt() * 2 + 1)
        ).build()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$subject is starting")
            .setContentText(if (detail.isBlank()) "Going to class?" else "Going to class? \u2022 $detail")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .addAction(yesAction)
            .addAction(noAction)
            .build()

        NotificationManagerCompat.from(context).notify(classId.toInt(), notification)
    }
}
