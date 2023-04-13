package com.renato.weatherapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.renato.weatherapp.R
import com.renato.weatherapp.data.model.WeatherRecent
import com.renato.weatherapp.databinding.CityListItemBinding
import com.renato.weatherapp.util.Utils
import com.renato.weatherapp.viewmodel.SharedViewModel
import java.util.ArrayList

class CityRecentsAdapter(
    val context: Context,
    val array: ArrayList<WeatherRecent>,
    val viewModel: SharedViewModel,
    val currentUnits: Boolean
) : RecyclerView.Adapter<CityRecentsAdapter.CityItemViewHolder>() {

    class CityItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = CityListItemBinding.bind(view)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityItemViewHolder {
        return CityItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.city_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CityItemViewHolder, position: Int) {

        val city = array[position]

        val isFavourite =
            viewModel.getFavouritesNames()?.contains(city.cityName) ?: false
        if (!isFavourite) {
            holder.binding.favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_outline))
        } else {
            holder.binding.favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
        }

        holder.binding.titleText.text = city.cityName
        holder.binding.imageWeather.load(context.getString(R.string.iconUrl, city.icon))

        val dms = Utils().convertToDMS(city.latitude, city.longitude)
        holder.binding.firstText.text = context.getString(R.string.dms, dms.first, dms.second)
        holder.binding.secondText.text = ""


        holder.binding.tempText.text =
            context.getString(R.string.temperatureMetricValue, city.temperature_c)

        holder.binding.favIcon.setOnClickListener {
            if (viewModel.getFavouritesNames()?.contains(city.cityName) == true) {
                viewModel.removeCityFromFavourites(context, city.cityName)
                holder.binding.favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_outline))
            } else {
                viewModel.addCityToFavourites(context, city.cityName)
                holder.binding.favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
            }
        }

    }

    override fun getItemCount() = array.size

    private fun changeFavouriteStatus(cityName: String) {

    }

}