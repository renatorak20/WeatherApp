package com.renato.weatherapp.util.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.renato.weatherapp.MainActivity
import com.renato.weatherapp.R
import com.renato.weatherapp.data.networking.Network
import com.renato.weatherapp.util.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object NotificationHandler {
    private const val CHANNEL_ID = "transactions_reminder_channel"

    @SuppressLint("MissingPermission")
    fun createReminderNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel(context)

        CoroutineScope(Dispatchers.IO).launch {

            if (Preferences(context).getMyCity() != "") {

                val call = async {
                    Network().getService().getForecast(
                        context.resources.getString(R.string.apiKey),
                        Preferences(context).getMyCity(),
                        1
                    )
                }
                val response = call.await()
                val city = response.body()!!

                val minMax = if (Preferences(context).getCurrentUnits()) {
                    context.resources.getString(
                        R.string.temperatureMetricMinMax,
                        city.forecast.forecastday[0].day.mintemp_c.toInt().toString(),
                        city.forecast.forecastday[0].day.maxtemp_c.toInt().toString()
                    )
                } else {
                    context.resources.getString(
                        R.string.temperatureImperialMinMax,
                        city.forecast.forecastday[0].day.mintemp_f.toInt().toString(),
                        city.forecast.forecastday[0].day.maxtemp_f.toInt().toString()
                    )
                }

                val condition = city.forecast.forecastday[0].day.condition

                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(context.resources.getString(R.string.iconUrl, condition.icon))
                    .build()
                val disposable = imageLoader.execute(request)

                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(city.location.name)
                    .setContentText("$minMax • ${condition.text}")
                    .setStyle(
                        NotificationCompat.BigPictureStyle()
                            .bigLargeIcon(disposable.drawable!!.toBitmap())
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

                with(NotificationManagerCompat.from(context)) {
                    notify(1, builder.build())
                }
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.resources.getString(R.string.channel_name),
            importance
        ).apply {
            description = context.resources.getString(R.string.channel_description)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
