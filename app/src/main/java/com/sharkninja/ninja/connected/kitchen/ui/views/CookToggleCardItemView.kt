package com.sharkninja.ninja.connected.kitchen.ui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.sharkninja.grillcore.CalculatedState
import com.sharkninja.grillcore.CookMode
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.ListItemCookToggleCardBinding
import com.sharkninja.ninja.connected.kitchen.ext.setIconTintColor

class CookToggleCardItemView(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {

    private var binding: ListItemCookToggleCardBinding

    private var woodfireIcon: Drawable? = null
    private var woodfireLabel: String? = null
    private var woodfireStatus: String? = null

    private var probeIcon: Drawable? = null
    private var probeName: String? = null
    private var iconRight: Drawable? = null
    private var tempLabel: String? = null

    private var isWoodFireCard: Boolean = false
    private var isMonitorAndControl: Boolean = false
    private var textAndIconColor: Int = R.color.darkest_grey


    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ListItemCookToggleCardBinding.inflate(inflater, this, true)

        attrs?.let { setUpAttrs(it, context) }
        setUpViewLayout()
    }

    private fun setUpAttrs(attrs: AttributeSet, context: Context) {
        context.obtainStyledAttributes(attrs, R.styleable.CookCardToggleListItemView).apply {

            getDrawable(R.styleable.CookCardToggleListItemView_woodfireIcon)?.also { woodfireIcon = it }
//            getDrawable(R.styleable.CookCardToggleListItemView_probeIcon)?.also { probeIcon = it }
            getDrawable(R.styleable.CookCardToggleListItemView_iconRight)?.also { iconRight = it }
            getString(R.styleable.CookCardToggleListItemView_probeName)?.also { probeName = it }
            getString(R.styleable.CookCardToggleListItemView_woodfireLabel)?.also { woodfireLabel = it }
            getBoolean(R.styleable.CookCardToggleListItemView_isWoodFireCardView, false).also { isWoodFireCard = it }
            getString(R.styleable.CookCardToggleListItemView_tempLabel).also { tempLabel = it }
            getBoolean(R.styleable.CookCardToggleListItemView_isMonitorAndControlToggleItem, false)
                .also {
                    textAndIconColor = if (it) R.color.mc_text_and_icon_color else R.color.darkest_grey
                }

        }.recycle()
    }

    private fun setUpViewLayout() {

        setTextColors(textAndIconColor)
        setIconColors(textAndIconColor)
        setHorizontalDividerColor(textAndIconColor)

        if (isWoodFireCard) {
            binding.probeCardView.visibility = View.GONE
            binding.woodFireCardView.visibility = View.VISIBLE
            woodfireLabel?.let { binding.woodfireViewLabel.text = it }
        } else {
            binding.woodFireCardView.visibility = View.GONE
            binding.probeCardView.visibility = View.VISIBLE
            iconRight?.let { binding.iconRight.setImageDrawable(it) }
            probeName?.let { binding.probeViewName.text = it }
            tempLabel?.let { binding.tempLabel.text = it }
        }

    }

    fun setProbePluggedIn(isPluggedIn: Boolean) {
        val probeIcon = if(isPluggedIn) R.drawable.ic_thermometer_probe else R.drawable.ic_thermometer_probe_unpluged
        binding.probeViewIcon.setImageResource(probeIcon)
    }

    fun setProbeOnClickListener(block: () -> Unit) {
        binding.probeToggleOnView.setOnClickListener { block.invoke() }
    }

    fun setProbeValue(probeValue: String) {
        binding.probeValue.text = probeValue
    }

    fun setProbeName(name: String) {
        binding.probeViewName.text = name
    }

    fun setWoodFireToggleOnCheckChangedCook(block: (Boolean) -> Unit) {
        binding.woodFireToggle.setOnClickListener {
            block.invoke(binding.woodFireToggle.isChecked)
        }
    }

    fun setWoodFireToggleOnCheckChangedPreCook(block: (Boolean) -> Unit) {
        binding.woodFireToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            block.invoke(isChecked)
        }
    }

    fun showTempUnit(shouldShow: Boolean) {
        binding.tempUnit.isVisible = shouldShow
    }

    fun removeCheckChangeListener() {
        binding.woodFireToggle.setOnCheckedChangeListener(null)
    }

    fun setWoodFireToggleStateAndStatus(isChecked: Boolean) {
        binding.woodFireToggle.isChecked = isChecked
        binding.woodfireViewStatus.text = if(isChecked) resources.getString(R.string.on) else resources.getString(R.string.off)
    }

    fun setWoodFireStatePreCook(cookMode: CookMode) {
        when(cookMode) {
            CookMode.Broil -> {
                binding.woodFireToggle.isEnabled = false
                binding.woodFireToggle.isClickable = false
                setWoodFireToggleStateAndStatus(false)
                binding.root.background = ContextCompat.getDrawable(context, R.drawable.disabled_card_background)
            }
            CookMode.Smoke -> {
                binding.woodFireToggle.isEnabled = true
                binding.woodFireToggle.isClickable = false
                setWoodFireToggleStateAndStatus(true)
                binding.root.background = ContextCompat.getDrawable(context, R.drawable.precook_cook_card_background)
            }
            else -> {
                binding.woodFireToggle.isEnabled = true
                binding.woodFireToggle.isClickable = true
                binding.root.background = ContextCompat.getDrawable(context, R.drawable.precook_cook_card_background)
            }
        }
    }

    fun setWoodfireStateMonitorAndControl(cookMode: CookMode,
                                          woodFireOn: Boolean,
                                          calculatedState: CalculatedState,
                                          isOnline: Boolean
    ) {
        val isEditable = calculatedState == CalculatedState.Preheating || calculatedState == CalculatedState.Igniting
         if(isOnline) {
             when(cookMode) {
                 CookMode.Smoke -> {
                     setWoodFireToggleStateAndStatus(true)
                     binding.woodFireToggle.isEnabled = true
                     binding.woodFireToggle.isClickable = false
                     background = ContextCompat.getDrawable(context, R.drawable.mc_cook_card_background)

                 }

                 CookMode.Broil -> {
                     setWoodFireToggleStateAndStatus(false)
                     binding.woodFireToggle.isEnabled = false
                     background = ContextCompat.getDrawable(context, R.drawable.mc_disabled_card_background)
                 }

                 else -> {
                     setWoodFireToggleStateAndStatus(woodFireOn)
                     binding.woodFireToggle.isClickable = isEditable
                     binding.woodFireToggle.isEnabled = true
                     background = ContextCompat.getDrawable(context, R.drawable.mc_cook_card_background)
                 }
             }
         } else {
             setWoodFireToggleStateAndStatus(woodFireOn)
             binding.woodFireToggle.isClickable = false
             binding.woodFireToggle.isEnabled = false
             background = ContextCompat.getDrawable(context, R.drawable.offline_disabled_card_background)
         }

        if (binding.woodFireToggle.isEnabled) {
            setTextColors(textAndIconColor)
            setIconColors(textAndIconColor)
        } else {
            setTextColors(R.color.dashboard_tab_unselected_offline)
            setIconColors(R.color.dashboard_tab_unselected_offline)
        }
    }

    fun setTempUnit(unit: String) {
        binding.tempUnit.text = unit
    }

    fun setToggleStateProbe(isChecked: Boolean) {
        binding.probeToggle.isChecked = isChecked
    }

    fun setProbeToggleOnCheckChangedListener(block: (Boolean) -> Unit) {
        binding.probeToggle.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                binding.probeToggleOnView.visibility = View.VISIBLE
            } else {
                binding.probeToggleOnView.visibility = View.GONE
            }

            block.invoke(b)
        }
    }

    private fun setTextColors(color: Int) {
        binding.woodfireViewLabel.setTextColor(ContextCompat.getColor(context, color))
        binding.probeValue.setTextColor(ContextCompat.getColor(context, color))
        binding.tempUnit.setTextColor(ContextCompat.getColor(context, color))
        binding.probeViewName.setTextColor(ContextCompat.getColor(context, color))
        binding.woodfireViewStatus.setTextColor(ContextCompat.getColor(context, color))
    }

    private fun setIconColors(color: Int) {
        binding.iconRight.setIconTintColor(context, color)
//        binding.probeViewIcon.setIconTintColor(context, color)
        binding.woodFireIcon.setIconTintColor(context, color)
    }

    private fun setHorizontalDividerColor(color: Int) {
        binding.dividerProbe.setBackgroundColor(ContextCompat.getColor(context, color))
    }

}