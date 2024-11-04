package com.sharkninja.ninja.connected.kitchen.ui.components.fields

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.sharkninja.ninja.connected.kitchen.R

import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

class CriteriaBox(
    context: Context,
    attributeSet: AttributeSet
): ConstraintLayout(context, attributeSet) {

    enum class BoxState {
        UNKNOWN, SUCCESS, ERROR
    }

    lateinit var textView: TextView
    lateinit var imageView: ImageView

    var boxState: BoxState by propertyObserver(BoxState.UNKNOWN, didSet = {
        when (boxState) {
            BoxState.UNKNOWN -> imageView.setImageResource(R.drawable.ic_icon_password_validation_ico_empty)
            BoxState.ERROR -> imageView.setImageResource(R.drawable.ic_icon_password_validation_ico_error)
            BoxState.SUCCESS -> imageView.setImageResource(R.drawable.ic_icon_password_validation_ico_check)
        }
    })

    public var criteria: String by propertyObserver("", didSet = {
        textView.text = criteria
    })

    init {
        this.initSetup()
    }

    private fun initSetup() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.box_criterial_layout, this, true)

        imageView = view.findViewById(R.id.imageAsset)
        textView = view.findViewById(R.id.textView)
    }

    private fun <T> propertyObserver(initialValue: T, willSet: () -> Unit = {}, didSet: () -> Unit = {}) = object : ObservableProperty<T>(initialValue) {
        override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean = true.apply { willSet() }
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) = didSet()
    }
}