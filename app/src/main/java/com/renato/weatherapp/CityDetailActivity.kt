package com.renato.weatherapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
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
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarItem: MenuItem
    private lateinit var sharedViewModel: SharedViewModel
    private var iconFlag = false
    private var currentUnits: Boolean = true
    private var isMapTransitionEnabled = false

    private val callback = OnMapReadyCallback { googleMap ->
        val cityLocation = LatLng(
            sharedViewModel.getForecastValue().location.lat,
            sharedViewModel.getForecastValue().location.lon
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLocation, 12.0f))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarCity)
        toolbar = binding.toolbarCity

        cityToLoad = intent.getStringExtra(getString(R.string.passing_data))!!
        binding.collapsingToolbar.title = cityToLoad

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        sharedViewModel.getForecast().observe(this) { city ->
            if (city.isSuccessful) {
                setValues()
                isMapTransitionEnabled = true
            } else {
                Utils().showErrorDialog(this)
            }
            sharedViewModel.addCityToRecents(this)
        }

        sharedViewModel.getNewForecast(cityToLoad, this)

        currentUnits = Preferences(this).getCurrentUnits()

        binding.back.setOnClickListener {
            finish()
        }

        binding.content.viewMoreButton.setOnClickListener {
            if (isMapTransitionEnabled) {
                startMapActivity()
            }
        }
    }

    fun startMapActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra(
            resources.getString(R.string.passing_data),
            LatLng(
                sharedViewModel.getForecastValue().location.lat,
                sharedViewModel.getForecastValue().location.lon
            )
        )
        startActivity(intent)
    }


    private fun setValues() {

        val viewParameters =
            binding.content.cityDetailHead.root.children.filterIsInstance<CityDetailParameter>()
        val viewHeader =
            binding.content.cityDetailHead.basicInfo.root.children.filterIsInstance<TextView>()


        Utils().fillCityDetailParameters(this, viewParameters, sharedViewModel.getForecastValue())
        Utils().fillCityHeader(this, viewHeader, sharedViewModel.getForecastValue())
        Utils().setCityMainIcon(
            this,
            binding.content.cityDetailHead.basicInfo.weatherImage,
            sharedViewModel.getForecastValue()
        )

        setupRecyclerViews()
        setupMap()
    }


    private fun setupRecyclerViews() {
        binding.content.cityDetailToday.hoursRecyclerView.adapter = CityForecastAdapter(
            this,
            (sharedViewModel.getForecastValue().forecast.forecastday[0].hour) as ArrayList<Any>,
            currentUnits,
            0
        )
        binding.content.cityDetailToday.hoursRecyclerView.smoothScrollToPosition(sharedViewModel.getForecastValue().location.getCurrentHour())
        binding.content.cityDetailDays.daysRecyclerView.adapter = CityForecastAdapter(
            this,
            (sharedViewModel.getForecastValue().forecast.forecastday.slice(IntRange(1, 7))
                .toList() as ArrayList<Any>),
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

        sharedViewModel.getFavourites().observe(this) { cities ->
            iconFlag = if (cities.map { it.cityName }.toList().contains(cityToLoad)) {
                toolbarItem.setIcon(R.drawable.ic_star_filled)
                true
            } else {
                toolbarItem.setIcon(R.drawable.ic_star_outline)
                false
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_item_favourite) {
            if (iconFlag) {
                item.setIcon(R.drawable.ic_star_outline)
                sharedViewModel.removeCityFromFavourites(this, cityToLoad)
            } else {
                item.setIcon(R.drawable.ic_star_filled)
                sharedViewModel.addCityToFavourites(
                    this,
                    sharedViewModel.getForecastValue().location.name
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }
}