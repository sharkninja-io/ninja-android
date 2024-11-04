package com.sharkninja.ninja.connected.kitchen.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sharkninja.grillcore.CookMode
import com.sharkninja.grillcore.DegreeType
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.COLD_SMOKE_DISPLAY
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.COLD_SMOKE_TEMP_CELSIUS
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.COLD_SMOKE_TEMP_FAREN
import com.sharkninja.ninja.connected.kitchen.data.enums.getDisplayName
import com.sharkninja.ninja.connected.kitchen.data.enums.getDisplayTemp
import com.sharkninja.ninja.connected.kitchen.data.models.cookitems.GrillTemperatureItem
import com.sharkninja.ninja.connected.kitchen.databinding.ItemTemperatureDefaultBinding
import com.sharkninja.ninja.connected.kitchen.databinding.ItemTemperatureGrillBinding

class GrillTempListAdapter(
    var tempList: List<GrillTemperatureItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var temperatureUnit: DegreeType = DegreeType.Fahrenheit
    var cookMode: CookMode = CookMode.Grill

    var mIsDarkTheme: Boolean = false


    inner class DefaultViewHolder(val binding: ItemTemperatureDefaultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GrillTemperatureItem, position: Int) {
            val isFarenheight = temperatureUnit == DegreeType.Fahrenheit
            val tempUnit = if (isFarenheight) DegreeType.Fahrenheit.getDisplayName() else DegreeType.Celsius.getDisplayName()
            val tempUnitDisplay = binding.root.context.getString(tempUnit)

            val coldSmokeTemp = if(isFarenheight) COLD_SMOKE_TEMP_FAREN else COLD_SMOKE_TEMP_CELSIUS
            val isColdSmokeTemp = cookMode === CookMode.Smoke && item.tempString == coldSmokeTemp
            if(item.isSelected) {
                binding.tempSelected.visibility = View.VISIBLE
//                binding.degreeTv.visibility = View.VISIBLE
                if (isColdSmokeTemp) {
                    binding.tempSelected.text = COLD_SMOKE_DISPLAY
//                    binding.degreeTv.visibility = View.GONE
                } else {
                    binding.tempSelected.text = "${item.tempString} $tempUnitDisplay"
                }

                binding.tempUnselected.visibility = View.INVISIBLE

//                binding.degreeTv.text = if (isFarenheight) TemperatureUnit.FA.displayName else TemperatureUnit.C.displayName

            } else {
                binding.tempUnselected.visibility = View.VISIBLE
                binding.tempUnselected.text = if (isColdSmokeTemp) COLD_SMOKE_DISPLAY else "${item.tempString} $tempUnitDisplay"

                binding.tempSelected.visibility = View.GONE
//                binding.degreeTv.visibility = View.GONE
            }

            if(position == itemCount - 1) {
                binding.tempLineShortBottom.visibility = View.GONE
                binding.tempLineShortBottomMiddle.visibility = View.GONE
            } else {
                binding.tempLineShortBottom.visibility = View.VISIBLE
                binding.tempLineShortBottomMiddle.visibility = View.VISIBLE
            }

            if(position == 0) {
                binding.tempLineShortTop.visibility = View.GONE
                binding.tempLineShortTopMiddle.visibility = View.GONE
            } else {
                binding.tempLineShortTop.visibility = View.VISIBLE
                binding.tempLineShortTopMiddle.visibility = View.VISIBLE
            }

            if(mIsDarkTheme) {
                binding.tempSelected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.mc_text_and_icon_color))
                binding.tempUnselected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
//                binding.degreeTv.setTextColor(ContextCompat.getColor(binding.root.context, R.color.mc_text_and_icon_color))
                binding.tempLineShortBottom.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
                binding.tempLineShortBottomMiddle.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
                binding.tempLineShortTopMiddle.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
                binding.tempLineShortTop.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
                binding.tempLineLongCenter.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
            } else {
                binding.tempSelected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black1))
                binding.tempUnselected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
//                binding.degreeTv.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black1))
                binding.tempLineShortBottom.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
                binding.tempLineShortBottomMiddle.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
                binding.tempLineShortTopMiddle.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
                binding.tempLineShortTop.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
                binding.tempLineLongCenter.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
            }
        }
    }

    inner class GrillViewHolder(val binding: ItemTemperatureGrillBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GrillTemperatureItem, position: Int) {

            if(mIsDarkTheme) {
                binding.tempSelected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.mc_text_and_icon_color))
                binding.tempUnselected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
                binding.tempLineShortBottom.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
                binding.tempLineShortTop.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
                binding.tempLineLongCenter.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.temp_dial_line_dark_theme))
            } else {
                binding.tempSelected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black1))
                binding.tempUnselected.setTextColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
                binding.tempLineShortBottom.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
                binding.tempLineShortTop.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
                binding.tempLineLongCenter.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.dial_line_color))
            }

            val tempDisplay = binding.root.context.getString(getDisplayTemp(item.tempString)).uppercase()

            if(item.isSelected) {
                binding.tempUnselected.visibility = View.INVISIBLE
                binding.tempSelected.text = tempDisplay
                binding.tempSelected.visibility = View.VISIBLE

            } else {
                binding.tempSelected.visibility = View.INVISIBLE
                binding.tempUnselected.text = tempDisplay
                binding.tempUnselected.visibility = View.VISIBLE
            }

            if(position == itemCount - 1) {
                binding.tempLineShortBottom.visibility = View.GONE
            } else {
                binding.tempLineShortBottom.visibility = View.VISIBLE
            }

            if(position == 0) {
                binding.tempLineShortTop.visibility = View.GONE
            } else {
                binding.tempLineShortTop.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
       return when (cookMode) {
           CookMode.Grill -> { GRILL_COOK_MODE_VIEW_TYPE }
           else -> { DEFAULT_VIEW_TYPE }
       }
    }

    fun updateList(grillTempList: List<GrillTemperatureItem>) {
        this.tempList = grillTempList
        notifyDataSetChanged()
    }

    fun updateTemperatureUnit(temperatureUnit: DegreeType) {
        this.temperatureUnit = temperatureUnit
        notifyDataSetChanged()
    }

    fun setColorTheme(isDarkTheme: Boolean) {
        mIsDarkTheme = isDarkTheme
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            GRILL_COOK_MODE_VIEW_TYPE -> GrillViewHolder(ItemTemperatureGrillBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> DefaultViewHolder(ItemTemperatureDefaultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(getItemViewType(position)) {
            DEFAULT_VIEW_TYPE -> (holder as DefaultViewHolder).bind(tempList[position], position)
            GRILL_COOK_MODE_VIEW_TYPE -> (holder as GrillViewHolder).bind(tempList[position], position)
            else -> {}
        }
    }

    override fun getItemCount(): Int {
        return tempList.size
    }

    companion object {
        const val DEFAULT_VIEW_TYPE = 0
        const val GRILL_COOK_MODE_VIEW_TYPE = 1
    }
}