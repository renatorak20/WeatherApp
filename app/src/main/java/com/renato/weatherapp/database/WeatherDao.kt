package com.renato.weatherapp.database

import androidx.room.*
import com.renato.weatherapp.data.model.WeatherFavourite
import com.renato.weatherapp.data.model.WeatherRecent

@Dao
interface WeatherDao {

    @Query("SELECT * FROM recentsTable")
    suspend fun getAllRecents(): List<WeatherRecent>

    @Query("SELECT * FROM favouritesTable")
    suspend fun getAllFavourites(): List<WeatherFavourite>

    @Query("SELECT * FROM recentsTable WHERE city_name = :cityName")
    suspend fun getRecentWeatherByCityName(cityName: String): List<WeatherRecent>

    @Query("SELECT * FROM favouritesTable WHERE city_name = :cityName")
    suspend fun getFavouriteWeatherByCityName(cityName: String): List<WeatherFavourite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(recent: WeatherRecent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourite(favourite: WeatherFavourite)

    @Query("DELETE FROM favouritesTable WHERE city_name = :cityName")
    suspend fun deleteFavourite(cityName: String)

    @Query("DELETE FROM recentsTable WHERE city_name = :cityName")
    suspend fun deleteRecent(cityName: String)

    @Query("DELETE FROM recentsTable")
    suspend fun nukeRecents()

    @Query("DELETE FROM favouritesTable")
    suspend fun nukeFavourites()

    @Update
    suspend fun updateFavourites(favs: List<WeatherFavourite>)
}