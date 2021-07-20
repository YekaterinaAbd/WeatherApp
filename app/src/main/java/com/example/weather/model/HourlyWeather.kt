package com.example.weather.model

import com.google.gson.annotations.SerializedName

data class HourlyWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,

    @SerializedName("feels_like")
    val feelsLike: Double,

    val pressure: Long,
    val humidity: Long,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    val weather: List<Weather>
)
