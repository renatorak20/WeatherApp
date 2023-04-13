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
import com.renato.weatherapp.data.model.WeatherFavourite
import com.renato.weatherapp.databinding.CityListItemBinding
import com.renato.weatherapp.viewmodel.SharedViewModel
import java.util.ArrayList

class CityFavouritesAdapter(
    val context: Context,
    val array: ArrayList<WeatherFavourite>,
    val viewModel: SharedViewModel,
    val currentUnits: Boolean
) : RecyclerView.Adapter<CityFavouritesAdapter.CityItemViewHolder>() {

    private lateinit var binding: CityListItemBinding
    private var isFavourite: Boolean = true
    var reorderSwitch = false

    class CityItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = CityListItemBinding.bind(view)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityFavouritesAdapter.CityItemViewHolder {
        return CityItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.city_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CityItemViewHolder, position: Int) {
        binding = holder.binding

        val city = array[position]

        binding.titleText.text = city.cityName
        binding.imageWeather.load(context.getString(R.string.iconUrl, city.icon))
        val currentTime = city.getCurrentTime().split(" ")
        binding.firstText.text =
            context.getString(R.string.timeText, currentTime[0], currentTime[1])
        binding.secondText.text = currentTime[2].replace("(", "").replace(")", "")
        binding.tempText.text =
            context.getString(R.string.temperatureMetricValue, city.temperatureC)

        binding.favIcon.setOnClickListener {
            changeFavouriteStatus(city.cityName, binding.favIcon)
            removeItemFromPosition(position)
        }
        checkDragSwitch()
    }

    override fun getItemCount() = array.size

    private fun changeFavouriteStatus(cityName: String, favIcon: ImageView) {
        if (isFavourite) {
            Log.i("CHANGE FAVOURITE STATUS", "$cityName $isFavourite")
            viewModel.removeCityFromFavourites(context, cityName)
            favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_outline))
        } else {
            Log.i("CHANGE FAVOURITE STATUS", "$cityName $isFavourite")
            viewModel.addCityToFavourites(context, cityName)
            favIcon.setImageDrawable(context.getDrawable(R.drawable.ic_star_filled))
        }
        isFavourite = !isFavourite
        notifyDataSetChanged()
    }

    private fun removeItemFromPosition(position: Int) {
        array.removeAt(position)
        notifyItemRemoved(position)
    }

    private fun checkDragSwitch() {
        val marginValue: Int
        if (reorderSwitch) {
            marginValue = (45 * context.resources.displayMetrics.density).toInt()
            binding.dragHandle.setImageResource(R.drawable.ic_icons_android_ic_reorder)
        } else {
            marginValue = (5 * context.resources.displayMetrics.density).toInt()
            binding.dragHandle.setImageResource(0)
        }
        val layoutParams = binding.contentLayout.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.rightMargin = marginValue
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = array.removeAt(fromPosition)
        array.add(toPosition, item)

        viewModel.addCitiesInFravourites(context, array as List<WeatherFavourite>)
    }

}