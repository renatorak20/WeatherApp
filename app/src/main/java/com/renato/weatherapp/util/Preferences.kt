package com.renato.weatherapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.renato.weatherapp.MainActivity
import com.renato.weatherapp.R


class Preferences(private val activity: Activity) {

    private val resources = activity.resources
    private val extrasUnit = resources.getStringArray(R.array.units)
    private val preferences = activity.getSharedPreferences(resources.getString(R.string.package_name), Context.MODE_PRIVATE)


    fun swapUnits(){

        when(preferences.getString(extrasUnit[0], extrasUnit[1])){
            extrasUnit[1] -> preferences.edit().putString(extrasUnit[0], extrasUnit[2]).apply()
            else -> preferences.edit().putString(extrasUnit[0], extrasUnit[1]).apply()
        }

        restartApp()
    }

    fun getCurrentUnits(): Boolean {
        return preferences.getString(extrasUnit[0], extrasUnit[1]).toString() == extrasUnit[1].toString()
    }

    private fun restartApp() {
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
        activity.finish()
    }

}
