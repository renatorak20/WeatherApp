package com.renato.weatherapp.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.currentComposer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.renato.weatherapp.R
import com.renato.weatherapp.databinding.CityDetailTodayItemBinding
import com.renato.weatherapp.data.model.Hour

class CityForecastTodayAdapter(val context:Context, val hours:ArrayList<Hour>, val current:Boolean): RecyclerView.Adapter<CityForecastTodayAdapter.CityHourItemHolder>() {

    class CityHourItemHolder(view:View): RecyclerView.ViewHolder(view){
        val binding = CityDetailTodayItemBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHourItemHolder {
        return CityHourItemHolder(LayoutInflater.from(context).inflate(R.layout.city_detail_today_item, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CityHourItemHolder, position: Int) {
        val hour = hours[position]

        if(current){
            holder.binding.temperatureText.text = "${hour.temp_c.toInt()}°C"
        }else{
            holder.binding.temperatureText.text = "${hour.temp_f.toInt()}°F"
        }
        holder.binding.timeText.text = hour.getCurrentHour()
        holder.binding.weatherIcon.load("https:" + hour.condition.icon)

    }

    override fun getItemCount() = hours.size
}