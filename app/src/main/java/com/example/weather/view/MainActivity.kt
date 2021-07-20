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

    //Client that will help to get the user location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //Presenter instance (MVP) to get weather data
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

    //Check if have all permissions
    //If missing - show alert dialogs to get permission
    //If presented - check if GPS is ON
    private fun checkPermissions() {
        Dexter.withActivity(this)
            //Check these two permissions
            .withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                //If all permissions granted check if location is enabled
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        checkLocation()
                    }
                }

                //If some permissions are missing show alert dialog to get them
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    //Chek if Location(GPS) is ON on the phone
    //If OFF show alert dialog
    //If ON get user location
    private fun checkLocation() {
        if (!isLocationEnabled()) {
            showLocationNotEnabled()
        } else {
            requestLocationData()
        }
    }

    //Check if GPS is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //Call to get the user location
    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //Ask for location updates
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    //When the call is made locationCallback will get the location which can be used
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
            //When have location get the weather for this location
            getLocationWeather(latitude!!, longitude!!)
        }
    }

    //Put some listeners on views
    private fun bindViews() {

        //The first view which is shown when app is opened with the button to get location weather
        searchLocationButton.setOnClickListener {
            //If no location info check if location service is enabled and get location weather
            if (latitude == null || longitude == null) {
                checkLocation()
            } else {
                //If there is location info get weather for the location
                getLocationWeather(latitude!!, longitude!!)
            }
        }

        //When click on search to get weather for another city
        layoutSearch.setOnClickListener {
            location.visibility = View.GONE
            searchView.visibility = View.VISIBLE
            searchView.requestFocus()
            showKeyboard()
        }

        //Listener when type the city name in search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //When submit the city name search for weather in this city
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    city = query.trim()
                    //Search only for current weather but not full weather as have no lat and lon for full weather call
                    doOnInternet { presenter.getCurrentWeather(query) }
                }
                return true
            }

            //When text is chenged to nothing
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    //Define recyclerView and Adapters
    private fun initRecycler() {
        //Hourly weather will scroll horizontally
        recyclerHourly.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        //Daily weather will scroll vertically (default)
        recyclerDaily.layoutManager = LinearLayoutManager(this)

        recyclerHourly.adapter = hourAdapter
        recyclerDaily.adapter = dailyAdapter
    }

    //Get full location both for current weather and full weather (hourly, daily)
    private fun getLocationWeather(latitude: Double, longitude: Double) {
        doOnInternet {
            presenter.getCurrentWeather(latitude, longitude)
            presenter.getFullWeather(latitude, longitude)
        }
    }

    //Set current weather main info into the xml view
    override fun setCurrentWeather(weather: CurrentWeather) {

        //If call was made from search, no coordinates info was presented, so pass coordinates
        //when we have them now to get full info about the weather
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

        //Set sunrise and sunset time for SunriseSunsetView (library from github)
        ssv.sunriseTime = DateUtils.timeFromUnix(weather.sys.sunrise)
        ssv.sunsetTime = DateUtils.timeFromUnix(weather.sys.sunset)
        ssv.startAnimate()
    }

    //Set the full weather info (current, daily, hourly weather) into the xml view
    override fun setFullWeather(weather: FullWeather) {
        DateUtils.timezone = weather.timezoneOffset

        //Set current weather extra info
        val current = weather.daily.first()
        maxMinTemperature.text =
            getString(
                R.string.max_min_temperature, current.temp.day.toInt(), current.temp.night.toInt()
            )
        humidity.text = getString(R.string.humidity, current.humidity.toInt())
        dewPoint.text = getString(R.string.degrees, current.dewPoint.toInt())
        pressure.text = getString(R.string.pressure, current.pressure.toInt())
        uvIndex.text = current.uvi.toString()

        //Pass daily and hourly data to adapters
        val size = if (weather.hourly.size < 12) weather.hourly.size else 12
        hourAdapter.setList(weather.hourly.subList(1, size))
        dailyAdapter.setList(weather.daily.subList(1, weather.daily.size))

        hideLoading()
    }

    //When getting info from internet need some time, so show progressbar for this time
    override fun showLoading() {
        progressBar.show()
        mainView.hide()
        emptyLayout.hide()
    }

    //Hide progressbar and show main screen
    override fun hideLoading() {
        progressBar.hide()
        mainView.show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    //If some error show empty screen with button to get the current location weather
    override fun showError(message: String) {
        showMessage(message)
        progressBar.hide()
        mainView.hide()
        emptyLayout.show()
    }

    //Get the full weather info when the call was made from search
    private fun processIfFromSearch(coord: Coord) {
        if (!city.isNullOrEmpty()) {
            city = null
            location.visibility = View.VISIBLE
            searchView.visibility = View.INVISIBLE
            doOnInternet { presenter.getFullWeather(coord.lat, coord.lon) }
        }
    }

    //Function which takes another function as parameter
    //It checks if any internet connection - calls this function, if internet unavailable shows error
    private fun doOnInternet(f: () -> Unit) {
        if (isNetworkAvailable()) f()
        else showMessage("No Internet connection")
    }

    private fun showKeyboard() {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}