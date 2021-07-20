package com.example.weather.presenter

import com.example.weather.model.CurrentWeather
import com.example.weather.model.FullWeather

interface Contract {
    interface ContractView {
        fun setFullWeather(weather: FullWeather)
        fun setCurrentWeather(weather: CurrentWeather)
        fun showLoading()
        fun hideLoading()
        fun showMessage(message: String)
        fun showError(message: String)
    }

    interface ContractPresenter {
        fun getCurrentWeather(city: String)
        fun getCurrentWeather(latitude: Double, longitude: Double)
        fun getFullWeather(latitude: Double, longitude: Double)
    }
}