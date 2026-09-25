package com.example.dailytracker.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    /** Start of the day (00:00:00.000) for the given millis, same calendar day. */
    fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** End of the day (23:59:59.999) for the given millis, same calendar day. */
    fun endOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun startOfMonth(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun endOfMonth(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    /** The 7 days (Sun..Sat) that contain [millis], as start-of-day millis, oldest first. */
    fun weekOf(millis: Long): List<Long> {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return (0 until 7).map {
            val day = cal.clone() as Calendar
            day.add(Calendar.DAY_OF_MONTH, it)
            day.timeInMillis
        }
    }

    fun isSameDay(a: Long, b: Long): Boolean {
        val ca = Calendar.getInstance().apply { timeInMillis = a }
        val cb = Calendar.getInstance().apply { timeInMillis = b }
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR) &&
            ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR)
    }

    fun dayLetter(millis: Long): String =
        SimpleDateFormat("EEEEE", Locale.getDefault()).format(Date(millis))

    fun dayNumber(millis: Long): String =
        SimpleDateFormat("d", Locale.getDefault()).format(Date(millis))

    fun formatFullDate(millis: Long): String =
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date(millis))

    fun formatShortDate(millis: Long): String =
        SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(millis))

    fun now(): Long = System.currentTimeMillis()

    /** Today's date combined with the given hour/minute, as millis. */
    fun atTimeToday(hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun hourOf(millis: Long): Int = Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.HOUR_OF_DAY)

    fun minuteOf(millis: Long): Int = Calendar.getInstance().apply { timeInMillis = millis }.get(Calendar.MINUTE)

    fun formatTime(millis: Long): String =
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))
}
