package com.sharkninja.ninja.connected.kitchen.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sharkninja.grillcore.Grill
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.CardViewApplianceNameBinding

class CustomApplianceSelectorAdapter(private var mList: MutableList<Grill>) : RecyclerView.Adapter<CustomApplianceSelectorAdapter.ViewHolder>() {

    private lateinit var mListener : OnItemClickListener

    var mIsDefault: Boolean = true

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val binding = CardViewApplianceNameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, mListener)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateList(listOfAppliances: MutableList<Grill>) {
        this.mList = listOfAppliances
        notifyDataSetChanged()
    }

//    fun hideInitialSelectedItem(index: Int) {
//        hiddenItem = mList[index]
//        mList.removeAt(index)
//        notifyItemRemoved(index)
//        notifyItemRangeChanged(index, mList.size)
//    }

    fun changeColorThemeDarkModeCook(isDefault: Boolean) {
        mIsDefault = isDefault
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Grill {
        return mList[position]
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(val binding: CardViewApplianceNameBinding, listener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Grill) {
            binding.textView.text = item.getName()
            if(mIsDefault) {
                binding.textView.setTextColor(ContextCompat.getColor(binding.textView.context, R.color.black1))
                binding.root.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.white_8F))
            } else {
                binding.textView.setTextColor(ContextCompat.getColor(binding.textView.context, R.color.mc_text_and_icon_color))
                binding.root.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.darkest_grey))
            }
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }
}