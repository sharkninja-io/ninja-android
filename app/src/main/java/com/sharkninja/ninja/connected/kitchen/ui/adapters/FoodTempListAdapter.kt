package com.sharkninja.ninja.connected.kitchen.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sharkninja.grillcore.DegreeType
import com.sharkninja.grillcore.Doneness
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.getDisplayName
import com.sharkninja.ninja.connected.kitchen.data.models.cookitems.FoodTemperatureItem
import com.sharkninja.ninja.connected.kitchen.databinding.ItemInternalTemperatureBinding
import com.sharkninja.ninja.connected.kitchen.ext.displayName

class FoodTempListAdapter(
    var internalTempList: List<FoodTemperatureItem>
    ) : RecyclerView.Adapter<FoodTempListAdapter.ViewHolder>() {

    var temperatureUnit: DegreeType = DegreeType.Fahrenheit
    var mIsDarkTheme: Boolean = false


    inner class ViewHolder(val binding: ItemInternalTemperatureBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(temp: FoodTemperatureItem) {
            if(mIsDarkTheme) {
                binding.tempSelected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.mc_text_and_icon_color))
                binding.tempUnselected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
                binding.donenessPointTvUnselected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
//                binding.degreeTv.setTextColor(ContextCompat.getColor(binding.root.context, R.color.mc_text_and_icon_color))
                binding.donenessPointTv.setTextColor(ContextCompat.getColor(binding.root.context, R.color.mc_text_and_icon_color))
                binding.tempLineShort.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
//                binding.tempLineLongCenter.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
            } else {
                binding.tempSelected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black1))
                binding.tempUnselected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
                binding.donenessPointTvUnselected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
//                binding.degreeTv.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black1))
                binding.donenessPointTv.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black1))
                binding.tempLineShort.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
//                binding.tempLineLongCenter.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
            }
            val item = internalTempList[bindingAdapterPosition]

            if(item.isSelected) {
                binding.tempSelected.visibility = View.VISIBLE
                binding.donenessPointTv.visibility = View.VISIBLE
                binding.tempSelected.text = "${temp.tempString} ${binding.root.context.getString(temperatureUnit.getDisplayName())}"
                binding.tempUnselected.visibility = View.GONE
                binding.donenessPointTvUnselected.visibility = View.GONE
                binding.tempLineShort.alpha = 1f
//                binding.degreeTv.visibility = View.VISIBLE
//                binding.degreeTv.text = binding.root.context.getString(temperatureUnit.getDisplayName())

            } else {
                binding.tempUnselected.visibility = View.VISIBLE
                binding.donenessPointTvUnselected.visibility = View.VISIBLE
                binding.tempUnselected.text = temp.tempString
                binding.tempSelected.visibility = View.GONE
                binding.donenessPointTv.visibility = View.GONE
                binding.tempLineShort.alpha = .3f
//                binding.degreeTv.visibility = View.GONE
            }

//            if (item.isIncrementNumber) {
//                binding.tempLineLongCenter.visibility = View.VISIBLE
//                binding.tempLineShort.visibility = View.GONE
//            } else {
//                binding.tempLineShort.visibility = View.VISIBLE
//                binding.tempLineLongCenter.visibility = View.GONE
//            }

            if(item.donenessLevel != Doneness.NotSet) {
//                binding.donenessPointIcon.visibility = View.VISIBLE
                binding.donenessPointTv.text = binding.root.context.getString(item.donenessLevel.displayName())
                binding.donenessPointTvUnselected.text = binding.root.context.getString(item.donenessLevel.displayName())
            } else {
//                binding.donenessPointIcon.visibility = View.INVISIBLE
                binding.donenessPointTv.visibility = View.INVISIBLE
                binding.donenessPointTvUnselected.visibility = View.INVISIBLE
            }
        }
    }

    fun updateList(internalTempList: List<FoodTemperatureItem>) {
        this.internalTempList = internalTempList
        notifyDataSetChanged()
    }

    fun updateTemperatureUnit(temperatureUnit: DegreeType) {
        this.temperatureUnit = temperatureUnit
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInternalTemperatureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(internalTempList[position])
    }

    override fun getItemCount(): Int {
        return internalTempList.size
    }

    fun setColorTheme(isDarkTheme: Boolean) {
        mIsDarkTheme = isDarkTheme
    }
}