package com.renato.weatherapp.data.networking

import com.renato.weatherapp.data.model.WeatherAutoCompleteResponse
import com.renato.weatherapp.data.model.WeatherResponseForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("forecast.json")
    suspend fun getForecast(@Query("key") key:String,
                            @Query("q") location:String,
                            @Query("days") days:Int): Response<WeatherResponseForecast>

    @GET("search.json")
    suspend fun getAutoComplete(@Query("key") key: String,
                                @Query("q") text:String): Response<ArrayList<WeatherAutoCompleteResponse>>
}