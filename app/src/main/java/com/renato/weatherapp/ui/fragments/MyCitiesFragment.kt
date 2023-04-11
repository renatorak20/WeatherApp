package com.renato.weatherapp.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.renato.weatherapp.R
import com.renato.weatherapp.adapters.CityListAdapter
import com.renato.weatherapp.adapters.ItemTouchCallback
import com.renato.weatherapp.databinding.FragmentMyCitiesBinding
import com.renato.weatherapp.util.Preferences
import com.renato.weatherapp.viewmodel.SharedViewModel
import kotlin.collections.ArrayList


class MyCitiesFragment : Fragment() {

    private lateinit var binding: FragmentMyCitiesBinding
    private var isEditable = false
    private lateinit var toolbar: Toolbar
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var itemTouchHelper: ItemTouchHelper

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

        val itemTouchCallback = ItemTouchCallback(binding.recyclerView)
        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        sharedViewModel.getFavourites().observe(viewLifecycleOwner) { favourites ->
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            val adapter = CityListAdapter(
                requireContext(),
                favourites as ArrayList<Any>,
                sharedViewModel,
                Preferences(requireActivity()).getCurrentUnits()
            )
            binding.swipeRefreshLayout.isRefreshing = false
            binding.recyclerView.adapter = adapter
        }

        sharedViewModel.getFavLastUpdated().observe(viewLifecycleOwner) {
            binding.lastUpdated.text = sharedViewModel.getFavLastUpdated().value
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            handleRefresh()
        }



        setListeners()
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