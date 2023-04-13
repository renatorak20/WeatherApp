package com.renato.weatherapp.adapters

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemTouchCallback(val recView: RecyclerView) : ItemTouchHelper.Callback() {

    private var isEnabled = false

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {

        return if (isEnabled) {
            val flags = UP or DOWN
            return makeMovementFlags(flags, 0)
        } else {
            0
        }

    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val adapter = recView.adapter as CityListAdapter
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition
        CoroutineScope(IO).launch {
            adapter.moveItem(from, to)
        }
        adapter.notifyItemMoved(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }

    fun changeEnabled() {
        isEnabled = !isEnabled
    }

}