package com.renato.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.renato.weatherapp.R
import com.renato.weatherapp.databinding.CityDetailDaysBinding
import com.renato.weatherapp.databinding.CityDetailDaysItemBinding
import com.renato.weatherapp.databinding.CityDetailTodayItemBinding
import com.renato.weatherapp.data.model.Day
import com.renato.weatherapp.data.model.ForecastDay
import com.renato.weatherapp.data.model.Hour
import com.renato.weatherapp.util.Preferences

class CityForecastDaysAdapter(val context: Context, val days:ArrayList<ForecastDay>, val current:Boolean): RecyclerView.Adapter<CityForecastDaysAdapter.CityDayHolder>() {


    class CityDayHolder(view: View): RecyclerView.ViewHolder(view){
        val binding = CityDetailDaysItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityDayHolder {
        return CityDayHolder(LayoutInflater.from(context).inflate(R.layout.city_detail_days_item, parent, false))
    }

    override fun onBindViewHolder(holder: CityDayHolder, position: Int) {
        val day = days[position]

        if(current){
            holder.binding.temperatureText.text = "${day.day.maxtemp_c.toInt()}°C"
        }else{
            holder.binding.temperatureText.text = "${day.day.maxtemp_f.toInt()}°F"
        }

        holder.binding.dayText.text = day.getDayInWeek()
        holder.binding.weatherIcon.load("https:" + day.day.condition.icon)


    }

    override fun getItemCount() = days.size
}