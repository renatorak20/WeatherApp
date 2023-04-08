package com.renato.weatherapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renato.weatherapp.data.model.WeatherAutoCompleteResponse
import com.renato.weatherapp.data.model.FavouriteWeather
import com.renato.weatherapp.data.model.WeatherRecent
import com.renato.weatherapp.data.model.WeatherResponseForecast
import com.renato.weatherapp.data.networking.Network
import com.renato.weatherapp.database.WeatherApiDatabase
import com.renato.weatherapp.util.Utils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import retrofit2.Response

class SharedViewModel : ViewModel() {

    private val _autocompleteList =
        MutableLiveData<Response<ArrayList<WeatherAutoCompleteResponse>>>()
    private val _forecast = MutableLiveData<Response<WeatherResponseForecast>>()
    private val _favourites = MutableLiveData<List<FavouriteWeather>>()
    private val _recents = MutableLiveData<List<WeatherRecent>>()

    private val apiKey = "6c0c76f140cf4673aaa80504230704"

    fun setAutoCompleteList(results: Response<ArrayList<WeatherAutoCompleteResponse>>) {
        _autocompleteList.value = results
    }

    fun setForecast(results: Response<WeatherResponseForecast>) {
        _forecast.value = results
    }

    fun getCurrentFavourites(context: Context) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            val allFavourites = async { database?.weatherDao()?.getAllFavourites() }
            val newValue = allFavourites.await()
            _favourites.value = newValue!!
            for (city in _favourites.value!!) {
                Log.i("ITEM", city.cityName)
            }
        }
    }

    fun getCurrentRecents(context: Context) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            val allRecents = async { database?.weatherDao()?.getAllRecents() }
            _recents.value = allRecents.await()
        }
    }

    fun getAutoCompleteList(): MutableLiveData<Response<ArrayList<WeatherAutoCompleteResponse>>> {
        return _autocompleteList
    }

    fun getForecast(): MutableLiveData<Response<WeatherResponseForecast>> {
        return _forecast
    }

    fun getFavourites(): MutableLiveData<List<FavouriteWeather>> {
        if (_favourites.value?.isNotEmpty() == true) {
            for (city in _favourites.value!!) {
                Log.i("ITEM", city.cityName)
            }
        }
        return _favourites
    }

    fun getRecents(): MutableLiveData<List<WeatherRecent>> {
        return _recents
    }


    fun getNewForecast(city: String) {
        viewModelScope.launch {
            val forecastResponse = Network().getService().getForecast(apiKey, city, 8)
            if (forecastResponse.isSuccessful) {
                setForecast(forecastResponse)
            }
        }
    }

    fun getNewAutoCompleteList(city: String) {
        viewModelScope.launch {
            val response = Network().getService().getAutoComplete(apiKey, city)
            if (response.isSuccessful) {
                setAutoCompleteList(response)
            }
        }
    }

    fun addCityToFavourites(context: Context) {
        val newFavourite = getForecast().value?.body()?.let { Utils().weatherToFavourites(it) }
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            newFavourite?.let { database?.weatherDao()?.insertFavourite(it) }
            _favourites.value = database?.weatherDao()?.getAllFavourites()

            for (item in _favourites.value!!) {
                Log.i("FAVOURITE", item.cityName)
            }
        }
    }

    fun removeCityFromFavourites(context: Context, cityName: String) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            database?.weatherDao()?.deleteFavourite(cityName)
            _favourites.value = database?.weatherDao()?.getAllFavourites()

            for (item in _favourites.value!!) {
                Log.i("FAVOURITE", item.cityName)
            }
        }
    }

    fun addCityToRecents(context: Context) {
        val newRecent = getForecast().value?.body()?.let { Utils().weatherToFavourites(it) }
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            newRecent?.let { database?.weatherDao()?.insertFavourite(it) }
        }
    }

}