package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sharkninja.grillcore.CookMode
import com.sharkninja.grillcore.Food
import com.sharkninja.ninja.connected.kitchen.R
object FoodTypeStrings {
    const val chicken = "poultry"
    const val beef = "beef"
    const val pork = "pork"
    const val seafood = "seafood"
    const val veggies = "vegetables"
    const val bread = "bread/cheese"
    const val fruit = "fruit"
    const val lamb = "lamb/veal"
}

object CookTypeStrings {
    const val grill = "grill"
    const val smoke = "smoke"
    const val airCrisp = "aircrisp"
    const val roast = "roast"
    const val bake = "bake"
    const val broil = "broil"
    const val dehydrate = "dehydrate"
    const val reheat = "reheat"
}
data class CookingCard(
    val image: Int,
    val meatType: String = "Chicken",
    val cookingMethod: String,
    val name: String,
    val description: String,
    val preparation: String,
    val grillTemperatureF: Int,
    val grillTemperatureGeneric: String,
    val cookTime: String,
    val notes: String,

    var cookMode: CookMode? = null,
    var duration: Int = 0,
    var infuse: Boolean = false,
    var setProbe0Manual: Int = 0,
    var setProbe0Preset: Food = Food.Beef,
    var setProbe1Manual: Int = 0,
    var setProbe1Preset: Food = Food.Beef,
    var setTemp: Int = 0
)

private lateinit var ctx: Context

class CustomChartSelectorAdapter(private val mList: List<CookingCard>) : RecyclerView.Adapter<CustomChartSelectorAdapter.ViewHolder>() {

    private lateinit var mListener : OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        ctx = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_cooking_charts_card, parent, false)

        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val itemsViewModel = mList[position]

        holder.recipeName.text = itemsViewModel.name
        holder.cookingMethod.text = itemsViewModel.cookingMethod.uppercase()
        holder.imageResource?.setBackgroundResource(itemsViewModel.image)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getItem(index: Int): CookingCard {
        return mList[index]
    }

    class ViewHolder(ItemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        val imageResource: View? = itemView.findViewById(R.id.imageResource)
        val cookingMethod: TextView = itemView.findViewById(R.id.cookingMethod)
        val recipeName: TextView = itemView.findViewById(R.id.recipeName)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }
}

class CustomFilteredChartSelectectorAdaptor(private val mList: List<CookingCard>, tempUnit: String) : RecyclerView.Adapter<CustomFilteredChartSelectectorAdaptor.ViewHolder>() {

    private lateinit var mListener : OnItemClickListener
    private val tempUnit = tempUnit

    fun secondsToHoursAndMins(time: Int) : String {
        val hours = (time / 3600).toInt()
        var minutes = (time / 60).toInt()
        val seconds = time - (minutes * 60)

        return if (hours > 0) {
            minutes = ((time - (3600 * hours)) / 60).toInt()
            if (minutes > 0) {
                "${hours}h ${minutes}m"
            } else {
                "${hours}h"
            }
        } else {
            if (seconds > 0) {
                "${minutes}m ${seconds}s"
            } else {
                "${minutes}m"
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        ctx = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.filtered_chart_card, parent, false)

        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val itemsViewModel = mList[position]

        holder.recipeTitle.text = itemsViewModel.name
        holder.cookType.text = itemsViewModel.cookingMethod.uppercase()
        holder.image.setBackgroundResource(itemsViewModel.image)
        holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
        holder.cookTime.text = secondsToHoursAndMins(itemsViewModel.cookTime.toInt())

        when (itemsViewModel.meatType.lowercase()) {
            FoodTypeStrings.chicken -> {
                holder.foodTypeImageBackground.setBackgroundResource(R.drawable.explore_food_icon_background_24)
                holder.foodTypeImage.setBackgroundResource(R.drawable.ic_explore_poultry_unselected)
            }
            FoodTypeStrings.beef -> {
                holder.foodTypeImageBackground.setBackgroundResource(R.drawable.explore_food_icon_background_24)
                holder.foodTypeImage.setBackgroundResource(R.drawable.ic_explore_beef_unselected)
            }
            FoodTypeStrings.pork -> {
                holder.foodTypeImageBackground.setBackgroundResource(R.drawable.explore_food_icon_background_24)
                holder.foodTypeImage.setBackgroundResource(R.drawable.ic_explore_pork_unselected)
            }
            FoodTypeStrings.seafood -> {
                holder.foodTypeImageBackground.setBackgroundResource(R.drawable.explore_food_icon_background_24)
                holder.foodTypeImage.setBackgroundResource(R.drawable.ic_explore_seafood_unselected)
            }
            FoodTypeStrings.veggies -> {
                holder.foodTypeImageBackground.setBackgroundResource(R.drawable.explore_food_icon_background_24)
                holder.foodTypeImage.setBackgroundResource(R.drawable.ic_explore_carrot_unselected)
            }
            FoodTypeStrings.bread -> {
                holder.foodTypeImageBackground.setBackgroundResource(R.drawable.explore_food_icon_background_24)
                holder.foodTypeImage.setBackgroundResource(R.drawable.ic_explore_cheese_unselected)
            }
            FoodTypeStrings.fruit -> {
                holder.foodTypeImageBackground.setBackgroundResource(R.drawable.explore_food_icon_background_24)
                holder.foodTypeImage.setBackgroundResource(R.drawable.ic_explore_fruit_unselected)
            }
            FoodTypeStrings.lamb -> {
                holder.foodTypeImageBackground.setBackgroundResource(R.drawable.explore_food_icon_background_24)
                holder.foodTypeImage.setBackgroundResource(R.drawable.ic_explore_lamb_unselected)
            }
            else -> {
                holder.foodTypeImageBackground.visibility = View.INVISIBLE
                holder.foodTypeImage.visibility = View.INVISIBLE
            }
        }

        if (itemsViewModel.grillTemperatureF == 0) {
            holder.cookTemp.text = itemsViewModel.grillTemperatureGeneric
        } else {
            holder.cookTemp.text = itemsViewModel.grillTemperatureF.toString() + tempUnit
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getItem(index: Int): CookingCard {
        return mList[index]
    }

    class ViewHolder(ItemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        val image: ImageView = itemView.findViewById(R.id.image)
        val cookType: TextView = itemView.findViewById(R.id.cookType)
        val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        val cookTime: TextView = itemView.findViewById(R.id.cookTime)
        val cookTemp: TextView = itemView.findViewById(R.id.cookTemp)
        val foodTypeImageBackground: View = itemView.findViewById(R.id.foodTypeImageBackground)
        val foodTypeImage: View = itemView.findViewById(R.id.foodTypeImage)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }
        }
    }
}