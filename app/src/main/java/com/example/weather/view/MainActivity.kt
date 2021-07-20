package com.example.weather.view

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.model.Coord
import com.example.weather.model.CurrentWeather
import com.example.weather.model.FullWeather
import com.example.weather.presenter.Contract
import com.example.weather.presenter.Presenter
import com.example.weather.utils.DateUtils
import com.example.weather.utils.PermissionDialogUtils.showLocationNotEnabled
import com.example.weather.utils.PermissionDialogUtils.showRationalDialogForPermissions
import com.example.weather.utils.WeatherIconUtils
import com.example.weather.utils.hide
import com.example.weather.utils.isNetworkAvailable
import com.example.weather.utils.show
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), Contract.ContractView {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var presenter: Contract.ContractPresenter

    private val hourAdapter by lazy { HourAdapter() }
    private val dailyAdapter by lazy { DailyAdapter() }

    private var city: String? = null

    private var latitude: Double? = null
    private var longitude: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = Presenter(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        bindViews()
        initRecycler()
        checkPermissions()
    }

    private fun checkPermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        checkLocation()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    private fun checkLocation() {
        if (!isLocationEnabled()) {
            showLocationNotEnabled()
        } else {
            requestLocationData()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
            getLocationWeather(latitude!!, longitude!!)
        }
    }

    private fun bindViews() {
        searchLocationButton.setOnClickListener {
            if (latitude == null || longitude == null) {
                checkLocation()
            } else {
                getLocationWeather(latitude!!, longitude!!)
            }
        }
        layoutSearch.setOnClickListener {
            location.visibility = View.GONE
            searchView.visibility = View.VISIBLE
            searchView.requestFocus()
            showKeyboard()
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    city = query.trim()
                    doOnInternet { presenter.getCurrentWeather(query) }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun initRecycler() {
        recyclerHourly.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerDaily.layoutManager = LinearLayoutManager(this)

        recyclerHourly.adapter = hourAdapter
        recyclerDaily.adapter = dailyAdapter
    }

    private fun getLocationWeather(latitude: Double, longitude: Double) {
        doOnInternet {
            presenter.getCurrentWeather(latitude, longitude)
            presenter.getFullWeather(latitude, longitude)
        }
    }

    override fun setCurrentWeather(weather: CurrentWeather) {
        processIfFromSearch(weather.coord)

        image.setImageResource(WeatherIconUtils.getWeatherIcon(weather.weather.first()))

        location.text = weather.name
        degrees.text = getString(R.string.degrees, weather.main.temp.toInt())
        fellsLike.text = getString(R.string.feels_like, weather.main.feelsLike.toInt())
        maxMinTemperature.text =
            getString(
                R.string.max_min_temperature,
                weather.main.tempMax.toInt(),
                weather.main.tempMin.toInt()
            )
        if (!weather.weather.isNullOrEmpty()) {
            this.weather.text = weather.weather.first().main
        }
        wind.text = getString(R.string.wind, weather.wind.speed.toInt())
        visibility.text = getString(R.string.visibility, (weather.visibility / 1000).toInt())

        ssv.sunriseTime = DateUtils.timeFromUnix(weather.sys.sunrise)
        ssv.sunsetTime = DateUtils.timeFromUnix(weather.sys.sunset)
        ssv.startAnimate()
    }

    override fun setFullWeather(weather: FullWeather) {
        DateUtils.timezone = weather.timezoneOffset

        val current = weather.daily.first()
        maxMinTemperature.text =
            getString(
                R.string.max_min_temperature, current.temp.day.toInt(), current.temp.night.toInt()
            )
        humidity.text = getString(R.string.humidity, current.humidity.toInt())
        dewPoint.text = getString(R.string.degrees, current.dewPoint.toInt())
        pressure.text = getString(R.string.pressure, current.pressure.toInt())
        uvIndex.text = current.uvi.toString()

        val size = if (weather.hourly.size < 12) weather.hourly.size else 12
        hourAdapter.setList(weather.hourly.subList(1, size))
        dailyAdapter.setList(weather.daily.subList(1, weather.daily.size))

        hideLoading()
    }

    override fun showLoading() {
        progressBar.show()
        mainView.hide()
        emptyLayout.hide()
    }

    override fun hideLoading() {
        progressBar.hide()
        mainView.show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun showError(message: String) {
        showMessage(message)
        progressBar.hide()
        mainView.hide()
        emptyLayout.show()
    }

    private fun processIfFromSearch(coord: Coord) {
        if (!city.isNullOrEmpty()) {
            city = null
            location.visibility = View.VISIBLE
            searchView.visibility = View.INVISIBLE
            doOnInternet { presenter.getFullWeather(coord.lat, coord.lon) }
        }
    }

    private fun doOnInternet(f: () -> Unit) {
        if (isNetworkAvailable()) f()
        else showMessage("No Internet connection")
    }

    private fun showKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}