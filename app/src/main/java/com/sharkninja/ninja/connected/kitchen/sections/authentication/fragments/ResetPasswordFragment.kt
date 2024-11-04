package com.sharkninja.ninja.connected.kitchen.sections.authentication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentResetPasswordBinding
import com.sharkninja.ninja.connected.kitchen.ext.hideSoftInput
import com.sharkninja.ninja.connected.kitchen.sections.authentication.activities.AuthenticationDeepLinkHelper
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModel
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditText
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditTextInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class ResetPasswordFragment : Fragment(), PasswordEditTextInterface {

    private lateinit var binding: FragmentResetPasswordBinding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()
    var passwordState = PasswordEditText.PasswordState.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().intent?.let {
            val resetToken = AuthenticationDeepLinkHelper.getPasswordResetToken(it)
            Log.i("Deep Link", "onViewCreated: reset token $it ")
            resetToken?.let { token ->
                authenticationViewModel.setToken(token)
            }
        }
    }

    private fun initBindings() {
        binding.passwordEditText.apply {
            editText.imeOptions = EditorInfo.IME_ACTION_DONE
            placeholder = resources.getString(R.string.auth_password_hint)
            delegate = this@ResetPasswordFragment
            isValidationEnabled = true
            showCriteriaBoxes = true
            enableToggleButton()
        }

        binding.resetPasswordButton.setOnClickListener {
            it.hideSoftInput()
            authenticationViewModel.setPassword(binding.passwordEditText.editText.text.toString())
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                authenticationViewModel.resetPassword().flowOn(Dispatchers.IO).collect {
                    when (it) {
                        is Unit -> {
                            findNavController().navigate(ResetPasswordFragmentDirections.actionResetPasswordFragmentToSignInFragment())
                        }
                    }
                }
            }
        }
    }

    override fun passwordEditTextDidChangedHandler(chars: CharSequence) { }

    override fun passwordStateDidChangeHandler(state: PasswordEditText.PasswordState) {
        passwordState = state
        when (state) {
            PasswordEditText.PasswordState.VALID -> binding.resetPasswordButton.isEnabled = true
            else -> binding.resetPasswordButton.isEnabled = false
        }
    }
}