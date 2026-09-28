package com.prabhu.app.util

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.os.Process
import android.provider.Settings
import android.util.LruCache
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Immutable
data class AppUsage(
    val packageName: String,
    val appName: String,
    val foregroundMillis: Long,
    val durationText: String
)

/** Small, lazily-filled icon cache so scrolling never decodes full-size icons on the UI thread. */
object AppIconCache {
    private const val ICON_PX = 120
    private val cache = LruCache<String, ImageBitmap>(200)

    fun peek(packageName: String): ImageBitmap? = cache.get(packageName)

    suspend fun load(context: Context, packageName: String): ImageBitmap? {
        cache.get(packageName)?.let { return it }
        return withContext(Dispatchers.IO) {
            try {
                val raw = context.packageManager.getApplicationIcon(packageName).toBitmap(ICON_PX, ICON_PX)
                // Rounded corners are baked into the bitmap once, so drawing a row
                // needs no clipping at scroll time.
                val rounded = Bitmap.createBitmap(ICON_PX, ICON_PX, Bitmap.Config.ARGB_8888)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    shader = BitmapShader(raw, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                }
                val radius = ICON_PX / 4f
                Canvas(rounded).drawRoundRect(0f, 0f, ICON_PX.toFloat(), ICON_PX.toFloat(), radius, radius, paint)
                val bitmap = rounded.asImageBitmap()
                cache.put(packageName, bitmap)
                bitmap
            } catch (e: Exception) {
                null
            }
        }
    }
}

object UsageStatsHelper {

    /** The special "Usage access" permission can't be requested at runtime - it must be
     * granted by the user in system Settings, so this just checks the current state. */
    fun hasUsageAccess(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun openUsageAccessSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    /** Per-app foreground time since midnight today for ALL apps, longest first.
     * Excludes this app itself. */
    fun getTodayUsage(context: Context): List<AppUsage> {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val end = DateUtils.now()
        val start = DateUtils.startOfDay(end)

        val totals = totalsFromEvents(usm, start, end).ifEmpty { totalsFromAggregate(usm, start, end) }

        val pm = context.packageManager
        return totals
            .filter { it.value >= 1000L && it.key != context.packageName }
            .mapNotNull { (packageName, millis) ->
                try {
                    val label = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
                    AppUsage(packageName, label, millis, formatDuration(millis))
                } catch (e: Exception) {
                    null // uninstalled or inaccessible package; skip it
                }
            }
            .sortedByDescending { it.foregroundMillis }
    }

    /** Most accurate source (matches Digital Wellbeing): sums resumed->paused spans. */
    private fun totalsFromEvents(usm: UsageStatsManager, start: Long, end: Long): Map<String, Long> {
        val events = usm.queryEvents(start, end) ?: return emptyMap()
        val event = UsageEvents.Event()
        val totals = HashMap<String, Long>()
        val active = HashMap<String, Long>() // "package/class" -> resume time

        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            val pkg = event.packageName ?: continue
            val key = pkg + "/" + (event.className ?: "")
            when (event.eventType) {
                1 -> active[key] = event.timeStamp // ACTIVITY_RESUMED / MOVE_TO_FOREGROUND
                2, 23 -> { // ACTIVITY_PAUSED / MOVE_TO_BACKGROUND, ACTIVITY_STOPPED
                    val from = active.remove(key)
                    if (from != null && event.timeStamp > from) {
                        totals[pkg] = (totals[pkg] ?: 0L) + (event.timeStamp - from)
                    }
                }
                26 -> active.clear() // DEVICE_SHUTDOWN: drop unfinished sessions
            }
        }
        // App(s) still in the foreground right now.
        for ((key, from) in active) {
            val pkg = key.substringBefore('/')
            if (end > from) totals[pkg] = (totals[pkg] ?: 0L) + (end - from)
        }
        return totals
    }

    private fun totalsFromAggregate(usm: UsageStatsManager, start: Long, end: Long): Map<String, Long> {
        val stats = usm.queryAndAggregateUsageStats(start, end) ?: return emptyMap()
        return stats.mapValues { it.value.totalTimeInForeground }
    }

    fun formatDuration(millis: Long): String {
        val totalMinutes = millis / 60000
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            totalMinutes == 0L -> "<1m"
            else -> "${minutes}m"
        }
    }
}
