package com.renato.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.renato.weatherapp.R
import com.renato.weatherapp.data.model.ForecastDay
import com.renato.weatherapp.data.model.Hour
import com.renato.weatherapp.databinding.CityDetailRecyclerItemBinding

const val TYPE_TODAY_HOURS = 0
const val TYPE_DAYS = 1

class CityForecastAdapter(
    val context: Context,
    val array: ArrayList<Any>,
    private val currentUnits: Boolean,
    private val type: Int
) : RecyclerView.Adapter<CityForecastAdapter.CityItemViewHolder>() {

    class CityItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = CityDetailRecyclerItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityItemViewHolder {
        return CityItemViewHolder(
            LayoutInflater.from(context).inflate(R.layout.city_detail_recycler_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CityItemViewHolder, position: Int) {

        when (type) {
            TYPE_TODAY_HOURS -> {
                val hour = array[position] as Hour

                if (currentUnits) {
                    holder.binding.temperatureText.text = context.resources.getString(
                        R.string.temperatureMetricValue,
                        hour.temp_c.toInt()
                    )
                } else {
                    holder.binding.temperatureText.text = context.resources.getString(
                        R.string.temperatureImperialValue,
                        hour.temp_f.toInt()
                    )
                }
                holder.binding.timeDateText.text = hour.getCurrentHour()
                holder.binding.weatherIcon.load(
                    context.resources.getString(
                        R.string.iconUrl,
                        hour.condition.icon
                    )
                )
            }
            TYPE_DAYS -> {
                val day = array[position] as ForecastDay

                if (currentUnits) {
                    holder.binding.temperatureText.text = context.resources.getString(
                        R.string.temperatureMetricValue,
                        day.day.maxtemp_c.toInt()
                    )
                } else {
                    holder.binding.temperatureText.text = context.resources.getString(
                        R.string.temperatureImperialValue,
                        day.day.maxtemp_f.toInt()
                    )
                }

                holder.binding.timeDateText.text = day.getDayInWeek()
                holder.binding.weatherIcon.load(
                    context.resources.getString(
                        R.string.iconUrl,
                        day.day.condition.icon
                    )
                )
            }
        }

    }

    override fun getItemCount() = array.size

}