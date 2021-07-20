package com.example.weather.api

import com.example.weather.model.CurrentWeather
import com.example.weather.model.FullWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("onecall")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "minutely,alerts",
        @Query("units") units: String = UNITS,
        @Query("appid") apiKey: String = API_KEY
    ): Call<FullWeather>

    @GET("weather")
    fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String = UNITS,
        @Query("appid") apiKey: String = API_KEY
    ): Call<CurrentWeather>

    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = UNITS,
        @Query("appid") apiKey: String = API_KEY
    ): Call<CurrentWeather>

}