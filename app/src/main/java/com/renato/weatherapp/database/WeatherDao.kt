package com.renato.weatherapp.database

import androidx.room.*
import com.renato.weatherapp.data.model.FavouriteWeather
import com.renato.weatherapp.data.model.WeatherRecent

@Dao
interface WeatherDao {

    @Query("SELECT * FROM recentsTable")
    suspend fun getAllRecents(): List<WeatherRecent>

    @Query("SELECT * FROM favourite_weather_table")
    suspend fun getAllFavourites(): List<FavouriteWeather>

    @Query("SELECT * FROM recentsTable WHERE city_name = :cityName")
    suspend fun getRecentWeatherByCityName(cityName: String): List<WeatherRecent>

    @Query("SELECT * FROM favourite_weather_table WHERE city_name = :cityName")
    suspend fun getFavouriteWeatherByCityName(cityName: String): List<FavouriteWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(recent: WeatherRecent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: FavouriteWeather)

    @Query("DELETE FROM favourite_weather_table WHERE city_name = :cityName")
    suspend fun deleteFavourite(cityName: String)

    @Query("DELETE FROM recentsTable WHERE city_name = :cityName")
    suspend fun deleteRecent(cityName: String)

    @Query("DELETE FROM recentsTable")
    suspend fun nukeRecents()

    @Query("DELETE FROM favourite_weather_table")
    suspend fun nukeFavourites()

    @Update
    suspend fun updateFavourites(favs: List<FavouriteWeather>)
}