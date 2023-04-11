package com.renato.weatherapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.renato.weatherapp.R
import com.renato.weatherapp.data.model.WeatherFavourite
import com.renato.weatherapp.data.model.WeatherRecent
import com.renato.weatherapp.databinding.CityListItemBinding
import com.renato.weatherapp.util.Utils
import com.renato.weatherapp.viewmodel.SharedViewModel
import java.util.*


class CityListAdapter(
    val context: Context,
    val array: ArrayList<Any>,
    val viewModel: SharedViewModel,
    val currentUnits: Boolean
) : RecyclerView.Adapter<CityListAdapter.CityItemViewHolder>() {


    class CityItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = CityListItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityItemViewHolder {
        return CityItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.city_list_item, parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: CityItemViewHolder, position: Int) {
        val binding = holder.binding

        val city = array[position]

        when (city) {
            is WeatherFavourite -> {

                binding.titleText.text = city.cityName
                binding.imageWeather.load(context.getString(R.string.iconUrl, city.icon))
                val currentTime = city.getCurrentTime().split(" ")
                binding.firstText.text =
                    context.getString(R.string.timeText, currentTime[0], currentTime[1])
                binding.secondText.text = currentTime[2].replace("(", "").replace(")", "")
                binding.tempText.text =
                    context.getString(R.string.temperatureMetricValue, city.temperatureC)

                binding.favIcon.setOnClickListener {
                    changeFavouriteStatus(city.cityName, true, binding.favIcon)
                    removeItemFromPosition(position)
                }
            }
            is WeatherRecent -> {

                val isFavourite =
                    viewModel.getFavourites().value?.map { fav -> fav.cityName }?.toList()
                        ?.contains(city.cityName) ?: false
                if (!isFavourite) {
                    binding.favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_outline))
                } else {
                    binding.favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
                }

                binding.titleText.text = city.cityName
                binding.imageWeather.load(context.getString(R.string.iconUrl, city.icon))

                val dms = Utils().convertToDMS(city.latitude, city.longitude)
                binding.firstText.text = context.getString(R.string.dms, dms.first, dms.second)
                binding.secondText.text = ""


                binding.tempText.text =
                    context.getString(R.string.temperatureMetricValue, city.temperature_c)

                binding.favIcon.setOnClickListener {
                    changeFavouriteStatus(city.cityName, isFavourite, binding.favIcon)
                }
            }
        }
    }


    override fun getItemCount() = array.size

    private fun changeFavouriteStatus(cityName: String, status: Boolean, favIcon: ImageView) {
        if (status) {
            viewModel.removeCityFromFavourites(context, cityName)
            favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_outline))
        } else {
            viewModel.addCityToFavourites(context, cityName)
            favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
        }
    }

    private fun removeItemFromPosition(position: Int) {
        array.removeAt(position)
        notifyItemRemoved(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = array.removeAt(fromPosition)
        array.add(toPosition, item)

        viewModel.updateCitiesInFravourites(context, array as List<WeatherFavourite>)
    }
}