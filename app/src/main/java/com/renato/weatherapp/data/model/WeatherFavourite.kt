package com.renato.weatherapp.data.model

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.renato.weatherapp.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@Entity(tableName = "favouritesTable")
data class WeatherFavourite(
    @PrimaryKey
    @ColumnInfo(name = "city_name")
    val cityName: String,
    @ColumnInfo(name = "localtime")
    val localtime: String,
    @ColumnInfo(name = "tz_id")
    val tzId: String,
    @ColumnInfo(name = "temperature_c")
    val temperatureC: Int,
    @ColumnInfo(name = "temperature_f")
    val temperatureF: Int,
    @ColumnInfo(name = "icon")
    val icon: String,
    @ColumnInfo(name = "latitude")
    val latitude: Float,
    @ColumnInfo(name = "longitude")
    val longitude: Float
) {

    private fun getFullCurrentTime(): String {

        val zone = ZoneId.of(tzId)
        val offset = zone.rules.getOffset(Instant.now()) as ZoneOffset
        val hours = offset.totalSeconds / 3600
        val timezone =
            " (GMT${if (hours > 0) "+${abs(hours)}" else if (hours < 0) "-${abs(hours)}" else ""})"

        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm")
        val outputFormat = DateTimeFormatter.ofPattern("H:mm a")
        val dateTime = LocalDateTime.parse(localtime, inputFormat)

        return dateTime.format(outputFormat) + timezone

    }

    fun getCurrentTimezone(): String {
        val currentTime = getFullCurrentTime().split(" ")
        return currentTime[2].replace("(", "").replace(")", "")
    }

    fun getCurrentTime(context: Context): String {
        val currentTime = getFullCurrentTime().split(" ")
        return context.getString(R.string.timeText, currentTime[0], currentTime[1])
    }

}
