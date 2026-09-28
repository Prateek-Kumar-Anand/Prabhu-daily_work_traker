package com.prabhu.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.prabhu.app.ui.theme.ActivityPurple
import com.prabhu.app.ui.theme.CardWhite
import com.prabhu.app.ui.theme.ClassTeal
import com.prabhu.app.ui.theme.DividerGray
import com.prabhu.app.ui.theme.PrimaryBlue
import com.prabhu.app.ui.theme.SpendingRed
import com.prabhu.app.ui.theme.WarningAmber
import com.prabhu.app.util.AppIconCache
import com.prabhu.app.util.AppUsage
import com.prabhu.app.util.UsageStatsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val BarColors = listOf(PrimaryBlue, ClassTeal, ActivityPurple, SpendingRed, WarningAmber, Color(0xFF64748B))
private const val CHART_APPS = 6

@Composable
fun AppUsageScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var hasAccess by remember { mutableStateOf(UsageStatsHelper.hasUsageAccess(context)) }
    var isLoading by remember { mutableStateOf(false) }
    var usage by remember { mutableStateOf<List<AppUsage>>(emptyList()) }

    // Re-check permission whenever the screen resumes (e.g. coming back from the
    // system Usage Access settings screen).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasAccess = UsageStatsHelper.hasUsageAccess(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(hasAccess) {
        if (hasAccess) {
            isLoading = true
            usage = withContext(Dispatchers.IO) { UsageStatsHelper.getTodayUsage(context) }
            isLoading = false
        }
    }

    // Decode every icon off the UI thread up front (top apps first) so scrolling
    // only ever reads from the cache.
    LaunchedEffect(usage) {
        withContext(Dispatchers.IO) {
            usage.forEach { AppIconCache.load(context, it.packageName) }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(start = 4.dp, top = 4.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        Text(
            text = "App Usage Today",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
        )

        when {
            !hasAccess -> Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "To show how much time you've spent in each app today, Prabhu needs the " +
                        "\"Usage access\" permission. This only reads usage time on your device - nothing " +
                        "is sent anywhere.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { UsageStatsHelper.openUsageAccessSettings(context) },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Grant Access")
                }
            }
            isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            usage.isEmpty() -> Text(
                text = "No app usage recorded yet today.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            else -> {
                val maxMillis = remember(usage) { usage.first().foregroundMillis.coerceAtLeast(1L) }
                val totalMillis = remember(usage) { usage.sumOf { it.foregroundMillis } }
                val topApps = remember(usage) { usage.take(CHART_APPS) }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item(key = "chart", contentType = "chart") {
                        UsageBarChart(topApps, maxMillis, totalMillis, usage.size)
                    }
                    items(usage, key = { it.packageName }, contentType = { "row" }) { app ->
                        AppUsageRow(app, app.foregroundMillis.toFloat() / maxMillis)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppIcon(packageName: String, size: androidx.compose.ui.unit.Dp) {
    val context = LocalContext.current
    val icon by produceState<ImageBitmap?>(AppIconCache.peek(packageName), packageName) {
        if (value == null) value = AppIconCache.load(context, packageName)
    }
    val bitmap = icon
    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier.size(size)
        )
    } else {
        Box(modifier = Modifier.size(size))
    }
}

@Composable
private fun UsageBarChart(topApps: List<AppUsage>, maxMillis: Long, totalMillis: Long, appCount: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .background(CardWhite, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = UsageStatsHelper.formatDuration(totalMillis),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "total screen time across $appCount apps",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            topApps.forEachIndexed { index, app ->
                val fraction = (app.foregroundMillis.toFloat() / maxMillis).coerceIn(0.03f, 1f)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = app.durationText,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    // Bar area: the tallest bar fills 110dp.
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp * fraction)
                            .background(
                                BarColors[index % BarColors.size],
                                RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                            )
                    )
                    Box(modifier = Modifier.padding(top = 6.dp)) {
                        AppIcon(app.packageName, 28.dp)
                    }
                    Text(
                        text = app.appName,
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
    Text(
        text = "All apps",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp)
    )
    }
}

@Composable
private fun AppUsageRow(app: AppUsage, fraction: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(app.packageName, 36.dp)
            Text(
                text = app.appName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp)
            )
            Text(
                text = app.durationText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Track + fill drawn in a single pass (no nested boxes, no clip layers).
        val barFraction = fraction.coerceIn(0.01f, 1f)
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
                .height(6.dp)
                .drawBehind {
                    val radius = CornerRadius(size.height / 2f)
                    drawRoundRect(DividerGray, size = size, cornerRadius = radius)
                    drawRoundRect(
                        PrimaryBlue,
                        size = Size(size.width * barFraction, size.height),
                        cornerRadius = radius
                    )
                }
        )
    }
}
