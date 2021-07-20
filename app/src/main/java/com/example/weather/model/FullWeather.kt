package com.example.weather.model

import com.google.gson.annotations.SerializedName

data class FullWeather(
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>
)