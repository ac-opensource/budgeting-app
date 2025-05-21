package dev.pandesal.sbp.notification

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import dev.pandesal.sbp.domain.model.NotificationType
import dev.pandesal.sbp.notification.InAppNotificationCenter

class FinancialAppUsageService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        detectFinancialApps()
        stopSelf(startId)
        return START_NOT_STICKY
    }

    private fun detectFinancialApps() {
        val manager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return
        val now = System.currentTimeMillis()
        val events = manager.queryEvents(now - CHECK_INTERVAL_MS, now)
        val event = UsageEvents.Event()
        val detectedPackages = mutableSetOf<String>()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND ||
                event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                if (FINANCIAL_KEYWORDS.any { event.packageName.contains(it, ignoreCase = true) }) {
                    detectedPackages.add(event.packageName)
                }
            }
        }
        detectedPackages.forEach { pkg ->
            val appName = try {
                val info = packageManager.getApplicationInfo(pkg, 0)
                packageManager.getApplicationLabel(info).toString()
            } catch (_: Exception) {
                pkg
            }
            InAppNotificationCenter.postNotification(
                message = "Did you transact with $appName?",
                type = NotificationType.TRANSACTION_SUGGESTION,
                canCreateTransaction = true
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHECK_INTERVAL_MS = 5 * 60 * 1000L // 5 minutes
        val FINANCIAL_KEYWORDS = listOf(
            "bpi", "bdo", "metrobank", "securitybank", "maybank",
            "eastwest", "rcbc", "unionbank", "gcash", "maya",
            "pnb", "landbank", "chinabank", "psbank", "coins",
            "tonik", "ing", "seabank", "gotyme", "diskartech",
            "komo", "grabpay"
        )
    }
}
