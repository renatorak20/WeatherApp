package com.renato.weatherapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.renato.weatherapp.AboutActivity
import com.renato.weatherapp.R
import com.renato.weatherapp.database.WeatherApiDatabase
import com.renato.weatherapp.databinding.FragmentSettingsBinding
import com.renato.weatherapp.util.Preferences
import com.renato.weatherapp.viewmodel.SharedViewModel


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var extrasUnit: List<String>
    private lateinit var extrasLang:Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.getFavouritesFromDb(requireContext())

        preferences = requireActivity().getSharedPreferences(
            resources.getString(R.string.package_name),
            Context.MODE_PRIVATE
        )

        extrasUnit = resources.getStringArray(R.array.units).toList()
        extrasLang = resources.getStringArray(R.array.languages)

        binding = FragmentSettingsBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSpinners()

        binding.unitSelector.unitRadioGroup.setOnCheckedChangeListener { _, _ ->
            Preferences(requireActivity()).swapUnits()
        }

        binding.about.moreInfoButton.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }

        binding.languageSelector.setOnItemClickListener { _, _, pos, _ ->
            when (pos) {
                0 -> Preferences(requireActivity()).setLanguage(extrasLang[1])
                else -> Preferences(requireActivity()).setLanguage(extrasLang[2])
            }
        }

        binding.citySelector.setOnItemClickListener { adapterView, view, i, l ->
            Preferences(requireActivity()).setMyCity(
                binding.citySelector.adapter.getItem(i).toString()
            )
        }

        binding.clearMyCities.setOnClickListener {
            sharedViewModel.removeAllCitiesFromFavourites(requireContext())
        }

        binding.clearRecentSearch.setOnClickListener {
            sharedViewModel.removeAllCitiesFromRecents(requireContext())
        }

    }

    private fun loadSpinners() {

        val languageAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            listOf(getString(R.string.english), getString(R.string.croatian))
        )
        (binding.languageSelector as? MaterialAutoCompleteTextView)?.setAdapter(languageAdapter)

        sharedViewModel.getFavourites().observe(viewLifecycleOwner) {

            if (sharedViewModel.getFavouritesNames()?.isNotEmpty() == true) {
                val myCityAdapter = ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_item,
                    sharedViewModel.getFavouritesNames()!!
                )
                (binding.citySelector as? MaterialAutoCompleteTextView)?.setAdapter(myCityAdapter)

                loadAppPreferenes()

            }
        }
    }

    private fun loadAppPreferenes() {
        when (preferences.getString(extrasUnit[0], extrasUnit[1])) {
            extrasUnit[1] -> binding.unitSelector.unitRadioGroup.check(R.id.metricButton)
            extrasUnit[2] -> binding.unitSelector.unitRadioGroup.check(R.id.imperialButton)
        }

        when (preferences.getString(extrasLang[0], extrasLang[1])) {
            extrasLang[1] -> binding.languageSelector.setText(getString(R.string.english), false)
            else -> binding.languageSelector.setText(getString(R.string.croatian), false)
        }

        val myCityIndex = sharedViewModel.getFavouritesNames()
            ?.indexOf(Preferences(requireActivity()).getMyCity())
        myCityIndex?.let { binding.citySelector.setSelection(it) }
    }
}