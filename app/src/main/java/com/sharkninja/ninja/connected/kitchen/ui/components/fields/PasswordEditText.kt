package com.sharkninja.ninja.connected.kitchen.ui.components.fields

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.sharkninja.ninja.connected.kitchen.R

interface PasswordEditTextInterface {
    fun passwordEditTextDidChangedHandler(chars: CharSequence)
    fun passwordStateDidChangeHandler(state: PasswordEditText.PasswordState)
}

open class PasswordEditText(
    context: Context,
    attributeSet: AttributeSet
) : PlainEditText(context, attributeSet) {

    enum class PasswordState {
        EMPTY, INVALID, VALID
    }

    lateinit var imageAssetButton: ImageView

    private lateinit var uppercaseCriteriaBox: CriteriaBox
    private lateinit var lowercaseCriteriaBox: CriteriaBox
    private lateinit var limitCriteriaBox: CriteriaBox
    private lateinit var numberCriteriaBox: CriteriaBox

    private lateinit var textWatcher: TextWatcher

    var delegate: PasswordEditTextInterface? = null
    var isValidationEnabled = true

    internal open var passwordState: PasswordState by propertyObserver(
        PasswordState.EMPTY,
        didSet = {
            passwordStateHandler(); delegate?.passwordStateDidChangeHandler(passwordState)
        })

    var styled: Boolean = false

    var showCriteriaBoxes: Boolean by propertyObserver(false, didSet = {
        if (showCriteriaBoxes) {
            showCriteriaBoxes()
        } else {
            hideCriteriaBoxes()
        }
    })

    override fun initSetup() {
        super.initSetup()
        this.caption.visibility = GONE
        this.editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        imageAssetButton = view.findViewById(R.id.imageAssetButton)

        uppercaseCriteriaBox = view.findViewById(R.id.uppercaseCriteriaBox)
        uppercaseCriteriaBox.criteria =
            context.getString(R.string.password_req_one_upper_letter)

        lowercaseCriteriaBox = view.findViewById(R.id.lowercaseCriteriaBox)
        lowercaseCriteriaBox.criteria =
            context.getString(R.string.password_req_one_lower_letter)

        limitCriteriaBox = view.findViewById(R.id.limitCriteriaBox)
        limitCriteriaBox.criteria =
            context.getString(R.string.password_req_eight_characters)

        numberCriteriaBox = view.findViewById(R.id.numberCriteriaBox)
        numberCriteriaBox.criteria =
            context.getString(R.string.password_req_one_number)
    }

    override fun enableObservers() {
        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                passwordEditTextHandler(s)
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        }
        editText.addTextChangedListener(textWatcher)
    }

    private fun passwordEditTextHandler(chars: Editable) {
        delegate?.passwordEditTextDidChangedHandler(chars)
        calculatePasswordState(chars)
        super.calculateStateHandler(chars)
    }

    private fun calculatePasswordState(chars: CharSequence) {
        if (chars.isEmpty()) {
            passwordState = PasswordState.EMPTY

            uppercaseCriteriaBox.boxState = CriteriaBox.BoxState.UNKNOWN
            lowercaseCriteriaBox.boxState = CriteriaBox.BoxState.UNKNOWN
            limitCriteriaBox.boxState = CriteriaBox.BoxState.UNKNOWN
            numberCriteriaBox.boxState = CriteriaBox.BoxState.UNKNOWN
            return
        }

        if (isValidationEnabled) {
            val passwordRepresentable = PasswordRepresentable(chars.toString())

            uppercaseCriteriaBox.boxState =
                if (passwordRepresentable.containsUppercase) CriteriaBox.BoxState.SUCCESS else CriteriaBox.BoxState.ERROR
            lowercaseCriteriaBox.boxState =
                if (passwordRepresentable.containsLowercase) CriteriaBox.BoxState.SUCCESS else CriteriaBox.BoxState.ERROR
            limitCriteriaBox.boxState =
                if (passwordRepresentable.isOverExpectedCriteria) CriteriaBox.BoxState.SUCCESS else CriteriaBox.BoxState.ERROR
            numberCriteriaBox.boxState =
                if (passwordRepresentable.containsNumber) CriteriaBox.BoxState.SUCCESS else CriteriaBox.BoxState.ERROR

            if (passwordRepresentable.containsUppercase
                && passwordRepresentable.containsLowercase
                && passwordRepresentable.isOverExpectedCriteria
                && passwordRepresentable.containsNumber
            ) {
                passwordState = PasswordState.VALID
                return
            }

            passwordState = PasswordState.INVALID
        } else {
            uppercaseCriteriaBox.boxState = CriteriaBox.BoxState.SUCCESS
            lowercaseCriteriaBox.boxState = CriteriaBox.BoxState.SUCCESS
            limitCriteriaBox.boxState = CriteriaBox.BoxState.SUCCESS
            numberCriteriaBox.boxState = CriteriaBox.BoxState.SUCCESS
            passwordState = PasswordState.VALID
        }
    }

    private fun showCriteriaBoxes() {
        uppercaseCriteriaBox.visibility = VISIBLE
        lowercaseCriteriaBox.visibility = VISIBLE
        limitCriteriaBox.visibility = VISIBLE
        numberCriteriaBox.visibility = VISIBLE
    }

    private fun hideCriteriaBoxes() {
        uppercaseCriteriaBox.visibility = GONE
        lowercaseCriteriaBox.visibility = GONE
        limitCriteriaBox.visibility = GONE
        numberCriteriaBox.visibility = GONE
    }

    private fun passwordStateHandler() {
        when (passwordState) {
            PasswordState.INVALID -> {
                this.editTextState = EditTextState.ERROR
                this.editText.backgroundTintList =
                    ContextCompat.getColorStateList(context, R.color.error_red)
            }
            PasswordState.VALID, PasswordState.EMPTY -> {
                this.editTextState = if (styled) EditTextState.STYLED else EditTextState.DEFAULT
                this.editText.backgroundTintList =
                    if (styled) ContextCompat.getColorStateList(context, R.color.ninja_green)
                    else ContextCompat.getColorStateList(context, R.color.edit_text_background_tint)
            }
        }
    }

    fun disablePasswordState() {
        passwordState = PasswordState.VALID
        editText.removeTextChangedListener(textWatcher)
    }

    fun enableToggleButton() {
        editText.transformationMethod = PasswordTransformationMethod()
        imageAssetButton.visibility = View.VISIBLE
        imageAssetButton.setOnClickListener {
            togglePasswordDisplay(editText)
        }
        togglePasswordDisplay(editText)
    }

    private fun togglePasswordDisplay(editText: AppCompatEditText) {
        val start = editText.selectionStart
        val end = editText.selectionEnd
        when (editText.transformationMethod) {
            is SingleLineTransformationMethod -> {
                editText.transformationMethod = PasswordTransformationMethod()
                imageAssetButton.setImageResource(R.drawable.ic_icon_ico_eye_closed)
            }
            else -> {
                editText.transformationMethod = SingleLineTransformationMethod()
                imageAssetButton.setImageResource(R.drawable.ic_icon_ico_eye)
            }
        }
        editText.setSelection(start, end)
    }
}