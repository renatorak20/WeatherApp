package com.renato.weatherapp.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.renato.weatherapp.R
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderNotificationWorker(
    private val appContext: Context,
    workerParameters: WorkerParameters
) : Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        NotificationHandler.createReminderNotification(appContext)
        return Result.success()
    }

    companion object {

        fun schedule(appContext: Context) {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
            }

            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }

            val notificationRequest =
                PeriodicWorkRequestBuilder<ReminderNotificationWorker>(24, TimeUnit.HOURS)
                    .addTag(appContext.resources.getString(R.string.WORKER_TAG))
                    .setInitialDelay(
                        target.timeInMillis - System.currentTimeMillis(),
                        TimeUnit.MILLISECONDS
                    ).build()
            WorkManager.getInstance(appContext)
                .enqueueUniquePeriodicWork(
                    appContext.resources.getString(R.string.reminder_notification_work),
                    ExistingPeriodicWorkPolicy.REPLACE,
                    notificationRequest
                )
        }
    }

}
