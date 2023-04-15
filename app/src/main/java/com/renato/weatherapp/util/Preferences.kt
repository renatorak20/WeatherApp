package com.renato.weatherapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.android.gms.maps.model.LatLng
import com.renato.weatherapp.MainActivity
import com.renato.weatherapp.R


class Preferences(private val context: Context) {

    private val extrasUnit = context.resources.getStringArray(R.array.units)
    private val extrasLang = context.resources.getStringArray(R.array.languages)
    private val extrasCoord = context.resources.getStringArray(R.array.latlng)
    private val extrasNotification = context.resources.getStringArray(R.array.notifications)
    private val preferences = context.getSharedPreferences(
        context.resources.getString(R.string.package_name),
        Context.MODE_PRIVATE
    )
    private val resources = context.resources


    fun swapUnits(activity: Activity) {
        when (preferences.getString(extrasUnit[0], extrasUnit[1])) {
            extrasUnit[1] -> preferences.edit().putString(extrasUnit[0], extrasUnit[2]).apply()
            else -> preferences.edit().putString(extrasUnit[0], extrasUnit[1]).apply()
        }
        Utils().restartApp(activity)
    }

    fun setLanguage(language: String, activity: Activity) {
        if (language != preferences.getString(extrasLang[0], extrasUnit[1])) {
            preferences.edit().putString(extrasLang[0], language).apply()
            Utils().restartApp(activity)
        }
    }

    fun loadAppLanguage() {
        when (preferences.getString(extrasLang[0], extrasLang[1])) {
            extrasLang[1] -> setAppLocale(extrasLang[1])
            else -> setAppLocale(extrasLang[2])
        }
    }

    fun getPreferedLatLng(): LatLng {
        val lat = preferences.getFloat(extrasCoord[1], 0.0F)
        val lon = preferences.getFloat(extrasCoord[2], 0.0F)
        return LatLng(lat.toDouble(), lon.toDouble())
    }

    fun getCurrentUnits(): Boolean {
        return preferences.getString(extrasUnit[0], extrasUnit[1])
            .toString() == extrasUnit[1].toString()
    }


    private fun setAppLocale(language: String) {
        preferences.edit().putString(extrasLang[0], language).apply()
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun setFavouritesLastUpdated() {
        preferences.edit().putString(
            resources.getString(R.string.favouritesLastUpdated),
            resources.getString(R.string.lastUpdated, Utils().getCurrentTime())
        ).apply()
    }

    fun getFavouritesLastUpdated(): String? {
        return preferences.getString(
            resources.getString(R.string.favouritesLastUpdated),
            resources.getString(R.string.notUpdated)
        )
    }

    fun setMyCity(cityName: String, lat: Float, lon: Float) {
        preferences.edit().putString(resources.getString(R.string.myCity), cityName).apply()
        preferences.edit().putFloat(extrasCoord[1], lat).apply()
        preferences.edit().putFloat(extrasCoord[2], lon).apply()
    }

    fun getMyCity(): String {
        return preferences.getString(resources.getString(R.string.myCity), "")!!
    }

    fun setNotificationsTime(hour: Int, minute: Int) {
        preferences.edit().putInt(resources.getString(R.string.notificationsHour), hour).apply()
        preferences.edit().putInt(resources.getString(R.string.notificationsMinute), minute).apply()
    }

    fun getNotificationsTime(): Pair<Int, Int> {
        return Pair(
            preferences.getInt(resources.getString(R.string.notificationsHour), 0),
            preferences.getInt(resources.getString(R.string.notificationsHour), 0)
        )
    }

}