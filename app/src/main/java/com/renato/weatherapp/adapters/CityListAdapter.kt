package com.renato.weatherapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.renato.weatherapp.CityDetailActivity
import com.renato.weatherapp.R
import com.renato.weatherapp.data.model.WeatherFavourite
import com.renato.weatherapp.data.model.WeatherRecent
import com.renato.weatherapp.databinding.CityListItemBinding
import com.renato.weatherapp.util.Utils
import com.renato.weatherapp.viewmodel.SharedViewModel

class CityListAdapter(
    val context: Context,
    val array: ArrayList<Any>,
    val viewModel: SharedViewModel,
    val currentUnits: Boolean
) : RecyclerView.Adapter<CityListAdapter.CityItemViewHolder>() {

    var reorderSwitch = false


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

        val city = array[position]

        when (city) {
            is WeatherFavourite -> {

                holder.binding.titleText.text = city.cityName
                holder.binding.imageWeather.load(context.getString(R.string.iconUrl, city.icon))
                val currentTime = city.getCurrentTime().split(" ")
                holder.binding.firstText.text =
                    context.getString(R.string.timeText, currentTime[0], currentTime[1])
                holder.binding.secondText.text = currentTime[2].replace("(", "").replace(")", "")
                holder.binding.tempText.text =
                    context.getString(R.string.temperatureMetricValue, city.temperatureC)

                holder.binding.favIcon.setOnClickListener {
                    changeFavouriteStatus(city.cityName, holder.binding.favIcon, true)
                    removeItemFromPosition(position)
                }

                holder.binding.titleText.setOnClickListener {
                    if (Utils().isNetworkAvailable(context)) {
                        startCityDetailActivity(city.cityName)
                    }
                }
                checkDragSwitch(holder)
            }
            is WeatherRecent -> {

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
                holder.binding.firstText.text =
                    context.getString(R.string.dms, dms.first, dms.second)
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

                holder.binding.titleText.setOnClickListener {
                    if (Utils().isNetworkAvailable(context)) {
                        startCityDetailActivity(city.cityName)
                    }
                }
            }
        }
    }


    override fun getItemCount() = array.size

    private fun changeFavouriteStatus(cityName: String, favIcon: ImageView, isFavourite: Boolean) {
        if (isFavourite) {
            viewModel.removeCityFromFavourites(context, cityName)
            favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_outline))
        } else {
            viewModel.addCityToFavourites(context, cityName)
            favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
        }
        notifyDataSetChanged()
    }

    private fun removeItemFromPosition(position: Int) {
        array.removeAt(position)
        notifyItemRemoved(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = array.removeAt(fromPosition)
        array.add(toPosition, item)

        viewModel.addCitiesInFravourites(context, array as List<WeatherFavourite>)
    }

    fun startCityDetailActivity(cityName: String) {
        context.startActivity(
            Intent(context, CityDetailActivity::class.java).putExtra(
                context.getString(R.string.passing_data),
                cityName
            ).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        )
    }

    private fun checkDragSwitch(holder: CityItemViewHolder) {
        val marginValue: Int
        if (reorderSwitch) {
            marginValue = (45 * context.resources.displayMetrics.density).toInt()
            holder.binding.dragHandle.setImageResource(R.drawable.ic_icons_android_ic_reorder)
        } else {
            marginValue = (5 * context.resources.displayMetrics.density).toInt()
            holder.binding.dragHandle.setImageResource(0)
        }
        val layoutParams = holder.binding.contentLayout.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.rightMargin = marginValue
    }
}