package dev.pandesal.sbp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.pandesal.sbp.R
import dev.pandesal.sbp.domain.usecase.RecurringTransactionUseCase
import dev.pandesal.sbp.presentation.MainActivity
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val useCase: RecurringTransactionUseCase,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val notifications = useCase.getUpcomingNotifications(LocalDate.now(), 1).first()
        notifications.forEach { push(it.message) }
        return Result.success()
    }

    private fun push(message: String) {
        val channelId = "recurring_reminders"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recurring Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notify = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle("Upcoming Bill")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        manager.notify(message.hashCode(), notify)
    }

    companion object {
        const val WORK_NAME = "daily_reminder"
    }
}
