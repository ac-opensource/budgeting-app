package dev.pandesal.sbp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.pandesal.sbp.R
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@AndroidEntryPoint
class RecurringReminderService : Service() {

    @Inject lateinit var useCase: RecurringTransactionUseCase

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            val notifications = useCase.getUpcomingNotifications(LocalDate.now(), 1).first()
            notifications.forEach { push(it.message) }
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }

    private fun push(message: String) {
        val channelId = "recurring_reminders"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recurring Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val notify = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Upcoming Bill")
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        manager.notify(message.hashCode(), notify)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
