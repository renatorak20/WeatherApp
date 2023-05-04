package com.renato.weatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.renato.weatherapp.data.model.WeatherFavourite
import com.renato.weatherapp.data.model.WeatherRecent

@Database(
    entities = [WeatherFavourite::class, WeatherRecent::class],
    version = 5,
    exportSchema = false
)
abstract class WeatherApiDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        private var dbInstance: WeatherApiDatabase? = null

        fun getDatabase(context: Context): WeatherApiDatabase? {
            if (dbInstance == null) {
                dbInstance = buildDatabase(context)
            }
            return dbInstance
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            WeatherApiDatabase::class.java,
            "WeatherApiDatabase.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

}