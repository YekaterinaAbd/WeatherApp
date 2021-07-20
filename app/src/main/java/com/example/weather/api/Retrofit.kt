package com.example.weather.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//REST API client to make API requests and get responses
//Automatically converts GSON responses to be easy to use in Android
object Retrofit {

    fun getApi(): WeatherApi {
        val retrofit = Retrofit.Builder()
            //base url of openweathermap api
            .baseUrl(BASE_URL)
            //to convert GSON responses automatically
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(WeatherApi::class.java)
    }
}