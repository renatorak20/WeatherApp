package com.renato.weatherapp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.renato.weatherapp.R
import com.renato.weatherapp.data.model.Hour
import com.renato.weatherapp.data.model.FavouriteWeather
import com.renato.weatherapp.data.model.WeatherRecent
import com.renato.weatherapp.data.model.WeatherResponseForecast
import com.renato.weatherapp.ui.custom.CityDetailParameter

class Utils {


    @SuppressLint("MissingPermission")
    fun isNetworkAvailable(context: Context): Boolean {
        val conManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = conManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun showErrorDialog(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.error))
            .setMessage(context.getString(R.string.httpError))
            .show()
    }

    fun getCurrentConditions(city: WeatherResponseForecast): Hour {
        return city.forecast.forecastday[0].hour[city.location.getCurrentHour()]
    }

    fun fillCityDetailParameters(
        activity: Activity,
        views: Sequence<CityDetailParameter>,
        city: WeatherResponseForecast
    ) {

        val resources = activity.applicationContext.resources
        val currentConditions = getCurrentConditions(city)

        val values: List<String> = if (Preferences(activity).getCurrentUnits()) {
            listOf(
                resources.getString(
                    R.string.temperatureMetricMinMax,
                    city.forecast.forecastday[0].day.mintemp_c.toInt().toString(),
                    city.forecast.forecastday[0].day.maxtemp_c.toInt().toString()
                ),
                currentConditions.getMetricWind(activity.applicationContext),
                resources.getString(R.string.humidityValue, currentConditions.humidity),
                resources.getString(
                    R.string.pressureMetricValue,
                    currentConditions.pressure_mb.toInt()
                ),
                resources.getString(
                    R.string.visibilityMetricValue,
                    currentConditions.vis_km.toInt()
                ),
                resources.getString(R.string.accuracyValue)
            )
        } else {
            listOf(
                resources.getString(
                    R.string.temperatureImperialMinMax,
                    city.forecast.forecastday[0].day.mintemp_f.toInt().toString(),
                    city.forecast.forecastday[0].day.maxtemp_f.toInt().toString()
                ),
                currentConditions.getImperialWind(activity.applicationContext),
                resources.getString(R.string.humidityValue, currentConditions.humidity),
                resources.getString(
                    R.string.pressureImperialValue,
                    currentConditions.pressure_in.toInt()
                ),
                resources.getString(
                    R.string.visibilityImperialValue,
                    currentConditions.vis_miles.toInt()
                ),
                resources.getString(R.string.accuracyValue)
            )
        }

        views.forEachIndexed { index, element ->
            element.setValue(values[index])
        }
    }

    fun fillCityHeader(
        activity: Activity,
        views: Sequence<TextView>,
        city: WeatherResponseForecast
    ) {

        val resources = activity.applicationContext.resources

        var values = mutableListOf(
            city.location.getCurrentDate(),
            city.location.getCurrentTime(),
            city.current.condition.text
        )

        if (Preferences(activity).getCurrentUnits()) {
            values.add(
                0,
                resources.getString(R.string.temperatureMetricValue, city.current.temp_c.toInt())
            )
        } else {
            values.add(
                0,
                resources.getString(R.string.temperatureImperialValue, city.current.temp_f.toInt())
            )
        }

        views.forEachIndexed { index, element ->
            element.text = values[index]
        }
    }

    fun setCityMainIcon(activity: Activity, imageView: ImageView, city: WeatherResponseForecast) {
        imageView.load(activity.resources.getString(R.string.iconUrl, city.current.condition.icon))
    }

    fun weatherToFavourites(weather: WeatherResponseForecast): FavouriteWeather {
        val newWeather = FavouriteWeather(
            weather.location.name,
            weather.location.localtime,
            weather.location.tz_id,
            weather.current.temp_c.toInt(),
            weather.current.temp_f.toInt(),
            weather.current.condition.icon
        )
        return newWeather
    }

    fun weatherToRecent(weather: WeatherResponseForecast, activity: Activity): WeatherRecent {

        val preferedLatLng = Preferences(activity).getPreferedLatLng()

        val newWeather = WeatherRecent(
            weather.location.name,
            weather.location.lat,
            weather.location.lon,
            getDistanceKm(weather.location.lat, weather.location.lon, preferedLatLng),
            getDistanceMil(weather.location.lat, weather.location.lon, preferedLatLng),
            weather.current.temp_c.toInt(),
            weather.current.temp_f.toInt(),
            weather.current.condition.icon
        )
        return newWeather
    }

    fun getDistanceKm(lat: Double, lon: Double, latLng: LatLng): Int {
        val results = FloatArray(1)
        Location.distanceBetween(lat, lon, latLng.latitude, latLng.longitude, results)
        Log.i("RESULTS", results[0].toString())
        return (results[0] / 1000).toInt()
    }

    fun getDistanceMil(lat: Double, lon: Double, latLng: LatLng): Int {
        val results = FloatArray(1)
        Location.distanceBetween(lat, lon, latLng.latitude, latLng.longitude, results)
        Log.i("RESULTS", results[0].toString())
        return (results[0] * 0.000621371).toInt()
    }
}