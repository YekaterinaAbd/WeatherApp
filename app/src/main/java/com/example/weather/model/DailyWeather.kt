package com.example.weather.model

import com.google.gson.annotations.SerializedName

data class DailyWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val weather: List<Weather>,
    val temp: Temp,
    val pressure: Double,
    val humidity: Double,
    @SerializedName("dew_point") val dewPoint: Double,
    val uvi: Double
)