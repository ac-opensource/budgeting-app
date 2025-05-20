package dev.pandesal.sbp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import dev.pandesal.sbp.R
import dev.pandesal.sbp.presentation.MainActivity
import dev.pandesal.sbp.notification.InAppNotificationCenter
import dev.pandesal.sbp.domain.model.NotificationType

class NotificationTransactionListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        val extras = notification.extras
        val title = extras.getString(android.app.Notification.EXTRA_TITLE) ?: ""
        val text = extras.getString(android.app.Notification.EXTRA_TEXT) ?: ""
        if (isFinancialNotification(title, text)) {
            suggestTransaction(text)
        }
    }

    private fun isFinancialNotification(title: String, text: String): Boolean {
        val pattern = Regex(
            "(?i)(\\bpaid\\b|\\bpurchase\\b|\\bdeposit\\b|\\bspent\\b|\\bpayment\\b|\\bdebit\\b|" +
                "\\bcredited\\b|\\bbalance\\b|\\bwithdraw\\b|\\bsent\\b|\\btransfer\\b|\\bcash\\s?in\\b|" +
                "\\bcashout\\b|\\binstapay\\b|\\bpesonet\\b|\\bpay\\b|\\bcharge\\b|\\breceived\\b|₱|PHP)"
        )
        return pattern.containsMatchIn(title) || pattern.containsMatchIn(text)
    }

    private fun suggestTransaction(message: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "transaction_suggestions"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Transaction Suggestions",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notify = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Create a transaction?")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        manager.notify(System.currentTimeMillis().toInt(), notify)

        InAppNotificationCenter.postNotification(
            message = message,
            type = NotificationType.TRANSACTION_SUGGESTION,
            canCreateTransaction = true
        )
    }
}
