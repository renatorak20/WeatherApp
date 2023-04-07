package com.renato.weatherapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recentsTable")
data class WeatherRecent(
    @PrimaryKey
    @ColumnInfo(name = "city_name")
    val cityname: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double,
    @ColumnInfo(name = "distance_km")
    val distance_km: Int,
    @ColumnInfo(name = "distance_mil")
    val distance_mil: Int,
    @ColumnInfo(name = "temperature_c")
    val temperature_c: Int,
    @ColumnInfo(name = "temeperature_f")
    val temperature_f: Int,
    @ColumnInfo(name = "icon")
    val icon: String
)