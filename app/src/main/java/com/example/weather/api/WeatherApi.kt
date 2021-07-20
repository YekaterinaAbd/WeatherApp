package com.example.weather.api

import com.example.weather.model.CurrentWeather
import com.example.weather.model.FullWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Add this interface to Retrofit to build REST API requests
interface WeatherApi {

    //API call - https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude={part}&appid={API key}
    //https://api.openweathermap.org/data/2.5/ - base URL
    //onecall - keyword
    //lat={lat} - query (location latitude)
    //lon={lon} - query (location longitude)
    //exclude={part} - exclude some parts of call
    //units - default is Fahrenheit but we need Celsius to use this query to define it
    //appid={API key} - unique API key of a user

    //onecall request gets all weather data in one: current weather, hourly weather, daily weather
    @GET("onecall")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "minutely,alerts",
        @Query("units") units: String = UNITS,
        @Query("appid") apiKey: String = API_KEY
    ): Call<FullWeather>

    //api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
    //request to get current weather by city name
    @GET("weather")
    fun getCurrentWeather(
        @Query("q") city: String,
        @Query("units") units: String = UNITS,
        @Query("appid") apiKey: String = API_KEY
    ): Call<CurrentWeather>

    //api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
    //request to get current weather by latitude and longitude
    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = UNITS,
        @Query("appid") apiKey: String = API_KEY
    ): Call<CurrentWeather>

}