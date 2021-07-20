package com.example.weather.utils

import android.icu.text.SimpleDateFormat
import android.text.format.DateFormat
import com.github.tianma8023.model.Time
import java.util.*

object DateUtils {

    var timezone = 3600

    fun getTime(unix: Long): String {
        val date = Date(unix * 1000L + timezone)
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return outputFormat.format(date)
    }

    fun dateFromUnix(unix: Long): String {
        val date = Date(unix * 1000L + timezone)
        val dayOfTheWeek = DateFormat.format("EEEE", date)
        val day = DateFormat.format("dd MMM", date)

        return ("$dayOfTheWeek, $day")
    }

    fun timeFromUnix(unix: Long): Time {
        val date = Date(unix * 1000L + timezone)

        val cal = Calendar.getInstance()
        cal.time = date
        val hours = cal.get(Calendar.HOUR_OF_DAY)
        val minutes = cal.get(Calendar.MINUTE)

        return Time(hours, minutes)
    }
}