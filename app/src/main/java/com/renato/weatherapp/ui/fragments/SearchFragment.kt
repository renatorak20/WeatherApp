package com.renato.weatherapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.renato.weatherapp.CityDetailActivity
import com.renato.weatherapp.R
import com.renato.weatherapp.adapters.CityListAdapter
import com.renato.weatherapp.data.model.WeatherRecent
import com.renato.weatherapp.databinding.FragmentSearchBinding
import com.renato.weatherapp.util.Preferences
import com.renato.weatherapp.util.Utils
import com.renato.weatherapp.viewmodel.SharedViewModel


class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        sharedViewModel.getAutoCompleteList().observe(viewLifecycleOwner) { cities ->
            if (cities.body()!!.isNotEmpty() && cities.isSuccessful) {
                val adapter = cities.body()
                    ?.let { cities ->
                        ArrayAdapter(
                            requireContext(),
                            R.layout.autocomplete_result_item,
                            cities.map { it.name })
                    }
                binding.autoCompleteCity.setAdapter(adapter)
                binding.autoCompleteCity.showDropDown()
            } else {
                Utils().showErrorDialog(requireContext())
            }
        }
        sharedViewModel.getRecentsFromDb(requireContext())

        sharedViewModel.getRecents().observe(viewLifecycleOwner) {
            updateRecentsRecyclerView(it)
        }

        binding.clearIcon.setOnClickListener {
            binding.autoCompleteCity.clear()
        }

        binding.autoCompleteCity.threshold = 2
        binding.autoCompleteCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(text: Editable?) {
                binding.autoCompleteCity.setOnKeyListener { view, code, keyEvent ->
                    run {
                        if (code == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN && checkForInternet() && !binding.autoCompleteCity.text.equals(
                                ""
                            )
                        ) {
                            dismissKeyboard(view.windowToken)
                            startCityDetailActivity()
                        }
                    }
                    false
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! == 3 && checkForInternet()) {
                    sharedViewModel.getNewAutoCompleteList(s.toString())
                } else if (s.isEmpty()) {
                    binding.autoCompleteCity.setAdapter(null)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        binding.autoCompleteCity.setText("")
        sharedViewModel.getRecentsFromDb(requireContext())
        sharedViewModel.getFavouritesFromDb(requireContext())

        sharedViewModel.getRecents().observe(viewLifecycleOwner) {
            updateRecentsRecyclerView(it)
        }
    }

    fun checkForInternet(): Boolean {
        return if (!Utils().isNetworkAvailable(requireContext())) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.error_title))
                .setMessage(getString(R.string.error_message))
                .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                    binding.autoCompleteCity.clear()
                }
                .setPositiveButton(getString(R.string.retry)) { dialog, which ->
                    checkForInternet()
                }
                .show()
            false
        } else {
            sharedViewModel.getNewAutoCompleteList(binding.autoCompleteCity.text.toString())
            true
        }
    }

    private fun dismissKeyboard(windowToken: IBinder) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }


    fun AutoCompleteTextView.clear() {
        this.text.clear()
    }

    fun startCityDetailActivity() {
        startActivity(
            Intent(requireContext(), CityDetailActivity::class.java).putExtra(
                getString(R.string.passing_data),
                binding.autoCompleteCity.text.toString()
            ).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        )
    }

    private fun updateRecentsRecyclerView(recents: List<WeatherRecent>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = CityListAdapter(
            requireContext(),
            recents as ArrayList<Any>,
            sharedViewModel,
            Preferences(requireActivity()).getCurrentUnits(),
            requireActivity()
        )
        binding.recyclerView.adapter = adapter
    }

}