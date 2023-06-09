package com.renato.weatherapp.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renato.weatherapp.CityDetailActivity
import com.renato.weatherapp.R
import com.renato.weatherapp.data.model.WeatherAutoCompleteResponse
import com.renato.weatherapp.data.model.WeatherFavourite
import com.renato.weatherapp.data.model.WeatherRecent
import com.renato.weatherapp.data.model.WeatherResponseForecast
import com.renato.weatherapp.data.networking.Network
import com.renato.weatherapp.database.WeatherApiDatabase
import com.renato.weatherapp.util.Preferences
import com.renato.weatherapp.util.Utils
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import retrofit2.Response
import kotlin.streams.toList

class SharedViewModel : ViewModel() {

    private val _autocompleteList =
        MutableLiveData<Response<ArrayList<WeatherAutoCompleteResponse>>>()
    private val _forecast = MutableLiveData<Response<WeatherResponseForecast>>()
    private val _favourites = MutableLiveData<List<WeatherFavourite>>()
    private val _recents = MutableLiveData<List<WeatherRecent>>()
    private val _favLastUpdated = MutableLiveData<String>()
    private val _recLastUpdated = MutableLiveData<String>()

    fun setAutoCompleteList(results: Response<ArrayList<WeatherAutoCompleteResponse>>) {
        _autocompleteList.value = results
    }

    fun setForecast(results: Response<WeatherResponseForecast>) {
        _forecast.value = results
    }

    fun getAutoCompleteList(): MutableLiveData<Response<ArrayList<WeatherAutoCompleteResponse>>> {
        return _autocompleteList
    }

    fun getForecast(): MutableLiveData<Response<WeatherResponseForecast>> {
        return _forecast
    }

    fun getForecastValue(): WeatherResponseForecast {
        return getForecast().value!!.body()!!
    }

    fun getFavourites(): MutableLiveData<List<WeatherFavourite>> {
        return _favourites
    }

    fun getRecents(): MutableLiveData<List<WeatherRecent>> {
        return _recents
    }

    fun getFavLastUpdated(): MutableLiveData<String> {
        return _favLastUpdated
    }

    fun getRecLastUpdated(): MutableLiveData<String> {
        return _recLastUpdated
    }

    fun getFavouritesFromDb(context: Context) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            val allFavourites = async { database?.weatherDao()?.getAllFavourites() }
            val newValue = allFavourites.await()
            _favourites.value = newValue!!
        }
    }

    fun getRecentsFromDb(context: Context) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            val allRecents = async { database?.weatherDao()?.getAllRecents() }
            _recents.value = allRecents.await()
        }
    }

    fun getUpdatedFavourites(activity: Activity) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(activity.applicationContext)
            val allFavourites = database?.weatherDao()?.getAllFavourites()
            val newFavourites = allFavourites?.map { city ->
                async {
                    Network().getService().getForecast(
                        activity.applicationContext.resources.getString(
                            R.string.apiKey
                        ), city.cityName, 1
                    )
                }
            }
            val responses = newFavourites?.awaitAll()
            _favourites.value =
                responses?.stream()?.map { city -> Utils().weatherToFavourites(city.body()!!) }
                    ?.toList()
            _favourites.value?.let { database?.weatherDao()?.updateFavourites(it) }
            Preferences(activity).setFavouritesLastUpdated()
            setFavouritesLatestUpdate(activity)
        }
    }

    fun getUpdatedRecents(context: Context) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            val allRecents = database?.weatherDao()?.getAllRecents()
            val newRecents = allRecents?.map { city ->
                async {
                    Network().getService().getForecast(
                        context.resources.getString(
                            R.string.apiKey
                        ), city.cityName, 1
                    )
                }
            }
            val responses = newRecents?.awaitAll()
            val recents =
                responses?.stream()?.map { city -> Utils().weatherToRecent(city.body()!!) }
                    ?.toList()
            recents?.let { database.weatherDao().updateRecents(it) }
            _recents.value =
                responses?.stream()?.map { city -> Utils().weatherToRecent(city.body()!!) }
                    ?.toList()
        }
    }

    fun setFavouritesLatestUpdate(activity: Activity) {
        _favLastUpdated.value = Preferences(activity).getFavouritesLastUpdated()
    }

    fun getNewForecast(city: String, context: Context) {
        viewModelScope.launch {
            val forecastResponse = Network().getService().getForecast(
                context.resources.getString(
                    R.string.apiKey
                ), city, 8
            )
            if (forecastResponse.isSuccessful) {
                setForecast(forecastResponse)
            }
        }
    }

    fun getNewAutoCompleteList(city: String, context: Context) {
        viewModelScope.launch {
            val response = Network().getService().getAutoComplete(
                context.resources.getString(
                    R.string.apiKey
                ), city
            )
            if (response.isSuccessful) {
                setAutoCompleteList(response)
            }
        }
    }

    fun addCityToFavourites(context: Context, cityName: String) {
        viewModelScope.launch {
            val cityToAdd =
                async {
                    Network().getService().getForecast(
                        context.resources.getString(
                            R.string.apiKey
                        ), cityName, 1
                    )
                }

            val response = cityToAdd.await()
            val database = WeatherApiDatabase.getDatabase(context)
            database?.weatherDao()?.insertFavourite(Utils().weatherToFavourites(response.body()!!))
            _favourites.value = database?.weatherDao()?.getAllFavourites()
        }
    }

    fun removeCityFromFavourites(context: Context, cityName: String) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            database?.weatherDao()?.deleteFavourite(cityName)
            _favourites.value = database?.weatherDao()?.getAllFavourites()
        }
    }

    fun addCitiesInFravourites(context: Context, cities: List<WeatherFavourite>) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            database?.weatherDao()?.insertAll(cities)
        }
    }

    fun removeAllCitiesFromFavourites(context: Context) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            database?.weatherDao()?.nukeFavourites()
        }
    }

    fun addCityToRecents(context: Context) {

        val newRecent = getForecast().value?.body()?.let {
            Utils().weatherToRecent(it)
        }

        viewModelScope.launch {
            val databaseDao = WeatherApiDatabase.getDatabase(context)?.weatherDao()
            val recentsSize = databaseDao?.getAllRecents()?.size

            if (recentsSize == 5) {
                databaseDao.deleteRecent(databaseDao.getAllRecents()[0].cityName)
            }

            newRecent?.let { databaseDao?.insertRecent(it) }
            _recents.value = databaseDao?.getAllRecents()
        }
    }

    fun removeAllCitiesFromRecents(context: Context) {
        viewModelScope.launch {
            val database = WeatherApiDatabase.getDatabase(context)
            database?.weatherDao()?.nukeRecents()
        }
    }

    fun getFavouritesNames(): List<String>? {
        return _favourites.value?.map { city -> city.cityName }?.toList()
    }

}