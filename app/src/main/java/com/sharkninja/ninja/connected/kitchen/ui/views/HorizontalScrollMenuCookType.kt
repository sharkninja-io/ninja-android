package com.sharkninja.ninja.connected.kitchen.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.sharkninja.grillcore.CookMode
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.IconButtonState
import com.sharkninja.ninja.connected.kitchen.data.enums.getMenuItem
import com.sharkninja.ninja.connected.kitchen.databinding.MenuCookSettingsBinding
import com.sharkninja.ninja.connected.kitchen.ext.setBGColor
import com.sharkninja.ninja.connected.kitchen.ext.setIconTintColor
import com.sharkninja.ninja.connected.kitchen.ext.updateIconLabelFontAndColor
import com.sharkninja.ninja.connected.kitchen.ext.updateIconLabelsDark
import com.sharkninja.ninja.connected.kitchen.ext.updateIconLabelsDefault
import kotlin.properties.Delegates

class HorizontalScrollMenuCookType (context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    private var binding: MenuCookSettingsBinding
    private var isDarkTheme: Boolean = false

    private var menuBackgroundColor: Int = R.color.cook_fragment_background
    private var labelColor: Int = R.color.black1

    var cookMode: CookMode by Delegates.observable(
        CookMode.Grill
    ) { _, _, newValue ->
        updateMenu(newValue)
    }

    var cookModesUnavailableInThermometerState: IconButtonState by Delegates.observable(
        IconButtonState.LightEnabled
    ) { _, _, newValue ->
        updateDehydrateAndBroilButtonState(newValue)
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = MenuCookSettingsBinding.inflate(inflater, this, true)

        attrs?.let { setUpAttrs(it, context) }
        setUpViewLayout()
    }

    private fun setUpAttrs(attrs: AttributeSet, context: Context) {
        context.obtainStyledAttributes(attrs, R.styleable.CookModeMenuView).apply {

            getBoolean(R.styleable.CookModeMenuView_isDarkThemeMenu, false).also {
                isDarkTheme = it
                menuBackgroundColor = if(it) R.color.deep_black else R.color.cook_fragment_background
                labelColor = if(it) R.color.mc_text_and_icon_color else R.color.black1
            }

        }.recycle()
    }

    private fun setUpViewLayout() {
        setBackgroundColor(ContextCompat.getColor(context, menuBackgroundColor))
        binding.cookModeLabel.setTextColor(ContextCompat.getColor(context, labelColor))
    }

    fun setCookModeMenuOptions(isUSGrill: Boolean) {
        binding.reheatButtonContainer.isVisible = isUSGrill.not()
        binding.broilButtonContainer.isVisible = isUSGrill
    }

    private fun updateDehydrateAndBroilButtonState(buttonState: IconButtonState) {
        binding.dehydrateIcon.isClickable = buttonState.isEnabled
        binding.dehydrateIcon.isEnabled = buttonState.isEnabled

        binding.broilIcon.isClickable = buttonState.isEnabled
        binding.broilIcon.isEnabled = buttonState.isEnabled

        binding.reheatIcon.isClickable = buttonState.isEnabled
        binding.reheatIcon.isEnabled = buttonState.isEnabled

        binding.dehydrateIcon.setBGColor(context, buttonState.backgroundColor)
        binding.dehydrateIconImage.setIconTintColor(context, buttonState.tintColor)
        binding.dehydrateLabel.updateIconLabelFontAndColor(context, buttonState.textColor)

        binding.broilIcon.setBGColor(context, buttonState.backgroundColor)
        binding.broilIconImage.setIconTintColor(context, buttonState.tintColor)
        binding.broilLabel.updateIconLabelFontAndColor(context, buttonState.textColor)

        binding.reheatIcon.setBGColor(context, buttonState.backgroundColor)
        binding.reheatIconImage.setIconTintColor(context, buttonState.tintColor)
        binding.reheatLabel.updateIconLabelFontAndColor(context, buttonState.textColor)
    }

    fun setMenuItemOnClicksListeners(block: (CookMode) -> Unit) {
        binding.grillIcon.setOnClickListener {
            block(CookMode.Grill)
        }
        binding.smokeIcon.setOnClickListener {
            block(CookMode.Smoke)
        }
        binding.aircrispIcon.setOnClickListener {
            block(CookMode.AirCrisp)
        }
        binding.roastIcon.setOnClickListener {
            block(CookMode.Roast)
        }
        binding.bakeIcon.setOnClickListener {
            block(CookMode.Bake)
        }
        binding.broilIcon.setOnClickListener {
            block(CookMode.Broil)
        }
        binding.dehydrateIcon.setOnClickListener {
            block(CookMode.Dehydrate)
        }

        binding.reheatIcon.setOnClickListener {
            block(CookMode.Reheat)
        }
    }


    private fun updateMenu(cookMode: CookMode) {
        val menuItem = cookMode.getMenuItem()
        if(isDarkTheme) {
            binding.grillIcon.setBGColor(context, menuItem.grill.iconColor.backgroundColorDark)
            binding.grillIconImage.setIconTintColor(context, menuItem.grill.iconColor.tintColorDark)
            binding.grillLabel.updateIconLabelsDark(context, menuItem.grill)

            binding.roastIcon.setBGColor(context, menuItem.roast.iconColor.backgroundColorDark)
            binding.roastIconImage.setIconTintColor(context, menuItem.roast.iconColor.tintColorDark)
            binding.roastLabel.updateIconLabelsDark(context, menuItem.roast)

            binding.bakeIcon.setBGColor(context, menuItem.bake.iconColor.backgroundColorDark)
            binding.bakeIconImage.setIconTintColor(context, menuItem.bake.iconColor.tintColorDark)
            binding.bakeLabel.updateIconLabelsDark(context, menuItem.bake)

            binding.smokeIcon.setBGColor(context, menuItem.smoke.iconColor.backgroundColorDark)
            binding.smokeIconImage.setIconTintColor(context, menuItem.smoke.iconColor.tintColorDark)
            binding.smokeLabel.updateIconLabelsDark(context, menuItem.smoke)

            binding.aircrispIcon.setBGColor(context, menuItem.aircrisp.iconColor.backgroundColorDark)
            binding.aircrispIconImage.setIconTintColor(context, menuItem.aircrisp.iconColor.tintColorDark)
            binding.aircrispLabel.updateIconLabelsDark(context, menuItem.aircrisp)


            if(cookModesUnavailableInThermometerState != IconButtonState.DarkDisabled) {

                binding.broilIcon.setBGColor(context, menuItem.broil.iconColor.backgroundColorDark)
                binding.broilIconImage.setIconTintColor(context, menuItem.broil.iconColor.tintColorDark)
                binding.broilLabel.updateIconLabelsDark(context, menuItem.broil)

                binding.dehydrateIcon.setBGColor(context, menuItem.dehydrate.iconColor.backgroundColorDark)
                binding.dehydrateIconImage.setIconTintColor(context, menuItem.dehydrate.iconColor.tintColorDark)
                binding.dehydrateLabel.updateIconLabelsDark(context, menuItem.dehydrate)

                binding.reheatIcon.setBGColor(context, menuItem.reheat.iconColor.backgroundColorDark)
                binding.reheatIconImage.setIconTintColor(context, menuItem.reheat.iconColor.tintColorDark)
                binding.reheatLabel.updateIconLabelsDark(context, menuItem.reheat)
            }

        } else {
            binding.grillIcon.setBGColor(context, menuItem.grill.iconColor.backgroundColorDefault)
            binding.grillIconImage.setIconTintColor(context, menuItem.grill.iconColor.tintColorDefault)
            binding.grillLabel.updateIconLabelsDefault(context, menuItem.grill)

            binding.roastIcon.setBGColor(context, menuItem.roast.iconColor.backgroundColorDefault)
            binding.roastIconImage.setIconTintColor(context, menuItem.roast.iconColor.tintColorDefault)
            binding.roastLabel.updateIconLabelsDefault(context, menuItem.roast)

            binding.bakeIcon.setBGColor(context, menuItem.bake.iconColor.backgroundColorDefault)
            binding.bakeIconImage.setIconTintColor(context, menuItem.bake.iconColor.tintColorDefault)
            binding.bakeLabel.updateIconLabelsDefault(context, menuItem.bake)

            binding.smokeIcon.setBGColor(context, menuItem.smoke.iconColor.backgroundColorDefault)
            binding.smokeIconImage.setIconTintColor(context, menuItem.smoke.iconColor.tintColorDefault)
            binding.smokeLabel.updateIconLabelsDefault(context, menuItem.smoke)

            binding.aircrispIcon.setBGColor(context, menuItem.aircrisp.iconColor.backgroundColorDefault)
            binding.aircrispIconImage.setIconTintColor(context, menuItem.aircrisp.iconColor.tintColorDefault)
            binding.aircrispLabel.updateIconLabelsDefault(context, menuItem.aircrisp)


            if(cookModesUnavailableInThermometerState != IconButtonState.LightDisabled) {
                binding.dehydrateIcon.setBGColor(context, menuItem.dehydrate.iconColor.backgroundColorDefault)
                binding.dehydrateIconImage.setIconTintColor(context, menuItem.dehydrate.iconColor.tintColorDefault)
                binding.dehydrateLabel.updateIconLabelsDefault(context, menuItem.dehydrate)

                binding.broilIcon.setBGColor(context, menuItem.broil.iconColor.backgroundColorDefault)
                binding.broilIconImage.setIconTintColor(context, menuItem.broil.iconColor.tintColorDefault)
                binding.broilLabel.updateIconLabelsDefault(context, menuItem.broil)

                binding.reheatIcon.setBGColor(context, menuItem.reheat.iconColor.backgroundColorDefault)
                binding.reheatIconImage.setIconTintColor(context, menuItem.reheat.iconColor.tintColorDefault)
                binding.reheatLabel.updateIconLabelsDefault(context, menuItem.reheat)
            }
        }
    }

    fun calculateSpacingForHorizontalModeSelector(density: Float, xPix: Int) {
        val numberOfButtonsTotal = 7 //keep this current
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

        setSpacingWidth(spacerSize * density)
    }

    private fun setSpacingWidth(size: Float) {
        binding.spacerOne.layoutParams.width = size.toInt()
        binding.spacerTwo.layoutParams.width = size.toInt()
        binding.spacerThree.layoutParams.width = size.toInt()
        binding.spacerFour.layoutParams.width = size.toInt()
        binding.spacerFive.layoutParams.width = size.toInt()
        binding.spacerSix.layoutParams.width = size.toInt()
    }
}