package com.sharkninja.ninja.connected.kitchen.sections.authentication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentForgotPasswordBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModel
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.EmailEditText
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.EmailEditText.EmailState.*
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.EmailEditTextInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class ForgotPasswordFragment : Fragment(), EmailEditTextInterface {

    private lateinit var binding: FragmentForgotPasswordBinding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.forgotEmailEditText.apply {
            editText.setText(authenticationViewModel.authUser.value.username)
            placeholder = resources.getString(R.string.auth_username_hint)
            delegate = this@ForgotPasswordFragment
        }

        binding.signUpHereText.setOnClickListener {
            findNavController().safeNavigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToCreateAccountFragment())
        }

        binding.resetPasswordButton.setOnClickListener {
            authenticationViewModel.setUser(binding.forgotEmailEditText.editText.text.toString())

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                authenticationViewModel.requestPasswordReset().flowOn(Dispatchers.IO).collect {
                    when (it) {
                        is Unit -> {
                            findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToConfirmCodeFragment())
                        }
                        is Throwable -> Log.e("ForgotPassword", it.message, it)
                    }
                }
            }
        }
    }

    override fun emailEditTextDidChangedHandler(chars: CharSequence) { }

    override fun emailStateDidChangeHandler(state: EmailEditText.EmailState) {
        binding.resetPasswordButton.isEnabled = state == VALID || state == AMBIGUOUS || state == DEVELOPMENT
    }
}