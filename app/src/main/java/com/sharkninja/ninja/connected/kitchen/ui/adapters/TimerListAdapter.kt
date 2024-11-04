package com.sharkninja.ninja.connected.kitchen.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.models.cookitems.TimerItem
import com.sharkninja.ninja.connected.kitchen.databinding.ItemTimerBinding

class TimerListAdapter(
    var timerList: List<TimerItem>
) : RecyclerView.Adapter<TimerListAdapter.ViewHolder>() {

    var mIsDarkTheme: Boolean = false
    inner class ViewHolder(val binding: ItemTimerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TimerItem) {
            if(mIsDarkTheme) {
                binding.numberSelected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.mc_text_and_icon_color))
                binding.numberUnselected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
            } else {
                binding.numberSelected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black1))
                binding.numberUnselected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
            }
            if(item.isSelected) {
                binding.numberSelected.visibility = View.VISIBLE
                binding.numberSelected.text = item.displayTimeString
                binding.numberUnselected.visibility = View.GONE

            } else {
                binding.numberUnselected.visibility = View.VISIBLE
                binding.numberUnselected.text = item.displayTimeString
                binding.numberSelected.visibility = View.GONE
            }
        }
    }

    fun updateList(timerList: List<TimerItem>) {
        this.timerList = timerList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(timerList[position])
    }


    override fun getItemCount(): Int {
        return timerList.size
    }

    fun setColorTheme(isDarkTheme: Boolean) {
        mIsDarkTheme = isDarkTheme
    }
}