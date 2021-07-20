package com.example.weather.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.HourlyWeather
import com.example.weather.utils.DateUtils
import com.example.weather.utils.WeatherIconUtils

//Adapter for recyclerView to show the list of hourly weather for next 12 hours
class HourAdapter : RecyclerView.Adapter<HourAdapter.HourViewHolder>() {

    private val list = mutableListOf<HourlyWeather>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_hourly, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setList(weatherList: List<HourlyWeather>) {
        list.clear()
        list.addAll(weatherList)
        notifyDataSetChanged()
    }

    inner class HourViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val time: TextView = view.findViewById(R.id.time)
        private val image: ImageView = view.findViewById(R.id.image)
        private val degrees: TextView = view.findViewById(R.id.degrees)

        fun bind(weather: HourlyWeather) {
            time.text = DateUtils.getTime(weather.dt)
            image.setImageResource(WeatherIconUtils.getWeatherIcon(weather.weather.first()))
            degrees.text = itemView.context.getString(R.string.degrees, weather.temp.toInt())
        }
    }
}