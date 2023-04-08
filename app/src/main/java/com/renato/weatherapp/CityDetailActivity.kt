package com.renato.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.renato.weatherapp.adapters.CityForecastAdapter
import com.renato.weatherapp.data.model.WeatherResponseForecast
import com.renato.weatherapp.databinding.ActivityCityDetailBinding
import com.renato.weatherapp.ui.custom.CityDetailParameter
import com.renato.weatherapp.util.Preferences
import com.renato.weatherapp.util.Utils
import com.renato.weatherapp.viewmodel.SharedViewModel

class CityDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCityDetailBinding
    private lateinit var cityToLoad: String
    private lateinit var city: WeatherResponseForecast
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarItem: MenuItem
    private lateinit var sharedViewModel: SharedViewModel
    private var iconFlag = false
    private var currentUnits: Boolean = true

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

        cityToLoad = intent.getStringExtra(getString(R.string.passing_data))!!

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]


        sharedViewModel.getForecast().observe(this) { city ->
            if (city.isSuccessful) {
                this.city = city.body()!!
                setValues()
            } else {
                Utils().showErrorDialog(this)
            }
        }

        sharedViewModel.getFavourites().observe(this) { cities ->
            if (cities.map { it.cityName }.toList().contains(cityToLoad)) {
                toolbarItem.setIcon(R.drawable.ic_star_filled)
            } else {
                toolbarItem.setIcon(R.drawable.ic_star_outline)
            }
        }

        sharedViewModel.getNewForecast(cityToLoad)

        currentUnits = Preferences(this).getCurrentUnits()

        binding.back.setOnClickListener {
            finish()
        }

        sharedViewModel.getFavourites().observe(this) {
            if (sharedViewModel.getFavourites().value?.any { it.cityName == cityToLoad } == true) {
                toolbarItem.setIcon(R.drawable.ic_star_filled)
            }
        }
    }

    private fun setValues() {

        val viewParameters =
            binding.content.cityDetailHead.root.children.filterIsInstance<CityDetailParameter>()
        val viewHeader =
            binding.content.cityDetailHead.basicInfo.root.children.filterIsInstance<TextView>()

        binding.collapsingToolbar.title = city.location.name
        Utils().fillCityDetailParameters(this, viewParameters, city)
        Utils().fillCityHeader(this, viewHeader, city)
        Utils().setCityMainIcon(this, binding.content.cityDetailHead.basicInfo.weatherImage, city)

        setupRecyclerViews()
        setupMap()
    }


    private fun setupRecyclerViews() {
        binding.content.cityDetailToday.hoursRecyclerView.adapter = CityForecastAdapter(
            this,
            (city.forecast.forecastday[0].hour) as ArrayList<Any>,
            currentUnits,
            0
        )
        binding.content.cityDetailToday.hoursRecyclerView.smoothScrollToPosition(city.location.getCurrentHour())
        binding.content.cityDetailDays.daysRecyclerView.adapter = CityForecastAdapter(
            this,
            (city.forecast.forecastday.slice(IntRange(1, 7)).toList() as ArrayList<Any>),
            currentUnits,
            1
        )
    }

    private fun setupMap() {
        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        map?.getMapAsync(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.city_detail_menu, menu)
        toolbarItem = menu!!.findItem(R.id.menu_item_favourite)

        sharedViewModel.getFavouritesFromDb(this)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_item_favourite) {
            if (iconFlag) {
                Log.i("PRESSED", "Before was not on")
                item.setIcon(R.drawable.ic_star_outline)
                sharedViewModel.removeCityFromFavourites(this, cityToLoad)
            } else {
                Log.i("PRESSED", "Before was on")
                item.setIcon(R.drawable.ic_star_filled)
                sharedViewModel.addCityToFavourites(this)
            }
            iconFlag = !iconFlag
        }
        return super.onOptionsItemSelected(item)
    }
}