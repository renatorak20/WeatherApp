package com.renato.weatherapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_weather_table")
data class FavouriteWeather(
    @PrimaryKey
    @ColumnInfo(name = "city_name")
    val cityName: String,
    @ColumnInfo(name = "localtime_epoch")
    val localtimeEpoch: Int,
    @ColumnInfo(name = "tz_id")
    val tzId: String,
    @ColumnInfo(name = "temperature_c")
    val temperatureC: Int,
    @ColumnInfo(name = "temperature_f")
    val temperatureF: Int,
    @ColumnInfo(name = "icon")
    val icon: String
) {
}
