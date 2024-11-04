package com.sharkninja.ninja.connected.kitchen.ui.components.fields

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.appcompat.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.sharkninja.ninja.connected.kitchen.R
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

interface PlainEditTextInterface {
    fun editTextDidChangedHandler(chars: CharSequence)
}

open class PlainEditText(
    context: Context,
    attributeSet: AttributeSet
): ConstraintLayout(context, attributeSet) {

    enum class EditTextState {
        DEFAULT, STYLED, ERROR
    }

    var parentDelegate: PlainEditTextInterface? = null

    internal lateinit var view: View
    internal lateinit var editText: AppCompatEditText
    internal lateinit var caption: TextView

    internal var editTextState: EditTextState by propertyObserver(EditTextState.DEFAULT, didSet = {
        when (editTextState) {
            EditTextState.DEFAULT -> {
                caption.setTextColor(ContextCompat.getColor(context, R.color.theme_reverse))
            }
            EditTextState.STYLED -> {
                caption.setTextColor(ContextCompat.getColor(context, R.color.ninja_green))
            }
            EditTextState.ERROR -> {
                caption.setTextColor(ContextCompat.getColor(context, R.color.error_red))
            }
        }
    })

    internal open var input: String by propertyObserver("", didSet = {
        editText.setText(input)
    })

    internal open var placeholder: String by propertyObserver("", didSet = {
        editText.hint = placeholder
    })

    internal open var message: String by propertyObserver("", didSet = {
        caption.text = message
    })

    init {
        this.initSetup()
        this.enableObservers()
    }

    internal open fun initSetup() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.edit_text_plain_layout, this, true)
        editText = view.findViewById(R.id.editText)
        editText.setTextColor(ContextCompat.getColor(context, R.color.theme_reverse))
        editText.setHintTextColor(ContextCompat.getColor(context, R.color.edit_text_hint_and_text_color))
        editText.imeOptions = EditorInfo.IME_ACTION_NEXT
        editText.textSize = 16F
        editText.typeface = ResourcesCompat.getFont(context, R.font.gotham_book_normal)
        caption = view.findViewById(R.id.caption)
        caption.alpha = 0.0F
    }

    internal open fun enableObservers() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                calculateStateHandler(s)
                parentDelegate?.editTextDidChangedHandler(s)
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {}
        })
    }

    internal fun calculateStateHandler(chars: Editable) {
        caption.alpha = if (chars.isEmpty()) 0.0f else 1.0f
    }

    internal fun <T> propertyObserver(initialValue: T, willSet: () -> Unit = {}, didSet: () -> Unit = {}) = object : ObservableProperty<T>(initialValue) {
        override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean = true.apply { willSet() }
        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) = didSet()
    }
}