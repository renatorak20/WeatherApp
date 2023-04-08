package com.renato.weatherapp.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.renato.weatherapp.R
import com.renato.weatherapp.adapters.CityListAdapter
import com.renato.weatherapp.databinding.FragmentMyCitiesBinding
import com.renato.weatherapp.viewmodel.SharedViewModel


class MyCitiesFragment : Fragment() {

    private lateinit var binding: FragmentMyCitiesBinding
    private var isEditable = false
    private lateinit var toolbar: Toolbar
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyCitiesBinding.inflate(layoutInflater)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.toolbar
        toolbar.menu.findItem(R.id.menu_item_done).isVisible = false

        sharedViewModel.getFavouritesFromDb(requireContext())
        sharedViewModel.getUpdatedFavourites(requireActivity())

        sharedViewModel.getFavourites().observe(viewLifecycleOwner) { favourites ->
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter =
                CityListAdapter(requireContext(), favourites as ArrayList<Any>, this)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        sharedViewModel.getFavLastUpdated().observe(viewLifecycleOwner) {
            binding.lastUpdated.text = sharedViewModel.getFavLastUpdated().value
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            handleRefresh()
        }

        setListeners()
    }

    fun removeFavouriteCity(city: String) {
        sharedViewModel.removeCityFromFavourites(requireContext(), city)
    }

    fun handleRefresh() {
        sharedViewModel.getUpdatedFavourites(requireActivity())
    }

    fun swapIcons() {
        if (!isEditable) {
            isEditable = !isEditable
            toolbar.menu.findItem(R.id.menu_item_edit).isVisible = false
            toolbar.menu.findItem(R.id.menu_item_calendar).isVisible = false
            toolbar.menu.findItem(R.id.menu_item_done).isVisible = true
        } else {
            isEditable = !isEditable
            toolbar.menu.findItem(R.id.menu_item_edit).isVisible = true
            toolbar.menu.findItem(R.id.menu_item_calendar).isVisible = true
            toolbar.menu.findItem(R.id.menu_item_done).isVisible = false
        }
    }
    fun setListeners() {
        binding.toolbar.menu.findItem(R.id.menu_item_edit).setOnMenuItemClickListener {
            swapIcons()
            return@setOnMenuItemClickListener false
        }
        binding.toolbar.menu.findItem(R.id.menu_item_done).setOnMenuItemClickListener {
            swapIcons()
            return@setOnMenuItemClickListener false
        }
    }
}