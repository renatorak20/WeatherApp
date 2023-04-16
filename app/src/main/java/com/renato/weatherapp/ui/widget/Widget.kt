package com.renato.weatherapp.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.AppWidgetTarget
import com.renato.weatherapp.MainActivity
import com.renato.weatherapp.R
import com.renato.weatherapp.data.networking.Network
import com.renato.weatherapp.util.Preferences
import com.renato.weatherapp.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Widget : AppWidgetProvider() {

    private val apiKey = "6c0c76f140cf4673aaa80504230704"

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
            val myCity = Preferences(context).getMyCity()
            val response = Network().getService().getForecast(apiKey, myCity, 1)

            val views = RemoteViews(context.packageName, R.layout.widget_basic)

            views.setTextViewText(R.id.cityName, response.body()!!.location.name)
            views.setTextViewText(R.id.currentCondition, response.body()!!.current.condition.text)

            if (Preferences(context).getCurrentUnits()) {
                views.setTextViewText(
                    R.id.temperature,
                    context.getString(
                        R.string.temperatureMetricValue,
                        response.body()!!.current.temp_c.toInt()
                    )
                )
            } else {
                views.setTextViewText(
                    R.id.temperature,
                    context.getString(
                        R.string.temperatureImperialValue,
                        response.body()!!.current.temp_f.toInt()
                    )
                )
            }


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