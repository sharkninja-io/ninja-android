package com.sharkninja.ninja.connected.kitchen.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sharkninja.ninja.connected.kitchen.data.models.Appliance
import com.sharkninja.ninja.connected.kitchen.databinding.ItemApplianceBinding
import com.sharkninja.ninja.connected.kitchen.R

class ApplianceListAdapter(private val mList: List<Appliance>, private val onItemClicked: (appliance: Appliance) -> Unit) : RecyclerView.Adapter<ApplianceListAdapter.ViewHolder>() {

    private var previousBinding: ItemApplianceBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemApplianceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(val binding: ItemApplianceBinding, private val onItemClicked: (appliance: Appliance) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(appliance: Appliance) {
            binding.applianceContainer.setOnClickListener {
                previousBinding?.let { prev ->
                    previousBinding = binding
                    prev.applianceContainer.setBackgroundResource(
                        R.drawable.appliance_item_background_unselected
                    ) } ?: run { previousBinding = binding }

                it.setBackgroundResource(R.drawable.appliance_item_background_selected)
                onItemClicked.invoke(appliance)
            }
            binding.applianceName.text = appliance.name
            var macAddress = if(appliance.macAddress.isEmpty()) appliance.details.mac else "MAC: ${appliance.macAddress}"
            binding.modelNumber.text = macAddress
        }
    }
}