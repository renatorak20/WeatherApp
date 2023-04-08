package com.renato.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.renato.weatherapp.R
import com.renato.weatherapp.data.model.FavouriteWeather
import com.renato.weatherapp.data.model.ForecastDay
import com.renato.weatherapp.data.model.Hour
import com.renato.weatherapp.databinding.CityDetailRecyclerItemBinding
import com.renato.weatherapp.databinding.CityListItemBinding


class CityListAdapter(
    val context: Context,
    val array: ArrayList<Any>
) : RecyclerView.Adapter<CityListAdapter.CityItemViewHolder>() {

    class CityItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = CityListItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityItemViewHolder {
        return CityItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.city_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CityItemViewHolder, position: Int) {
        val binding = holder.binding
        val city = array[position] as FavouriteWeather

        binding.titleText.text = city.cityName
        binding.imageWeather.load(context.getString(R.string.iconUrl, city.icon))
        val currentTime = city.getCurrentTime().split(" ")
        binding.firstText.text = "${currentTime[0]} ${currentTime[1]}"
        binding.secondText.text = currentTime[2]
        binding.tempText.text = city.temperatureC.toString()

    }

    override fun getItemCount() = array.size

}