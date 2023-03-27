package com.renato.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.renato.weatherapp.adapters.CityForecastDaysAdapter
import com.renato.weatherapp.adapters.CityForecastTodayAdapter
import com.renato.weatherapp.databinding.ActivityCityDetailBinding
import com.renato.weatherapp.data.model.ForecastDay
import com.renato.weatherapp.data.model.WeatherResponseForecast
import com.renato.weatherapp.util.Preferences

class CityDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCityDetailBinding
    private lateinit var city: WeatherResponseForecast
    private lateinit var toolbar: Toolbar

    private var currentUnits:Boolean = true

    private val callback = OnMapReadyCallback { googleMap ->
        val cityLocation = LatLng(city.location.lat, city.location.lon)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLocation, 12.0f))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCity)
        toolbar = binding.toolbarCity

        city = intent.getSerializableExtra(getString(R.string.passing_data)) as WeatherResponseForecast
        currentUnits = Preferences(this).getCurrentUnits()

        setValues()
        setupRecyclerViews()
        setupMap()

        binding.toolbarCity.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setValues(){

        if(currentUnits){
            setMetricValues()
        }else{
            setImperialValues()
        }

        setOtherValues()
        setCityParameters()

    }

    fun setMetricValues(){

        val currentConditions = city.forecast.forecastday[0].hour[city.location.getCurrentHour()]

        binding.content.cityDetailHead.basicInfo.temperatureText.text = city.current.temp_c.toInt().toString() + "°C"
        binding.content.cityDetailHead.temperatureParameter.parameterValue.text = "${city.forecast.forecastday[0].day.mintemp_c.toInt()}°C / ${city.forecast.forecastday[0].day.maxtemp_c.toInt()}°C"
        binding.content.cityDetailHead.visibilityParameter.parameterValue.text = "${currentConditions.vis_km.toInt()} km"
        binding.content.cityDetailHead.windParameter.parameterValue.text = currentConditions.getMetricWind()
    }
    fun setImperialValues(){

        val currentConditions = city.forecast.forecastday[0].hour[city.location.getCurrentHour()]

        binding.content.cityDetailHead.basicInfo.temperatureText.text = city.current.temp_f.toInt().toString() + "°F"
        binding.content.cityDetailHead.temperatureParameter.parameterValue.text = "${city.forecast.forecastday[0].day.mintemp_f.toInt()}°F / ${city.forecast.forecastday[0].day.maxtemp_f.toInt()}°F"
        binding.content.cityDetailHead.visibilityParameter.parameterValue.text = "${currentConditions.vis_miles.toInt()} mil"
        binding.content.cityDetailHead.windParameter.parameterValue.text = currentConditions.getImperialWind()
    }
    fun setOtherValues(){

        val currentConditions = city.forecast.forecastday[0].hour[city.location.getCurrentHour()]

        binding.content.cityDetailHead.pressureParameter.parameterValue.text = "${currentConditions.pressure_mb.toInt()} hPa"
        binding.content.cityDetailHead.humidityParameter.parameterValue.text = "${currentConditions.humidity}%"
        binding.collapsingToolbar.title = city.location.name
        binding.content.cityDetailHead.basicInfo.weatherImage.load("https:" + city.current.condition.icon)
        binding.content.cityDetailHead.basicInfo.descriptionText.text = city.current.condition.text
        binding.content.cityDetailHead.basicInfo.dateText.text = city.location.getCurrentDate()
        binding.content.cityDetailHead.basicInfo.timeText.text = city.location.getCurrentTime()
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    fun setCityParameters(){
        binding.content.cityDetailHead.accuracyParameter.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_accuracy))
        binding.content.cityDetailHead.humidityParameter.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_humidity))
        binding.content.cityDetailHead.pressureParameter.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_pressure))
        binding.content.cityDetailHead.temperatureParameter.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_thermostat))
        binding.content.cityDetailHead.visibilityParameter.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_visibility))
        binding.content.cityDetailHead.windParameter.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_wind))

        binding.content.cityDetailHead.accuracyParameter.parameterTitle.text = resources.getString(R.string.accuracy)
        binding.content.cityDetailHead.humidityParameter.parameterTitle.text = resources.getString(R.string.humidity)
        binding.content.cityDetailHead.pressureParameter.parameterTitle.text = resources.getString(R.string.pressure)
        binding.content.cityDetailHead.temperatureParameter.parameterTitle.text = resources.getString(R.string.minMax)
        binding.content.cityDetailHead.visibilityParameter.parameterTitle.text = resources.getString(R.string.visibility)
        binding.content.cityDetailHead.windParameter.parameterTitle.text = resources.getString(R.string.wind)

        //nisam našao podatak o točnosti
        binding.content.cityDetailHead.accuracyParameter.parameterValue.text= "99%"

    }

    fun setupRecyclerViews(){
        binding.content.cityDetailToday.hoursRecyclerView.adapter = CityForecastTodayAdapter(this, (city.forecast.forecastday[0].hour), currentUnits)
        binding.content.cityDetailToday.hoursRecyclerView.smoothScrollToPosition(city.location.getCurrentHour())
        binding.content.cityDetailDays.daysRecyclerView.adapter = CityForecastDaysAdapter(this, (city.forecast.forecastday.slice(IntRange(1, 7)).toList() as ArrayList<ForecastDay>), currentUnits)
    }

    fun setupMap(){
        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        map?.getMapAsync(callback)
    }
}