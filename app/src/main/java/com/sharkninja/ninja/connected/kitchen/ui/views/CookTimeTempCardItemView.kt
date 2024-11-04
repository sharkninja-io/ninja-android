package com.sharkninja.ninja.connected.kitchen.ui.views

import android.content.Context
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.sharkninja.grillcore.CookMode
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.getCookCardItem
import com.sharkninja.ninja.connected.kitchen.databinding.ListItemCookTimeTempCardBinding
import com.sharkninja.ninja.connected.kitchen.ext.setIconTintColor
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.TextGradients
import kotlin.properties.Delegates

class CookTimeTempCardItemView(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    private var binding: ListItemCookTimeTempCardBinding

    private var valueLeftIcon: Drawable? = null
    private var valueLeftLabel: String? = null
    private var valueLeftValue: String? = null

    private var valueRightIcon: Drawable? = null
    private var valueRight: String? = null
    private var valueRightLabel: String? = null

    private var isProbeCard: Boolean = false

    private var iconRight: Drawable? = null
    private var isTimedCook: Boolean = false
    private var textAndIconColor: Int = R.color.darkest_grey

    data class CookCardViewEnabled(
        val isOnline: Boolean = true,
        val isEnabled: Boolean = true
    )

    var isViewEnabled: CookCardViewEnabled by Delegates.observable(
        CookCardViewEnabled()
    ) { _, _, newValue ->
        setDarkThemeCardEnabled(newValue)
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ListItemCookTimeTempCardBinding.inflate(inflater, this, true)

        attrs?.let { setUpAttrs(it, context) }
        setUpViewLayout()
    }

    private fun setUpAttrs(attrs: AttributeSet, context: Context) {
        context.obtainStyledAttributes(attrs, R.styleable.CookCardTimeTempListItemView).apply {


            getDrawable(R.styleable.CookCardTimeTempListItemView_valueLeftIcon)?.also {
                valueLeftIcon = it
            }
            getString(R.styleable.CookCardTimeTempListItemView_valueLeftLabel)?.also {
                valueLeftLabel = it
            }

            getDrawable(R.styleable.CookCardTimeTempListItemView_valueRightIcon)?.also {
                valueRightIcon = it
            }
            getString(R.styleable.CookCardTimeTempListItemView_valueRightLabel)?.also {
                valueRightLabel = it
            }
            getDrawable(R.styleable.CookCardTimeTempListItemView_timeTempIconRight).also {
                iconRight = it
            }

            getBoolean(R.styleable.CookCardTimeTempListItemView_isProbeCard, false).also {
                isProbeCard = it
            }

            getBoolean(R.styleable.CookCardTimeTempListItemView_isTimedCook, false).also {
                isTimedCook = it
            }
            getBoolean(R.styleable.CookCardTimeTempListItemView_isMonitorAndControl, false).also {
                textAndIconColor = if (it) R.color.mc_text_and_icon_color else R.color.darkest_grey
            }

        }.recycle()
    }

    private fun setUpViewLayout() {
        setTextColors(textAndIconColor)
        setIconColors(textAndIconColor)
        setVerticalDividerColor(textAndIconColor)

        if (isTimedCook) {
            binding.containerRight.visibility = View.VISIBLE
            binding.divider.visibility = View.VISIBLE
        } else {
            binding.containerRight.visibility = View.GONE
            binding.divider.visibility = View.GONE
        }

        valueLeftLabel?.let { binding.valueLeftLabel.text = it }
        valueLeftIcon?.let { binding.valueLeftIcon.setImageDrawable(it) }

        valueRightLabel?.let { binding.valueRightLabel.text = it }
        valueRightIcon?.let { binding.valueRightIcon.setImageDrawable(it) }

    }

    private fun setDarkThemeCardEnabled(cookCardViewEnabled: CookCardViewEnabled) {
        if(cookCardViewEnabled.isOnline.not()) {
            background = ContextCompat.getDrawable(context, R.drawable.offline_disabled_card_background)
            binding.disabledViewCover.visibility = View.VISIBLE
            binding.editTimeTempCardContainer.isEnabled = false
            binding.editTimeTempCardContainer.isClickable = false
            setTextColors(R.color.dashboard_tab_unselected_offline)
            setIconColors(R.color.dashboard_tab_unselected_offline)
        } else if(cookCardViewEnabled.isEnabled.not()) {
            background = ContextCompat.getDrawable(context, R.drawable.mc_probe_card_background)
            binding.disabledViewCover.visibility = View.VISIBLE
            binding.editTimeTempCardContainer.isEnabled = false
            binding.editTimeTempCardContainer.isClickable = false
            setTextColors(R.color.dashboard_tab_unselected_offline)
            setIconColors(R.color.dashboard_tab_unselected_offline)
        } else {
            background = ContextCompat.getDrawable(context, R.drawable.mc_cook_card_background)
            binding.disabledViewCover.visibility = View.GONE
            binding.editTimeTempCardContainer.isEnabled = true
            binding.editTimeTempCardContainer.isClickable = true
            setTextColors(R.color.mc_text_and_icon_color)
            setIconColors(R.color.mc_text_and_icon_color)
        }
    }

    fun showTargetTempProbeCook(shouldShow: Boolean) {
        binding.containerRight.isInvisible = shouldShow.not()
    }

    fun setCardValues(valueLeft: String, valueRight: String?) {
        if(valueRight.isNullOrEmpty()) {
            binding.valueRight.visibility = View.GONE
            binding.unitRight.visibility = View.GONE
            binding.valueRightLabel.visibility = View.GONE
        }
        val leftValue = if(valueLeft == "NotSet") "--" else valueLeft
        val rightValue = valueRight?.let { if(valueRight == "NotSet") "--" else valueRight } ?: ""
        binding.valueLeft.text = leftValue
        binding.valueRight.text = rightValue
    }

    private fun setGradientTextCooling() {
        binding.grillStateTextView.text = resources.getString(R.string.grill_cooling)
        val paint = binding.grillStateTextView.paint
        val width = paint.measureText(binding.grillStateTextView.text.toString())
        val textSize = binding.grillStateTextView.textSize
        val textShader: Shader = TextGradients().getRestGradient(width, textSize)
        binding.grillStateTextView.paint.shader = textShader
    }

    private fun setGradientTextHeating() {
        binding.grillStateTextView.text = context.getString(R.string.grill_heating)
        val paint = binding.grillStateTextView.paint
        val width = paint.measureText(context.getString(R.string.grill_heating))
        val textSize = binding.grillStateTextView.textSize
        val textShader: Shader = TextGradients().getCookGradient(width, textSize)
        binding.grillStateTextView.paint.shader = textShader
    }

    fun showGrillState(shouldShow: Boolean) {
        if (shouldShow) {
            setGradientTextCooling()
            binding.grillStateTextView.visibility = View.VISIBLE
        } else {
            binding.grillStateTextView.visibility = View.GONE
        }
    }

    fun setUnitLeft(unit: String) {
        binding.unitLeft.text = unit
    }

    fun setUnitRight(unit: String) {
        binding.unitRight.text = unit
    }

    fun showUnitRight(shouldShow: Boolean) {
        binding.unitRight.isVisible = shouldShow
    }

    fun showUnitLeft(shouldShow: Boolean) {
        binding.unitLeft.isVisible = shouldShow
    }

    fun setOnCardContainerOnClick(block: () -> Unit) {
        binding.editTimeTempCardContainer.setOnClickListener {
            block()
        }
    }

    fun removeClickListener() {
        binding.editTimeTempCardContainer.setOnClickListener(null)
    }

    fun setRightIconAndLabelForCookMode(cookMode: CookMode) {
        val cookCardItem = cookMode.getCookCardItem()
        binding.valueLeft.text = context.getString(cookCardItem.displayName)
        binding.valueLeftIcon.setImageResource(cookCardItem.drawable)
    }

    private fun setTextColors(color: Int) {
        binding.valueLeft.setTextColor(ContextCompat.getColor(context, color))
        binding.valueRight.setTextColor(ContextCompat.getColor(context, color))
        binding.unitLeft.setTextColor(ContextCompat.getColor(context, color))
        binding.unitRight.setTextColor(ContextCompat.getColor(context, color))
        binding.valueLeftLabel.setTextColor(ContextCompat.getColor(context, color))
        binding.valueRightLabel.setTextColor(ContextCompat.getColor(context, color))
        binding.grillStateTextView.setTextColor(ContextCompat.getColor(context, color))
    }

    private fun setVerticalDividerColor(color: Int) {
        binding.divider.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    private fun setIconColors(color: Int) {
        if(isProbeCard.not()) {
            binding.valueLeftIcon.setIconTintColor(context, color)
        }
        binding.valueRightIcon.setIconTintColor(context, color)
        binding.icArrowRight.setIconTintColor(context, color)
    }
}