package com.sharkninja.ninja.connected.kitchen.ui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.ListItemCookingTipCardBinding
import com.sharkninja.ninja.connected.kitchen.ext.setIconTintColor

class CookingTipItemView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private var binding: ListItemCookingTipCardBinding
    private var title: String? = null
    private var description: String? = null
    private var topRightIcon: Drawable? = null
    private var link: String? = null
    private var isThermometerToolTip: Boolean = false
    private var descriptionImage: Drawable? = null
    private var textAndIconColor: Int = R.color.black1

    private var isClosed = true

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ListItemCookingTipCardBinding.inflate(inflater, this, true)

        attrs?.let { setUpAttrs(it, context) }
        setUpViewLayout()
    }

    private fun setUpAttrs(attrs: AttributeSet, context: Context) {
        context.obtainStyledAttributes(attrs, R.styleable.CookingTipItemView).apply {

            getDrawable(R.styleable.CookingTipItemView_cookTipItemTopRightIcon)?.also {
                topRightIcon = it
            }
            getString(R.styleable.CookingTipItemView_cookTipItemTitle).also {
                title = it
            }
            getString(R.styleable.CookingTipItemView_cookTipItemDescription).also {
                description = it
            }
            getString(R.styleable.CookingTipItemView_cookTipItemLink).also {
                link = it
            }

            getBoolean(R.styleable.CookingTipItemView_isThermometerToolTip, false).also {
                isThermometerToolTip = it
            }

            getDrawable(R.styleable.CookingTipItemView_descriptionImage).also {
                descriptionImage = it
            }

            getBoolean(R.styleable.CookingTipItemView_isDarkTheme, false).also {
               textAndIconColor = if(it) R.color.mc_text_and_icon_color else R.color.black1
            }
        }.recycle()
    }

    private fun setUpViewLayout() {
        setTextAndIconColors()
        if(isThermometerToolTip) {
            binding.itemDescription.visibility = View.GONE
            binding.descriptionImage.visibility = View.GONE
            binding.itemLink.visibility = View.GONE
            descriptionImage?.let { binding.descriptionImage.setImageDrawable(it) }
        } else {
            binding.descriptionImage.visibility = View.GONE
            binding.itemDescription.visibility = View.VISIBLE
        }
            topRightIcon?.let { binding.itemTopRightIcon.setImageDrawable(it) }
            title?.let { binding.itemTitle.text = it }
            description?.let { binding.itemDescription.text = it }
            link?.let { binding.itemLink.text = it }
    }

    fun setCloseButtonOnClick(block: () -> Unit) {
        binding.itemTopRightIcon.setOnClickListener { block() }
    }

    fun setArrowOnClick() {
        binding.itemTopRightIcon.setOnClickListener {
            if(isClosed) {
                binding.itemDescription.visibility = View.VISIBLE
                binding.descriptionImage.visibility = View.VISIBLE
                binding.itemTopRightIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_iconoir_nav_arrow_down))
            } else {
                binding.itemDescription.visibility = View.GONE
                binding.descriptionImage.visibility = View.GONE
                binding.itemTopRightIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_left))
            }
            isClosed = !isClosed
        }
    }

    private fun setTextAndIconColors() {
        binding.itemTitle.setTextColor(ContextCompat.getColor(context, textAndIconColor))
        binding.itemDescription.setTextColor(ContextCompat.getColor(context, textAndIconColor))
        binding.itemLink.setTextColor(ContextCompat.getColor(context, textAndIconColor))
        binding.itemTopRightIcon.setIconTintColor(context, textAndIconColor)
    }

    fun setLinkOnClick(block: () -> Unit) {
        binding.itemLink.setOnClickListener { block() }
    }
}