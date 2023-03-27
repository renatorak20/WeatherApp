package com.renato.weatherapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.renato.weatherapp.AboutActivity
import com.renato.weatherapp.R
import com.renato.weatherapp.databinding.FragmentSettingsBinding
import com.renato.weatherapp.util.Preferences


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var extrasUnit:List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        preferences = requireActivity().getSharedPreferences(resources.getString(R.string.package_name), Context.MODE_PRIVATE)
        extrasUnit = resources.getStringArray(R.array.units).toList()

        binding = FragmentSettingsBinding.inflate(layoutInflater)

        return binding.root
    }

    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadAppPreferenes()
        binding.unitSelector.unitRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            Preferences(requireActivity()).swapUnits()
        }

        binding.about.moreInfoButton.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }

    }

    fun loadAppPreferenes(){
        when(preferences.getString(extrasUnit[0], extrasUnit[1])){
            extrasUnit[1] -> binding.unitSelector.unitRadioGroup.check(R.id.metricButton)
            extrasUnit[2] -> binding.unitSelector.unitRadioGroup.check(R.id.imperialButton)
        }
    }
}