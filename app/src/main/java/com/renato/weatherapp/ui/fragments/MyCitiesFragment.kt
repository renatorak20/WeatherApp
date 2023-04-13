package com.renato.weatherapp.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.renato.weatherapp.R
import com.renato.weatherapp.adapters.CityListAdapter
import com.renato.weatherapp.adapters.ItemTouchCallback
import com.renato.weatherapp.databinding.FragmentMyCitiesBinding
import com.renato.weatherapp.util.Preferences
import com.renato.weatherapp.util.Utils
import com.renato.weatherapp.viewmodel.SharedViewModel
import java.util.ArrayList


class MyCitiesFragment : Fragment() {

    private lateinit var binding: FragmentMyCitiesBinding
    private var isEditable = false
    private lateinit var toolbar: Toolbar
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var recyclerAdapter: CityListAdapter
    private lateinit var itemTouchCallback: ItemTouchCallback

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
        binding.lastUpdated.text = sharedViewModel.getFavLastUpdated().value


        sharedViewModel.getFavouritesFromDb(requireContext())
        getUpdatedFavourites()

        itemTouchCallback = ItemTouchCallback(binding.recyclerView)
        itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        sharedViewModel.getFavourites().observe(viewLifecycleOwner) { favourites ->
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerAdapter = CityListAdapter(
                requireContext(),
                favourites as ArrayList<Any>,
                sharedViewModel,
                Preferences(requireActivity()).getCurrentUnits(),
                requireActivity()
            )
            binding.recyclerView.adapter = recyclerAdapter
        }

        sharedViewModel.getFavLastUpdated().observe(viewLifecycleOwner) {
            binding.lastUpdated.text = sharedViewModel.getFavLastUpdated().value
        }

        setListeners()
    }


    private fun swapIcons() {
        if (!isEditable) {
            toolbar.menu.findItem(R.id.menu_item_edit).isVisible = false
            toolbar.menu.findItem(R.id.menu_item_calendar).isVisible = false
            toolbar.menu.findItem(R.id.menu_item_done).isVisible = true
        } else {
            toolbar.menu.findItem(R.id.menu_item_edit).isVisible = true
            toolbar.menu.findItem(R.id.menu_item_calendar).isVisible = true
            toolbar.menu.findItem(R.id.menu_item_done).isVisible = false
        }
        isEditable = !isEditable
        recyclerAdapter.reorderSwitch = !recyclerAdapter.reorderSwitch
        itemTouchCallback.changeEnabled()
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun setListeners() {
        binding.toolbar.menu.findItem(R.id.menu_item_edit).setOnMenuItemClickListener {
            swapIcons()
            return@setOnMenuItemClickListener false
        }
        binding.toolbar.menu.findItem(R.id.menu_item_done).setOnMenuItemClickListener {
            swapIcons()
            return@setOnMenuItemClickListener false
        }
        binding.toolbar.menu.findItem(R.id.menu_item_calendar).setOnMenuItemClickListener {
            getUpdatedFavourites()
            return@setOnMenuItemClickListener false
        }
    }

    private fun getUpdatedFavourites() {
        if (context?.let { Utils().isNetworkAvailable(it) } == true) {
            sharedViewModel.getUpdatedFavourites(requireActivity())
        } else {
            Snackbar.make(
                requireView(),
                resources.getString(R.string.errorRefreshing),
                Snackbar.LENGTH_SHORT
            )
                .setAnchorView(requireActivity().findViewById(R.id.nav_view))
                .show()
            sharedViewModel.setFavouritesLatestUpdate(requireActivity())
        }
    }
}