package com.example.weather.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.DailyWeather
import com.example.weather.utils.DateUtils
import com.example.weather.utils.WeatherIconUtils

class DailyAdapter : RecyclerView.Adapter<DailyAdapter.DailyViewHolder>() {

    private val list = mutableListOf<DailyWeather>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_daily, parent, false)
        return DailyViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setList(weatherList: List<DailyWeather>) {
        list.clear()
        list.addAll(weatherList)
        notifyDataSetChanged()
    }

    inner class DailyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val date: TextView = view.findViewById(R.id.date)
        private val image: ImageView = view.findViewById(R.id.image)
        private val weatherView: TextView = view.findViewById(R.id.weather)
        private val degreesMax: TextView = view.findViewById(R.id.degreesMax)
        private val degreesMin: TextView = view.findViewById(R.id.degreesMin)

        fun bind(weather: DailyWeather) {
            date.text = DateUtils.dateFromUnix(weather.dt)
            image.setImageResource(WeatherIconUtils.getWeatherIcon(weather.weather.first()))
            weatherView.text = weather.weather.first().main
            degreesMax.text = itemView.context.getString(R.string.degrees, weather.temp.day.toInt())
            degreesMin.text =
                itemView.context.getString(R.string.degrees, weather.temp.night.toInt())
        }
    }
}