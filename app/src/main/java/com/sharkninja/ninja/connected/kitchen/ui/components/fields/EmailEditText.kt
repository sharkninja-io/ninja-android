package com.sharkninja.ninja.connected.kitchen.ui.components.fields

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.sharkninja.ninja.connected.kitchen.R

interface EmailEditTextInterface {
    fun emailEditTextDidChangedHandler(chars: CharSequence)
    fun emailStateDidChangeHandler(state: EmailEditText.EmailState)
}

class EmailEditText(
    context: Context,
    attributeSet: AttributeSet
): PlainEditText(context, attributeSet) {

    enum class EmailState {
        EMPTY, AMBIGUOUS, DEVELOPMENT,
        INVALID, VALID
    }

    private var email: EmailAddressRepresentable? = null

    var delegate: EmailEditTextInterface? = null

    override var input: String by propertyObserver("", didSet = {
        calculateEmailState(input)
    })

    var emailState: EmailState by propertyObserver(EmailState.EMPTY, didSet = {
        emailStateHandler(); delegate?.emailStateDidChangeHandler(emailState)
    })

    override fun initSetup() {
        super.initSetup()
    }

    override fun enableObservers() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                emailEditTextHandler(s)
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {}
        })
    }

    private fun emailEditTextHandler(chars: Editable) {
        delegate?.emailEditTextDidChangedHandler(chars)
        calculateEmailState(chars)
        super.calculateStateHandler(chars)
    }

    private fun calculateEmailState(chars: CharSequence) {
        if (chars.isEmpty()) {
            emailState = EmailState.EMPTY
            return
        }

        val emailAddressRepresentable = EmailAddressRepresentable(chars.toString())

        email = emailAddressRepresentable

        emailState = if (emailAddressRepresentable.isEmailValid()) {
            when {
                emailAddressRepresentable.possibleDomains() != null -> {
                    EmailState.AMBIGUOUS
                }
                emailAddressRepresentable.isDevDomain() -> {
                    EmailState.DEVELOPMENT
                }
                else -> {
                    EmailState.VALID
                }
            }
        } else {
            EmailState.INVALID
        }
    }

    private fun emailStateHandler() {
        when (emailState) {
            EmailState.INVALID -> {
                this.editTextState = EditTextState.ERROR
                this.message = "Please enter a valid email address."
                this.editText.backgroundTintList = ContextCompat.getColorStateList(context, R.color.error_red)
            }
            EmailState.AMBIGUOUS -> {
                this.editTextState = EditTextState.STYLED
                this.message = "Did you mean ${email?.possibleDomains()}"
                this.editText.backgroundTintList = ContextCompat.getColorStateList(context, R.color.ninja_green)
            }
            EmailState.DEVELOPMENT -> {
                this.editTextState = EditTextState.STYLED
                this.message = "Ayla Development Server"
                this.editText.backgroundTintList = ContextCompat.getColorStateList(context, R.color.light_grey)
            }
            EmailState.EMPTY, EmailState.VALID -> {
                this.editTextState = EditTextState.DEFAULT
                this.message = ""
                this.editText.backgroundTintList = ContextCompat.getColorStateList(context, R.color.light_grey)
            }
        }
    }
}

fun EmailEditText.EmailState.isValid(): Boolean {
    return this == EmailEditText.EmailState.VALID || this == EmailEditText.EmailState.DEVELOPMENT || this == EmailEditText.EmailState.AMBIGUOUS
}