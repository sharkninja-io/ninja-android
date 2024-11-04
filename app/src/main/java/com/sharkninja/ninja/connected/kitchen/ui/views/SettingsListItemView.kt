package com.sharkninja.ninja.connected.kitchen.ui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.ListItemSettingsCardBinding

class SettingsListItemView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private var binding: ListItemSettingsCardBinding
    private var title: String? = null
    private var description: String? = null
    private var leftIcon: Drawable? = null
    private var rightIcon: Drawable? = null
    private var toggleIcon: Boolean = false
    private var topDivider: Boolean = false
    private var bottomDivider: Boolean = false
    private var detailName: String? = null
    private var isDetail: Boolean = false

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ListItemSettingsCardBinding.inflate(inflater, this, true)

        attrs?.let { setUpAttrs(it, context) }
        setUpViewLayout()
    }

    private fun setUpAttrs(attrs: AttributeSet, context: Context) {
        context.obtainStyledAttributes(attrs, R.styleable.SettingsListItemView).apply {

            getBoolean(R.styleable.SettingsListItemView_listitem_detail, false).also {
                isDetail = it
            }
            getString(R.styleable.SettingsListItemView_listItemTitle).also {
                title = it
            }

            getString(R.styleable.SettingsListItemView_listItemDescription).also {
                description = it
            }
            getDrawable(R.styleable.SettingsListItemView_listItemLeftIcon)?.also {
                leftIcon = it
            }
            getDrawable(R.styleable.SettingsListItemView_listItemRightIcon)?.also {
                rightIcon = it
            }
            getBoolean(R.styleable.SettingsListItemView_listItemToggleView, false).also {
                toggleIcon = it
            }
            getBoolean(R.styleable.SettingsListItemView_listItemTopDivider, false).also {
                topDivider = it
            }
            getBoolean(R.styleable.SettingsListItemView_listItemBottomDivider, false).also {
                bottomDivider = it
            }
            getString(R.styleable.SettingsListItemView_listitem_detail_name).also {
                detailName = it
            }
        }.recycle()
    }

    public fun setOnClick(block: () -> Unit) {
        binding.listitemSettingsButtonContainer.setOnClickListener { block() }
    }

    private fun setUpViewLayout() {
        if (isDetail.not()) {
            title?.let { binding.listitemSettingsName.text = it }
            description?.let {
                binding.listitemSettingsDescription.text = it
                binding.listitemSettingsDescription.visibility = View.VISIBLE
            }
            leftIcon?.let { binding.listitemSettingsLeftIcon.setImageDrawable(it) }
            rightIcon?.let { binding.listitemSettingsRightIcon.setImageDrawable(it) }
            if (toggleIcon) binding.listitemSettingsToggleIcon.visibility = View.VISIBLE
        } else {
            binding.listitemSettingsButtonContainer.visibility = View.GONE
            binding.listItemApplianceDetail.visibility = View.VISIBLE
            detailName?.let { binding.listitemDetailName.text = it }
        }
        if (topDivider) binding.listitemSettingsTopDivider.visibility = View.VISIBLE
        if (bottomDivider) binding.listitemSettingsBottomDivider.visibility = View.VISIBLE
    }

    fun setChecked(isChecked: Boolean) {
        binding.listitemSettingsToggleIcon.isChecked = isChecked
    }

    fun setDetailValue(value: String) {
        binding.listitemDetailValue.text = value
    }

    fun getCheckedStatus(): Boolean {
        return binding.listitemSettingsToggleIcon.isChecked
    }

    fun setOnCheckedChangeListener(listener: (Boolean) -> Unit) {
        binding.listitemSettingsToggleIcon.setOnClickListener {
            listener(binding.listitemSettingsToggleIcon.isChecked)
        }
    }
}