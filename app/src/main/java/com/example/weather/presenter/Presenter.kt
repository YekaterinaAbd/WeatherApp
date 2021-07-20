package com.example.weather.presenter

import android.util.Log
import com.example.weather.api.Retrofit
import com.example.weather.model.CurrentWeather
import com.example.weather.model.FullWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Presenter(val view: Contract.ContractView) : Contract.ContractPresenter {

    override fun getCurrentWeather(city: String) {
        view.showLoading()
        Retrofit.getApi().getCurrentWeather(city).enqueueCurrentWeatherResponse()
    }

    override fun getCurrentWeather(latitude: Double, longitude: Double) {
        view.showLoading()
        Retrofit.getApi().getCurrentWeather(latitude, longitude).enqueueCurrentWeatherResponse()
    }

    override fun getFullWeather(latitude: Double, longitude: Double) {
        view.showLoading()
        Retrofit.getApi().getWeather(latitude, longitude).enqueueFullWeatherResponse()
    }

    private fun Call<CurrentWeather>.enqueueCurrentWeatherResponse() {
        this.enqueue(object : Callback<CurrentWeather> {
            override fun onResponse(
                call: Call<CurrentWeather>, response: Response<CurrentWeather>
            ) {
                if (response.isSuccessful) {
                    val weatherResult = response.body()
                    if (weatherResult != null) {
                        view.setCurrentWeather(weatherResult)
                    }
                } else {
                    processError(response.code())
                }
            }

            override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                processError(t)
            }
        })
    }

    private fun Call<FullWeather>.enqueueFullWeatherResponse() {
        this.enqueue(object : Callback<FullWeather> {
            override fun onResponse(
                call: Call<FullWeather>, response: Response<FullWeather>
            ) {
                if (response.isSuccessful) {
                    val weatherResult = response.body()
                    if (weatherResult != null) {
                        view.setFullWeather(weatherResult)
                    }
                } else {
                    processError(response.code())
                }
            }

            override fun onFailure(call: Call<FullWeather>, t: Throwable) {
                processError(t)
            }
        })
    }

    private fun processError(code: Int) {
        when (code) {
            400 -> Log.e("Error", "Bad Request")
            404 -> Log.e("Error", "Not found")
            else -> Log.d("Error", "Generic Error")
        }
        view.showError("Failed to load weather.")
    }

    private fun processError(t: Throwable) {
        Log.e("Error", t.message.toString())
        view.showError("Failed to load weather.")
    }
}