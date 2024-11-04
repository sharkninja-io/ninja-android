package com.sharkninja.ninja.connected.kitchen.ui.components.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sharkninja.cloudcore.WifiNetwork
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.ViewWifiNetworkBinding
import com.sharkninja.ninja.connected.kitchen.extensions.getWifiSignalStrengthDrawable

class WifiNetworkRecyclerViewAdapter(
    private var wifiNetworkList: List<WifiNetwork>,
    private val onOptionSelected: (WifiNetwork) -> Unit
) : RecyclerView.Adapter<WifiNetworkRecyclerViewAdapter.WifiNetworkViewHolder>() {
    private var currentSelection: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiNetworkViewHolder {
        return WifiNetworkViewHolder(
            binding = ViewWifiNetworkBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WifiNetworkViewHolder, position: Int) {
        holder.setWifiNetwork(
            wifiNetwork = wifiNetworkList[position],
            isSelected = wifiNetworkList[position].ssid == currentSelection,
            onOptionSelected = onOptionSelected
        )
    }

    fun updateList(networkList: List<WifiNetwork>) {
        wifiNetworkList = networkList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return wifiNetworkList.size
    }

    private fun setCurrentSelection(wifiNetwork: WifiNetwork) {
        val prevSelection = currentSelection
        currentSelection = wifiNetwork.ssid ?: ""

        val prevWifiNetwork = wifiNetworkList.firstOrNull {
            it.ssid == prevSelection
        }

        val currentWifiNetwork = wifiNetworkList.firstOrNull {
            it.ssid == currentSelection
        }

        notifyItemChanged(wifiNetworkList.indexOf(prevWifiNetwork))
        notifyItemChanged(wifiNetworkList.indexOf(currentWifiNetwork))
    }

    inner class WifiNetworkViewHolder(
        private val binding: ViewWifiNetworkBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun setWifiNetwork(
            wifiNetwork: WifiNetwork,
            isSelected: Boolean = false,
            onOptionSelected: (WifiNetwork) -> Unit
        ) {
            binding.signalStrengthImageView.setImageResource(wifiNetwork.getWifiSignalStrengthDrawable())

            binding.wifiNetworkNameTextView.setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
            binding.root.setBackgroundColor(
                if (isSelected) {
                    ContextCompat.getColor(binding.root.context, R.color.lighter_grey)
                } else {
                    Color.TRANSPARENT
                }
            )

            binding.wifiNetworkNameTextView.text = wifiNetwork.ssid

            binding.root.setOnClickListener {
                setCurrentSelection(wifiNetwork)
                onOptionSelected(wifiNetwork)
            }
        }
    }
}