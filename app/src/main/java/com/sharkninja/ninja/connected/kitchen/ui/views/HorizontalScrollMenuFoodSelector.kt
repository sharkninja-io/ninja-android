package com.sharkninja.ninja.connected.kitchen.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.sharkninja.grillcore.Food
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.getMenuItem
import com.sharkninja.ninja.connected.kitchen.databinding.MenuFoodSelectorBinding
import com.sharkninja.ninja.connected.kitchen.ext.hide
import com.sharkninja.ninja.connected.kitchen.ext.setBGColor
import com.sharkninja.ninja.connected.kitchen.ext.setIconTintColor
import com.sharkninja.ninja.connected.kitchen.ext.show
import com.sharkninja.ninja.connected.kitchen.ext.updateIconLabelsDark
import com.sharkninja.ninja.connected.kitchen.ext.updateIconLabelsDefault
import kotlin.properties.Delegates

class HorizontalScrollMenuFoodSelector (context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    private var binding: MenuFoodSelectorBinding
    private var isDarkTheme: Boolean = false

    private var menuBackgroundColor: Int = R.color.cook_fragment_background
    private var labelColor: Int = R.color.black1

    var foodSelection: Food by Delegates.observable(
        Food.NotSet
    ) { _, _, newValue ->
        updateMenu(newValue)
    }

    var isUSGrill: Boolean by Delegates.observable(
        true
    ) { _, _, newValue ->
        setProteinOptions(newValue)
    }


    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = MenuFoodSelectorBinding.inflate(inflater, this, true)

        attrs?.let { setUpAttrs(it, context) }
        setUpViewLayout()
    }

    private fun setProteinOptions(isUSGrill: Boolean) {
        if(isUSGrill) {
            binding.containerLamb.hide()
        } else {
            binding.containerLamb.show()
        }
    }

    private fun setUpAttrs(attrs: AttributeSet, context: Context) {
        context.obtainStyledAttributes(attrs, R.styleable.FoodSelectorView).apply {

            getBoolean(R.styleable.FoodSelectorView_isDarkThemeMenuFood, false).also {
                isDarkTheme = it
                menuBackgroundColor = if(it) R.color.deep_black else R.color.cook_fragment_background
                labelColor = if(it) R.color.mc_text_and_icon_color else R.color.black1
            }

        }.recycle()
    }

    private fun setUpViewLayout() {
        setBackgroundColor(ContextCompat.getColor(context, menuBackgroundColor))
        binding.foodSelectorMenuLabel.setTextColor(ContextCompat.getColor(context, labelColor))
    }

    fun setMenuItemOnClicksListeners(block: (Food) -> Unit) {
        binding.manualIcon.setOnClickListener {
            block(Food.Manual)
        }
        binding.chickenIcon.setOnClickListener {
            block(Food.Chicken)
        }
        binding.beefIcon.setOnClickListener {
            block(Food.Beef)
        }
        binding.porkIcon.setOnClickListener {
            block(Food.Pork)
        }
        binding.fishIcon.setOnClickListener {
            block(Food.Fish)
        }
    }

    fun setProteinMenuItemOnClicksListeners(block: (Food) -> Unit) {
        binding.manualIcon.setOnClickListener {
            foodSelection = Food.Manual
            block(Food.Manual)
        }
        binding.chickenIcon.setOnClickListener {
            foodSelection = Food.Chicken
            block(Food.Chicken)
        }
        binding.beefIcon.setOnClickListener {
            foodSelection = Food.Beef
            block(Food.Beef)
        }
        binding.porkIcon.setOnClickListener {
            foodSelection = Food.Pork
            block(Food.Pork)
        }

        binding.lambIcon.setOnClickListener {
            foodSelection = Food.Lamb
            block(Food.Lamb)
        }
        binding.fishIcon.setOnClickListener {
            foodSelection = Food.Fish
            block(Food.Fish)
        }
    }


    private fun updateMenu(food: Food) {
        val selection = food.getMenuItem()
        if(isDarkTheme) {

            binding.manualIcon.setBGColor(context, selection.manual.iconColor.backgroundColorDark)
            binding.manualIconImage.setIconTintColor(context, selection.manual.iconColor.tintColorDark)
            binding.manualLabel.updateIconLabelsDark(context, selection.manual)

            binding.chickenIcon.setBGColor(context, selection.poultry.iconColor.backgroundColorDark)
            binding.chickenIconImage.setIconTintColor(context, selection.poultry.iconColor.tintColorDark)
            binding.chickenLabel.updateIconLabelsDark(context, selection.poultry)

            binding.beefIcon.setBGColor(context, selection.beef.iconColor.backgroundColorDark)
            binding.beefIconImage.setIconTintColor(context, selection.beef.iconColor.tintColorDark)
            binding.beefLabel.updateIconLabelsDark(context, selection.beef)

            binding.porkIcon.setBGColor(context, selection.pork.iconColor.backgroundColorDark)
            binding.porkIconImage.setIconTintColor(context, selection.pork.iconColor.tintColorDark)
            binding.porkLabel.updateIconLabelsDark(context, selection.pork)

            binding.fishIcon.setBGColor(context, selection.fish.iconColor.backgroundColorDark)
            binding.fishIconImage.setIconTintColor(context, selection.fish.iconColor.tintColorDark)
            binding.fishLabel.updateIconLabelsDark(context, selection.fish)

            binding.lambIcon.setBGColor(context, selection.lamb.iconColor.backgroundColorDark)
            binding.lambIconImage.setIconTintColor(context, selection.lamb.iconColor.tintColorDark)
            binding.lambLabel.updateIconLabelsDark(context, selection.lamb)

        } else {
            binding.manualIcon.setBGColor(context, selection.manual.iconColor.backgroundColorDefault)
            binding.manualIconImage.setIconTintColor(context, selection.manual.iconColor.tintColorDefault)
            binding.manualLabel.updateIconLabelsDefault(context, selection.manual)

            binding.chickenIcon.setBGColor(context, selection.poultry.iconColor.backgroundColorDefault)
            binding.chickenIconImage.setIconTintColor(context, selection.poultry.iconColor.tintColorDefault)
            binding.chickenLabel.updateIconLabelsDefault(context, selection.poultry)

            binding.beefIcon.setBGColor(context, selection.beef.iconColor.backgroundColorDefault)
            binding.beefIconImage.setIconTintColor(context, selection.beef.iconColor.tintColorDefault)
            binding.beefLabel.updateIconLabelsDefault(context, selection.beef)

            binding.porkIcon.setBGColor(context, selection.pork.iconColor.backgroundColorDefault)
            binding.porkIconImage.setIconTintColor(context, selection.pork.iconColor.tintColorDefault)
            binding.porkLabel.updateIconLabelsDefault(context, selection.pork)

            binding.fishIcon.setBGColor(context, selection.fish.iconColor.backgroundColorDefault)
            binding.fishIconImage.setIconTintColor(context, selection.fish.iconColor.tintColorDefault)
            binding.fishLabel.updateIconLabelsDefault(context, selection.fish)

            binding.lambIcon.setBGColor(context, selection.lamb.iconColor.backgroundColorDefault)
            binding.lambIconImage.setIconTintColor(context, selection.lamb.iconColor.tintColorDefault)
            binding.lambLabel.updateIconLabelsDefault(context, selection.lamb)
        }
    }

    fun calculateSpacingForHorizontalModeSelector(density: Float, xPix: Int, isRegionUS: Boolean) {
        val numberOfButtonsTotal = if(isRegionUS) NUM_OF_PROTEINS_US else NUM_OF_PROTEINS_EU //keep this current
        val xdpi = xPix / density

        var spacerSize = 0

        //this set of loops finds the most buttons that will fit on the screen
        //with the least amount of spacing between them
        for (i in 1 until numberOfButtonsTotal + 1) {
            var scrollViewExtraDP = (xdpi - 19).toInt()
            scrollViewExtraDP -= (74 * i)

            if (scrollViewExtraDP > 74) {
                continue
            } else {
                var foundMinimumSpacerSize = false
                var workingSpacingAmount = 0
                for (j in 1 until 30) {
                    val newSpacingAmount = (scrollViewExtraDP - (j * (i - 1)))
                    if ((-37 <= newSpacingAmount) && (newSpacingAmount <= -19)) {
                        foundMinimumSpacerSize = true
                        workingSpacingAmount = j
                        break
                    }
                }
                if (foundMinimumSpacerSize) {
                    spacerSize = workingSpacingAmount
                }
            }
        }

        setSpacingWidth(spacerSize * density, isRegionUS)
    }

    private fun setSpacingWidth(size: Float, isRegionUS: Boolean) {
        binding.spacerOne.layoutParams.width = size.toInt()
        binding.spacerTwo.layoutParams.width = size.toInt()
        binding.spacerThree.layoutParams.width = size.toInt()
        binding.spacerFour.layoutParams.width = size.toInt()
        if(isRegionUS.not()) {
            binding.spacerFive.layoutParams.width = size.toInt()
        }
    }

    companion object {
        const val NUM_OF_PROTEINS_US = 5
        const val NUM_OF_PROTEINS_EU = 6
    }
}