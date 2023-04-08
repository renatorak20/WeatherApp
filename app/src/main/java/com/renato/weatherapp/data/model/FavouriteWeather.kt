package com.renato.weatherapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@Entity(tableName = "favourite_weather_table")
data class FavouriteWeather(
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
    val icon: String
) {

    fun getCurrentTime(): String {

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

}
