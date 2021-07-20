package com.example.weather.presenter

import android.util.Log
import com.example.weather.api.Retrofit
import com.example.weather.model.CurrentWeather
import com.example.weather.model.FullWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//MVP - Model View Presenter
//Presenter class to get data from the internet and define what view should do with this data
//Presenter class implements ContractPresenter interface

class Presenter(val view: Contract.ContractView) : Contract.ContractPresenter {

    //Get current weather by city name
    override fun getCurrentWeather(city: String) {
        view.showLoading()
        Retrofit.getApi().getCurrentWeather(city).enqueue(object : Callback<CurrentWeather> {
            //If the call was successful and there was any response
            override fun onResponse(
                call: Call<CurrentWeather>, response: Response<CurrentWeather>
            ) {
                //if not error response
                if (response.isSuccessful) {
                    val weatherResult = response.body()
                    if (weatherResult != null) {
                        //send result to view for processing
                        view.setCurrentWeather(weatherResult)
                    }
                    //if error response
                } else {
                    processError(response.code())
                }
            }

            //If the call failed
            override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                processError(t)
            }
        })
    }

    //Get current weather by coordinates
    override fun getCurrentWeather(latitude: Double, longitude: Double) {
        view.showLoading()
        Retrofit.getApi().getCurrentWeather(latitude, longitude)
            .enqueue(object : Callback<CurrentWeather> {
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

    //Get full weather by coordinates
    override fun getFullWeather(latitude: Double, longitude: Double) {
        view.showLoading()
        Retrofit.getApi().getWeather(latitude, longitude).enqueue(object : Callback<FullWeather> {
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

    //Process response error
    private fun processError(code: Int) {
        when (code) {
            400 -> Log.e("Error", "Bad Request")
            404 -> Log.e("Error", "Not found")
            else -> Log.d("Error", "Generic Error")
        }
        view.showError("Failed to load weather.")
    }

    //Process callback error
    private fun processError(t: Throwable) {
        Log.e("Error", t.message.toString())
        view.showError("Failed to load weather.")
    }
}