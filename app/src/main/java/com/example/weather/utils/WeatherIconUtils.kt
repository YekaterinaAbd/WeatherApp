package com.example.weather.utils

import com.example.weather.R
import com.example.weather.model.Weather

object WeatherIconUtils {

    private const val thunderstormCode = "11"
    private const val drizzleCode = "09"
    private const val rainCode = "10"
    private const val snowCode = "13"
    private const val atmosphereCode = "50"
    private const val clearDayCode = "01d"
    private const val clearNightCode = "01n"

    private const val cloudsImage = R.drawable.ic_cloud
    private const val rainImage = R.drawable.ic_rain
    private const val thunderstormImage = R.drawable.ic_thunderstorm
    private const val drizzleImage = R.drawable.ic_drizzle
    private const val snowImage = R.drawable.ic_snow
    private const val atmosphereImage = R.drawable.ic_atmosphere
    private const val clearDayImage = R.drawable.ic_day
    private const val clearNightImage = R.drawable.ic_night

    fun getWeatherIcon(weather: Weather): Int {
        when (weather.icon.subSequence(0, 2)) {
            thunderstormCode -> return thunderstormImage
            drizzleCode -> return drizzleImage
            rainCode -> return rainImage
            snowCode -> return snowImage
            atmosphereCode -> return atmosphereImage
        }
        when (weather.icon.subSequence(0, 3)) {
            clearDayCode -> return clearDayImage
            clearNightCode -> return clearNightImage
        }
        return cloudsImage
    }
}