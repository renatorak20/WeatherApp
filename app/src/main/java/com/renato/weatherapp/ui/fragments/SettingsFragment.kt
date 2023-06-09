package com.renato.weatherapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.renato.weatherapp.AboutActivity
import com.renato.weatherapp.R
import com.renato.weatherapp.databinding.FragmentSettingsBinding
import com.renato.weatherapp.util.Preferences
import com.renato.weatherapp.util.Utils
import com.renato.weatherapp.notifications.ReminderNotificationWorker
import com.renato.weatherapp.viewmodel.SharedViewModel


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var extrasUnit: List<String>
    private lateinit var extrasLang: Array<String>

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
        scheduleReminderNotification()

        binding.about.moreInfoButton.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }

        binding.languageSelector.setOnItemClickListener { _, _, pos, _ ->
            when (pos) {
                0 -> Preferences(requireContext()).setLanguage(extrasLang[1], requireActivity())
                else -> Preferences(requireContext()).setLanguage(extrasLang[2], requireActivity())
            }
        }

        binding.citySelector.setOnItemClickListener { adapterView, view, i, l ->
            Preferences(requireActivity()).setMyCity(
                binding.citySelector.adapter.getItem(i).toString(),
                sharedViewModel.getFavourites().value?.get(i)?.latitude!!,
                sharedViewModel.getFavourites().value?.get(i)?.longitude!!
            )
            if (Utils().isNetworkAvailable(requireContext())) {
                Utils().updateWidget(requireContext())
            }
        }

        binding.clearMyCities.setOnClickListener {
            sharedViewModel.removeAllCitiesFromFavourites(requireContext())
        }

        binding.clearRecentSearch.setOnClickListener {
            showClearRecentDialog()
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
                val citiesNames = sharedViewModel.getFavouritesNames()
                val myCityAdapter: ArrayAdapter<String> = ArrayAdapter(
                    requireContext(),
                    R.layout.spinner_item,
                    citiesNames!!
                )
                (binding.citySelector as? MaterialAutoCompleteTextView)?.setAdapter(myCityAdapter)
            }
            loadAppPreferenes()
        }
    }

    private fun loadAppPreferenes() {
        when (preferences.getString(extrasUnit[0], extrasUnit[1])) {
            extrasUnit[1] -> binding.unitSelector.unitRadioGroup.check(R.id.metricButton)
            else -> binding.unitSelector.unitRadioGroup.check(R.id.imperialButton)
        }

        when (preferences.getString(extrasLang[0], extrasLang[1])) {
            extrasLang[1] -> binding.languageSelector.setText(getString(R.string.english), false)
            else -> binding.languageSelector.setText(getString(R.string.croatian), false)
        }

        val names = sharedViewModel.getFavouritesNames()
        if (names?.contains(Preferences(requireActivity()).getMyCity()) == true) {
            binding.citySelector.setText(Preferences(requireContext()).getMyCity(), false)
        }

        binding.unitSelector.unitRadioGroup.setOnCheckedChangeListener { _, _ ->
            Preferences(requireContext()).swapUnits(requireActivity())
        }

    }

    private fun showClearRecentDialog() {

        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(resources.getString(R.string.clear_recent_title))
            .setMessage(resources.getString(R.string.clear_recent_desc))
            .setNegativeButton(resources.getString(R.string.cancel_cap)) { dialogInterface, i ->

            }
            .setPositiveButton(resources.getString(R.string.clear_cap)) { dialogInterface, i ->
                sharedViewModel.removeAllCitiesFromRecents(requireContext())

                val snackbar = Snackbar.make(requireView(), "", Snackbar.LENGTH_SHORT)
                val customSnackView =
                    layoutInflater.inflate(R.layout.recents_deleted_snackbar, null)
                snackbar.view.setBackgroundColor(Color.TRANSPARENT)
                val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
                snackbarLayout.setPadding(0, 0, 0, 0)
                snackbarLayout.addView(customSnackView, 0)
                snackbar.anchorView = requireActivity().findViewById(R.id.nav_view)
                snackbar.view.findViewById<ImageView>(R.id.snackbar_dismiss).setOnClickListener {
                    snackbar.dismiss()
                }
                snackbar.show()
            }
            .show()
    }

    fun scheduleReminderNotification() {
        ReminderNotificationWorker.schedule(requireContext())
    }
}