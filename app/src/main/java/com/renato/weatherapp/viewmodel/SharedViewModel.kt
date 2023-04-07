package com.renato.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renato.weatherapp.data.model.WeatherAutoCompleteResponse
import com.renato.weatherapp.data.model.WeatherResponseForecast
import com.renato.weatherapp.data.networking.Network
import com.renato.weatherapp.data.networking.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

class SharedViewModel : ViewModel() {

    private val _autocompleteList =
        MutableLiveData<Response<ArrayList<WeatherAutoCompleteResponse>>>()
    private val _forecast = MutableLiveData<Response<WeatherResponseForecast>>()

    private val apiKey = "6c0c76f140cf4673aaa80504230704"

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


    fun getNewForecast(city: String) {
        viewModelScope.launch {
            val forecastResponse = Network().getService().getForecast(apiKey, city, 8)
            if (forecastResponse.isSuccessful) {
                setForecast(forecastResponse)
            }
        }
    }

    fun getNewAutoCompleteList(city:String) {
        viewModelScope.launch {
            val response = Network().getService().getAutoComplete(apiKey, city)
            if(response.isSuccessful){
                setAutoCompleteList(response)
            }
        }
    }

}