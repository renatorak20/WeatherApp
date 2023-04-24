package com.renato.weatherapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.renato.weatherapp.MainActivity
import com.renato.weatherapp.R
import com.renato.weatherapp.data.model.WeatherResponseForecast
import com.renato.weatherapp.data.networking.Network
import com.renato.weatherapp.util.Preferences
import com.renato.weatherapp.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Widget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        for (appWidgetId in appWidgetIds!!) {
            updateAppWidget(context!!, appWidgetManager!!, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds =
            appWidgetManager.getAppWidgetIds(ComponentName(context!!, Widget::class.java))
        onUpdate(context, appWidgetManager, appWidgetIds)
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        CoroutineScope(Dispatchers.IO).launch {
            if (Preferences(context).getMyCity() != "") {
                val myCity = Preferences(context).getMyCity()
                val response = Network().getService()
                    .getForecast(context.resources.getString(R.string.apiKey), myCity, 2)
                val cityResponse = response.body()!!

                val views = RemoteViews(context.packageName, R.layout.widget_basic)

                if (Preferences(context).getCurrentUnits()) {
                    views.setTextViewText(
                        R.id.temperature,
                        context.getString(
                            R.string.temperatureMetricValue,
                            cityResponse.current.temp_c.toInt()
                        )
                    )
                } else {
                    views.setTextViewText(
                        R.id.temperature,
                        context.getString(
                            R.string.temperatureImperialValue,
                            cityResponse.current.temp_f.toInt()
                        )
                    )
                }

                views.setImageViewBitmap(
                    R.id.icon,
                    loadImage(context, cityResponse.current.condition.icon).get()
                )
                fillForecastHour(context, views, cityResponse)

                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.layout, pendingIntent)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    private fun fillForecastHour(
        context: Context,
        views: RemoteViews,
        city: WeatherResponseForecast
    ) {
        val hours = Utils().getNextThreeHoursConditions(city)
        val preferences = Preferences(context).getCurrentUnits()
        if (preferences) {
            views.setTextViewText(
                R.id.temperatureFirst,
                context.resources.getString(
                    R.string.temperatureMetricValue,
                    hours[0].temp_c.toInt()
                )
            )
            views.setTextViewText(
                R.id.temperatureSecond,
                context.resources.getString(
                    R.string.temperatureMetricValue,
                    hours[1].temp_c.toInt()
                )
            )
            views.setTextViewText(
                R.id.temperatureThird,
                context.resources.getString(
                    R.string.temperatureMetricValue,
                    hours[2].temp_c.toInt()
                )
            )
        } else {
            views.setTextViewText(
                R.id.temperatureFirst,
                context.resources.getString(
                    R.string.temperatureImperialValue,
                    hours[0].temp_f.toInt()
                )
            )
            views.setTextViewText(
                R.id.temperatureSecond,
                context.resources.getString(
                    R.string.temperatureImperialValue,
                    hours[1].temp_f.toInt()
                )
            )
            views.setTextViewText(
                R.id.temperatureThird,
                context.resources.getString(
                    R.string.temperatureImperialValue,
                    hours[2].temp_f.toInt()
                )
            )
        }

        views.setTextViewText(R.id.timeFirst, hours[0].getCurrentHour())
        views.setImageViewBitmap(
            R.id.weatherFirst,
            loadImage(context, hours[0].condition.icon).get()
        )
        views.setTextViewText(R.id.timeSecond, hours[1].getCurrentHour())
        views.setImageViewBitmap(
            R.id.weatherSecond,
            loadImage(context, hours[1].condition.icon).get()
        )
        views.setTextViewText(R.id.timeThird, hours[2].getCurrentHour())
        views.setImageViewBitmap(
            R.id.weatherThird,
            loadImage(context, hours[2].condition.icon).get()
        )

        if (preferences) {
            views.setTextViewText(
                R.id.minMax,
                context.resources.getString(
                    R.string.temperatureMetricMinMax,
                    city.forecast.forecastday[0].day.mintemp_c.toInt().toString(),
                    city.forecast.forecastday[0].day.maxtemp_c.toInt().toString()
                )
            )
        } else {
            views.setTextViewText(
                R.id.minMax,
                context.resources.getString(
                    R.string.temperatureImperialMinMax,
                    city.forecast.forecastday[0].day.mintemp_f.toInt().toString(),
                    city.forecast.forecastday[0].day.maxtemp_f.toInt().toString()
                )
            )
        }

        views.setTextViewText(R.id.cityName, city.location.name)
        views.setTextViewText(R.id.currentCondition, city.current.condition.text)
    }

    private fun loadImage(context: Context, cityIcon: String): FutureTarget<Bitmap> {
        return Glide.with(context)
            .asBitmap()
            .load(context.resources.getString(R.string.iconUrl, cityIcon))
            .submit(90, 90)
    }

}