package com.sharkninja.ninja.connected.kitchen.ui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.setPadding
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardTabs
import com.sharkninja.ninja.connected.kitchen.databinding.LayoutTabSelectionSwitchSegmentControlBinding
import kotlin.properties.Delegates

enum class TabState {
    OfflineDisabled,
    ThermometerNotSupported,
    Online
}


class RoundedSwitchSegmentedControl(context: Context, attrs: AttributeSet?) :
    LinearLayoutCompat(context, attrs) {

    private val binding: LayoutTabSelectionSwitchSegmentControlBinding =
        LayoutTabSelectionSwitchSegmentControlBinding.inflate(LayoutInflater.from(context), null, false)

    init {
        addView(binding.motionLayoutContainer)

        val layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setLayoutParams(layoutParams)
        binding.motionLayoutContainer.layoutParams = layoutParams

        attrs?.let { setUpAttrs(it, context) }
        setUpLayout()
    }


    private var transitionStart = false
    private var switchBackgroundColor = ContextCompat.getColor(context, BACKGROUND_COLOR)

    var activeBgRes: Drawable? = ContextCompat.getDrawable(context, R.drawable.selected_cook_type_tab_pre_cook_background)

    var selectedTextColor = ContextCompat.getColor(context, SELECTED_TEXT_COLOR)
    var rightToggleText: String? = null
    var leftToggleText: String? = null
    var textSize: Float? = null
    var unselectedTextColor = ContextCompat.getColor(context, UNSELECTED_TEXT_COLOR)
    var mCornerRadius: Float = CORNER_RADIUS_DP
    var mStrokeWidth: Int = 0
    private var borderColor = ContextCompat.getColor(context, BORDER_COLOR)
    //selected item background for MC
    var drawableSelected: Int by Delegates.observable(R.drawable.selected_cook_type_tab_preheat_background) { _, _, newValue ->
        val selectedBackground = ContextCompat.getDrawable(context, newValue)
        binding.switchSelected.background = selectedBackground
    }
    //only used for MC
    var tabState: TabState = TabState.OfflineDisabled
        set(value) {
            setBackgroundSwitchColor(value)
            field = value
        }

    private fun setBackgroundSwitchColor(tabState: TabState) {
        when(tabState) {
            TabState.Online -> {
                binding.motionLayoutContainer.alpha = 1f
                binding.switchFirstItem.alpha = 1f
                binding.switchFirstItem.isClickable = true
                binding.switchSecondItem.isClickable = true
            }
            TabState.OfflineDisabled -> {
                binding.motionLayoutContainer.alpha = .5f
                binding.switchFirstItem.isClickable = false
                binding.switchSecondItem.isClickable = false
            }
            TabState.ThermometerNotSupported -> {
                binding.switchFirstItem.alpha = .5f
                binding.switchFirstItem.isClickable = true
                binding.switchSecondItem.isClickable = true
            }
        }
    }


    private fun setUpAttrs(attrs: AttributeSet, context: Context) {
        context.obtainStyledAttributes(attrs, R.styleable.TabSelectionSwitchSegmentedControl)
            .apply {
                getString(R.styleable.TabSelectionSwitchSegmentedControl_textToggleLeft).also {
                    rightToggleText = it
                }

                getString(R.styleable.TabSelectionSwitchSegmentedControl_textToggleRight).also {
                    leftToggleText = it
                }

                getDrawable(
                    R.styleable.TabSelectionSwitchSegmentedControl_selectedTabBackground).also {
                    activeBgRes = it
                }

                getColor(
                    R.styleable.TabSelectionSwitchSegmentedControl_selectedTextColor,
                    ContextCompat.getColor(context, SELECTED_TEXT_COLOR)
                ).also {
                    selectedTextColor = it
                }

                getColor(
                    R.styleable.TabSelectionSwitchSegmentedControl_unselectedTextColor,
                    ContextCompat.getColor(context, UNSELECTED_TEXT_COLOR)
                ).also {
                    unselectedTextColor = it
                }

                getColor(
                    R.styleable.TabSelectionSwitchSegmentedControl_backgroundColor,
                    ContextCompat.getColor(context, BACKGROUND_COLOR)
                ).also {
                    switchBackgroundColor = it
                }

                getDimension(
                    R.styleable.TabSelectionSwitchSegmentedControl_textSize,
                    TEXT_SIZE
                ).also {
                    textSize = it
                }

                getColor(
                    R.styleable.TabSelectionSwitchSegmentedControl_borderColor,
                    ContextCompat.getColor(context, BORDER_COLOR)
                ).also {
                    borderColor = it
                }

                getDimension(
                    R.styleable.TabSelectionSwitchSegmentedControl_strokeWidth,
                    STROKE_WIDTH
                ).toInt().also {
                    mStrokeWidth = it
                }

                getDimension(
                    R.styleable.TabSelectionSwitchSegmentedControl_cornerRadius,
                    CORNER_RADIUS_DP
                ).also {
                    mCornerRadius = it
                }

            }.recycle()
    }

    private fun setUpLayout() {
        // set options
        leftToggleText?.let { binding.switchFirstItem.text = it }
        rightToggleText?.let { binding.switchSecondItem.text = it  }

        textSize?.let {
            binding.switchFirstItem.textSize = it
            binding.switchSecondItem.textSize = it
        }

        binding.switchFirstItem.typeface = ResourcesCompat.getFont(context, R.font.gotham_book_bold)
        binding.switchSecondItem.typeface = ResourcesCompat.getFont(context, R.font.gotham_book_bold)
        // selected item background
        val selectedBackground = activeBgRes
        binding.switchSelected.background = selectedBackground

        //switch background color
        val motionLayoutBackground = GradientDrawable()
        motionLayoutBackground.setColor(switchBackgroundColor)
        motionLayoutBackground.cornerRadius = mCornerRadius
        motionLayoutBackground.setStroke(mStrokeWidth, borderColor)
        binding.motionLayoutContainer.background = motionLayoutBackground
        binding.motionLayoutContainer.setPadding(mStrokeWidth)

        // active text color
        binding.motionLayout.getConstraintSet(R.id.starting_set)?.let { startConstraintSet ->
            startConstraintSet.getConstraint(R.id.switch_first_item).let {
                it.mCustomConstraints["TextColor"]?.setColorValue(selectedTextColor)
            }
        }
        binding.motionLayout.getConstraintSet(R.id.ending_set)?.let { startConstraintSet ->
            startConstraintSet.getConstraint(R.id.switch_second_item).let {
                it.mCustomConstraints["TextColor"]?.setColorValue(selectedTextColor)
            }
        }
        //inactive text color
        binding.motionLayout.getConstraintSet(R.id.starting_set)?.let { startConstraintSet ->
            startConstraintSet.getConstraint(R.id.switch_second_item).let {
                it.mCustomConstraints["TextColor"]?.setColorValue(unselectedTextColor)
            }
        }
        binding.motionLayout.getConstraintSet(R.id.ending_set)?.let { startConstraintSet ->
            startConstraintSet.getConstraint(R.id.switch_first_item).let {
                it.mCustomConstraints["TextColor"]?.setColorValue(unselectedTextColor)
            }
        }
    }


    fun setChecked(tab: CookDashboardTabs) {
        if (tab == CookDashboardTabs.Thermometer) {
            transitionStart = true
            binding.motionLayout.transitionToStart()
        } else {
            transitionStart = false
            binding.motionLayout.transitionToEnd()
        }
    }

    fun getCurrentTab(): CookDashboardTabs {
        return if (transitionStart) CookDashboardTabs.Thermometer else CookDashboardTabs.TimeCook
    }

    fun setOnToggleSwitchChangeListener(block: (CookDashboardTabs) -> Unit) {
        binding.switchFirstItem.setOnClickListener {
            block(CookDashboardTabs.Thermometer)
        }

        binding.switchSecondItem.setOnClickListener {
            block(CookDashboardTabs.TimeCook)
        }
    }

    companion object Default {
        const val SELECTED_TEXT_COLOR = android.R.color.white
        const val UNSELECTED_TEXT_COLOR = R.color.darkest_grey
        const val BORDER_COLOR = R.color.grey3
        const val BACKGROUND_COLOR = android.R.color.transparent
        const val CORNER_RADIUS_DP = 1000f
        const val TEXT_SIZE = 12f
        const val STROKE_WIDTH = 2f
    }
}