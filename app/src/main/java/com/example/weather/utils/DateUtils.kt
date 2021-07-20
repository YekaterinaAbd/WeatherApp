package com.example.weather.utils

import android.icu.text.SimpleDateFormat
import android.text.format.DateFormat
import com.github.tianma8023.model.Time
import java.util.*

//Functions to convert time to format needed
object DateUtils {

    var timezone = 3600

    //Get time in format [11:00] from unix time
    fun getTime(unix: Long): String {
        val date = Date(unix * 1000L + timezone)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return outputFormat.format(date)
    }

    //Get time in format [10.06, Monday] from unix time
    fun dateFromUnix(unix: Long): String {
        val date = Date(unix * 1000L + timezone)
        val dayOfTheWeek = DateFormat.format("EEEE", date)
        val day = DateFormat.format("dd MMM", date)

        return ("$dayOfTheWeek, $day")
    }

    //Get Time (hours, minutes) from unix for SunriseSunsetView
    fun timeFromUnix(unix: Long): Time {
        val date = Date(unix * 1000L + timezone)

        val cal = Calendar.getInstance()
        cal.time = date
        val hours = cal.get(Calendar.HOUR_OF_DAY)
        val minutes = cal.get(Calendar.MINUTE)

        return Time(hours, minutes)
    }
}