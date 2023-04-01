package com.renato.weatherapp.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.renato.weatherapp.R
import com.renato.weatherapp.databinding.CityDetailParameterBinding

class CityDetailParameter(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    private val binding: CityDetailParameterBinding

    init {
        inflate(context, R.layout.city_detail_parameter, this)
        binding = CityDetailParameterBinding.inflate(LayoutInflater.from(context), this, true)

        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.CityDetailParameter,
            0, 0
        ).apply {

            try {
                binding.parameterTitle.text =
                    getString(R.styleable.CityDetailParameter_parameterTitle)
                binding.icon.setImageResource(
                    getResourceId(
                        R.styleable.CityDetailParameter_parameterIcon,
                        0
                    )
                )
            } finally {
                recycle()
            }
        }
    }

    fun setValue(value: String) {
        binding.parameterValue.text = value
    }

}