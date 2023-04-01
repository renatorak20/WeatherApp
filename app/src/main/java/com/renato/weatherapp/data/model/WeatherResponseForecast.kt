package com.renato.weatherapp.data.model

import android.content.Context
import android.content.res.Resources
import android.provider.Settings.Global.getString
import com.renato.weatherapp.R
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList
import kotlin.math.abs

data class WeatherResponseForecast(
    val location: Location,
    val current: Current,
    val forecast: Forecast
) : Serializable

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val localtime_epoch: Int,
    val localtime: String,
    val tz_id: String
) : Serializable {

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("EEE, MMMM dd")
        return dateFormat.format(localtime_epoch)
    }

    fun getCurrentTime(): String {

        val zone = ZoneId.of(tz_id)
        val offset = zone.rules.getOffset(Instant.now()) as ZoneOffset
        val hours = offset.totalSeconds / 3600
        val timezone =
            " (GMT${if (hours > 0) "+${abs(hours)}" else if (hours < 0) "-${abs(hours)}" else ""})"

        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm")
        val outputFormat = DateTimeFormatter.ofPattern("H:mm a")
        val dateTime = LocalDateTime.parse(localtime, inputFormat)

        return dateTime.format(outputFormat) + timezone

    }

    fun getCurrentHour(): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm")
        val dateTime = LocalDateTime.parse(localtime, formatter)
        return dateTime.hour
    }

}

data class Current(
    val last_updated_epoch: Int,
    val last_updated: String,
    val temp_c: Double,
    val temp_f: Double,
    val is_day: Int,
    val condition: Condition,
    val wind_mph: Double,
    val wind_kph: Double,
    val wind_degree: Int,
    val wind_dir: String,
    val pressure_mb: Double,
    val pressure_in: Double,
    val precip_mm: Double,
    val precip_in: Double,
    val humidity: Int,
    val cloud: Int,
    val feelslike_c: Double,
    val feelslike_f: Double,
    val vis_km: Double,
    val vis_miles: Double,
    val uv: Int,
    val gust_mph: Double,
    val gust_kph: Double
) : Serializable

data class Condition(
    val text: String,
    val icon: String,
    val code: Int
) : Serializable

data class Forecast(val forecastday: ArrayList<ForecastDay>) : Serializable {
}

data class ForecastDay(
    val date: String,
    val date_epoch: Int,
    val day: Day,
    val hour: ArrayList<Hour>
) : Serializable {

    fun getDayInWeek(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(date, formatter)
        val dayOfWeek = date.dayOfWeek.toString().substring(0, 3)
        return dayOfWeek
    }

}

data class Day(
    val maxtemp_c: Double,
    val maxtemp_f: Double,
    val mintemp_c: Double,
    val mintemp_f: Double,
    val maxwind_mph: Double,
    val maxwind_kph: Double,
    val avghumidity: Int,
    val condition: Condition
) : Serializable

data class Hour(
    val time: String,
    val time_epoch: Int,
    val temp_c: Double,
    val temp_f: Double,
    val condition: Condition,
    val wind_mph: Double,
    val wind_kph: Double,
    val wind_dir: String,
    val pressure_mb: Double,
    val pressure_in: Double,
    val humidity: Int,
    val vis_km: Double,
    val vis_miles: Double
) : Serializable {

    fun getCurrentHour(): String {
        val dateTime = LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun getMetricWind(context: Context): String {
        var wind_direction: String
        var res = context.resources
        return if (wind_dir.length == 3) {
            wind_direction = getWindDirection(res)
            res.getString(R.string.windMetricValue, wind_kph.toInt(), wind_direction)
        } else {
            res.getString(R.string.windMetricValue, wind_kph, wind_dir)
        }
    }

    fun getImperialWind(context: Context): String {
        var wind_direction: String
        var res = context.resources
        return if (wind_dir.length == 3) {
            wind_direction = getWindDirection(res)
            res.getString(R.string.windImperialValue, wind_mph.toInt(), wind_direction)
        } else {
            res.getString(R.string.windImperialValue, wind_kph.toInt(), wind_dir)
        }
    }

    fun getWindDirection(res: Resources): String {
        return res.getString(
            R.string.windDirectionValue,
            wind_dir.substring(0, 1),
            wind_dir.substring(1)
        )
    }

}


