package com.renato.weatherapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.android.gms.maps.model.LatLng
import com.renato.weatherapp.MainActivity
import com.renato.weatherapp.R


class Preferences(private val activity: Activity) {

    private val extrasUnit = activity.resources.getStringArray(R.array.units)
    private val extrasLang = activity.resources.getStringArray(R.array.languages)
    private val extrasCoord = activity.resources.getStringArray(R.array.latlng)
    private val preferences = activity.getSharedPreferences(
        activity.resources.getString(R.string.package_name),
        Context.MODE_PRIVATE
    )
    private val resources = activity.resources


    fun swapUnits(){
        when(preferences.getString(extrasUnit[0], extrasUnit[1])){
            extrasUnit[1] -> preferences.edit().putString(extrasUnit[0], extrasUnit[2]).apply()
            else -> preferences.edit().putString(extrasUnit[0], extrasUnit[1]).apply()
        }
        restartApp()
    }

    fun setLanguage(language: String){
        if(language != preferences.getString(extrasLang[0], extrasUnit[1])){
            preferences.edit().putString(extrasLang[0], language).apply()
            restartApp()
        }
    }

    fun loadAppLanguage() {
        when (preferences.getString(extrasLang[0], extrasLang[1])) {
            extrasLang[1] -> setAppLocale(extrasLang[1])
            else -> setAppLocale(extrasLang[2])
        }
    }

    fun getPreferedLatLng(): LatLng {
        val lat = preferences.getFloat(extrasCoord[0], 0.0F)
        val lon = preferences.getFloat(extrasCoord[1], 0.0F)
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

    private fun restartApp() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()
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

}