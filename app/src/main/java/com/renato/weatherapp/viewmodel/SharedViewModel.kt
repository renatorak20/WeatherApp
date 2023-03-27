package com.renato.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renato.weatherapp.data.model.WeatherAutoCompleteResponse
import com.renato.weatherapp.data.model.WeatherResponseForecast
import com.renato.weatherapp.data.networking.Network
import kotlinx.coroutines.launch
import retrofit2.Response

class SharedViewModel : ViewModel() {

    private val _autocompleteList = MutableLiveData<Response<ArrayList<WeatherAutoCompleteResponse>>>()
    private val _forecast = SingleLiveEvent<Response<WeatherResponseForecast>>()

    private val apiKey = "b0cdddd07d474c2ba10190718232303"

    fun setAutoCompleteList(results:Response<ArrayList<WeatherAutoCompleteResponse>>){
        _autocompleteList.value = results
    }
    fun setForecast(results: Response<WeatherResponseForecast>){
        _forecast.value = results
    }

    fun getAutoCompleteList(): MutableLiveData<Response<ArrayList<WeatherAutoCompleteResponse>>> {
        return _autocompleteList
    }
    fun getForecast(): SingleLiveEvent<Response<WeatherResponseForecast>> {
        return _forecast
    }


    fun getNewForecast(city:String){
        viewModelScope.launch {
            val forecastResponse = Network().getService().getForecast(apiKey, city, 8)
            if(forecastResponse.isSuccessful){
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