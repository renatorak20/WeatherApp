package com.renato.weatherapp.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.renato.weatherapp.R
import com.renato.weatherapp.databinding.FragmentMyCitiesBinding


class MyCitiesFragment : Fragment() {

    private lateinit var binding: FragmentMyCitiesBinding
    private var isEditable = false
    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyCitiesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.toolbar
        toolbar.menu.findItem(R.id.menu_item_done).isVisible = false

        setListeners()

    }

    fun swapIcons(){
        if(!isEditable){
            isEditable = !isEditable
            toolbar.menu.findItem(R.id.menu_item_edit).isVisible = false
            toolbar.menu.findItem(R.id.menu_item_calendar).isVisible = false
            toolbar.menu.findItem(R.id.menu_item_done).isVisible = true
        }else{
            isEditable = !isEditable
            toolbar.menu.findItem(R.id.menu_item_edit).isVisible = true
            toolbar.menu.findItem(R.id.menu_item_calendar).isVisible = true
            toolbar.menu.findItem(R.id.menu_item_done).isVisible = false
        }
    }

    fun setListeners(){
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