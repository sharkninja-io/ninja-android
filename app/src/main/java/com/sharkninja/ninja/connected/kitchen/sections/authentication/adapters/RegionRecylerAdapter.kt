package com.sharkninja.ninja.connected.kitchen.sections.authentication.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.RegionCode
import com.sharkninja.ninja.connected.kitchen.databinding.RecyclerviewItemRegionRowBinding
import java.util.*

class RegionRecyclerAdapter(private val context: Context, private val regions: MutableList<String>,
                            private var defaultSelectionPosition: Int?,
                            private val listener: RegionInterface): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var previousSelection: RegionHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RegionHolder(RecyclerviewItemRegionRowBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return regions.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = regions[position]
        (holder as RegionHolder).bindRegion(item)
        if (defaultSelectionPosition != null) {
            if (defaultSelectionPosition == position) {
                holder.setSelected(true)
                defaultSelectionPosition = null
                previousSelection = holder
                listener.regionSelected(regions[position])
            }
        }
        holder.setOnClickListener(View.OnClickListener {
            if (previousSelection != null && previousSelection != holder) {
                previousSelection!!.setSelected(false)
                notifyItemChanged(previousSelection!!.adapterPosition)
            }
            previousSelection = holder
            previousSelection?.setSelected(true)
            listener.regionSelected(regions[position])
        })

        if (position == regions.size-1) {
            holder.hideDivider()
        }
    }

    interface RegionInterface {
        fun regionSelected(regionCode: String)
    }

    private class RegionHolder(private val binding: RecyclerviewItemRegionRowBinding) : RecyclerView.ViewHolder(binding.root) {
        private var region: String? = null
        private var isSelected: Boolean = false

        fun bindRegion(region: String) {
            this.region = region
//            if (region == RegionCode.RestOfWorld.toString()) {
//                binding.itemRegion.text = itemView.context.getString(R.string.a4131d16e5bf6d00e74011851ee8980a4e0b495f5)
//            } else {
                val loc = Locale("", region)
                binding.itemRegion.text = loc.displayCountry
//            }
        }

        fun hideDivider() {
            binding.divider.visibility = View.GONE
        }

        fun setOnClickListener(onClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onClickListener)
        }

        fun setSelected(isSelected: Boolean) {
            this.isSelected = isSelected

            if (isSelected) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.ninja_green_selector))
//                binding.itemRegion.typeface = SharkApp.getFontBold()
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
//                binding.itemRegion.typeface = SharkApp.getFontRegular()
            }
        }
    }
}