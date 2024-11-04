package com.sharkninja.ninja.connected.kitchen.sections.authentication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentSignInBinding
import com.sharkninja.ninja.connected.kitchen.ext.hideSoftInput
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModel
import com.sharkninja.ninja.connected.kitchen.ui.animators.OpacityAnimator
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.EmailEditText
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.EmailEditTextInterface
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditText
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditTextInterface
import kotlinx.coroutines.flow.*


class SignInFragment : Fragment(), EmailEditTextInterface, PasswordEditTextInterface {

    private lateinit var binding: FragmentSignInBinding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.emailEditText.apply {
            editText.imeOptions = EditorInfo.IME_ACTION_NEXT
            editText.setText(authenticationViewModel.authUser.value.username)
            placeholder = resources.getString(R.string.auth_username_hint)
            delegate = this@SignInFragment
        }

        binding.passwordEditText.apply {
            editText.imeOptions = EditorInfo.IME_ACTION_DONE
            placeholder = resources.getString(R.string.auth_password_hint)
            delegate = this@SignInFragment
            isValidationEnabled = false
            enableToggleButton()
        }

        binding.signInContinue.setOnClickListener {
            it.hideSoftInput()
            val user = binding.emailEditText.editText.text.toString()
            val pass = binding.passwordEditText.editText.text.toString()
            authenticationViewModel.setCredentials(user, pass)

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                authenticationViewModel.login()
                    .onSuccess {
                       authenticationViewModel.checkEcommerceAccountCreation()
                    }
                    .onFailure {
                        if (it.message?.contains("You have to confirm") == true) {
                            OpacityAnimator.fadeInOut(binding.confirmAccountLabel, 3000)
                        } else {
                            OpacityAnimator.fadeInOut(binding.emailPasswordError, 3000)
                        }
                    }
            }
        }

        binding.signUpHereText.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToCreateAccountFragment())
        }

        binding.forgotPasswordText.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToForgotPasswordFragment())
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            authenticationViewModel.isLoggedIn.collectLatest { isLoggedIn ->
                if (isLoggedIn) {
                    authenticationViewModel.setBugSnagUser()
                    findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToHomeActivity())
                }
            }
        }
    }

    override fun emailEditTextDidChangedHandler(chars: CharSequence) { }

    override fun emailStateDidChangeHandler(state: EmailEditText.EmailState) { }

    override fun passwordEditTextDidChangedHandler(chars: CharSequence) { }

    override fun passwordStateDidChangeHandler(state: PasswordEditText.PasswordState) {
        when (state) {
            PasswordEditText.PasswordState.VALID -> binding.signInContinue.isEnabled = true
            else -> binding.signInContinue.isEnabled = false
        }
    }
}