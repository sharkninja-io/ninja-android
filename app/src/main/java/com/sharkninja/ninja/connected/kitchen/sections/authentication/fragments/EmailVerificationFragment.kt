package com.sharkninja.ninja.connected.kitchen.sections.authentication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentEmailVerificationBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.authentication.activities.AuthenticationDeepLinkHelper
import com.sharkninja.ninja.connected.kitchen.sections.authentication.activities.AuthenticationDeepLinkHelper.Companion.SP_KEY_PW
import com.sharkninja.ninja.connected.kitchen.sections.authentication.activities.AuthenticationDeepLinkHelper.Companion.SP_KEY_USER
import com.sharkninja.ninja.connected.kitchen.sections.authentication.helper.SecuredSharedPref
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModel
import com.sharkninja.ninja.connected.kitchen.ui.animators.OpacityAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class EmailVerificationFragment : Fragment() {

    private lateinit var binding: FragmentEmailVerificationBinding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(
            SignInFragment::class.java.simpleName,
            "Confirm Email Account token: ${
                AuthenticationDeepLinkHelper.getConfirmAccountEmailToken(requireActivity().intent)
            }"
        )
        val confirmToken = AuthenticationDeepLinkHelper.getConfirmAccountEmailToken(requireActivity().intent)
        confirmToken?.let {
            authenticationViewModel.setVerifyAccountToken(it)
            authenticationViewModel.verifyUserAccount()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)
        subscribeToVm()
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.verifyEmailSub.text = getString(R.string.auth_verify_email_sub_text, authenticationViewModel.authUser.value.username)
        binding.editEmailButton.text = getString(R.string.edit_email).uppercase()

        binding.editEmailButton.setOnClickListener {
            findNavController().navigate(EmailVerificationFragmentDirections.actionEmailVerificationFragmentToCreateAccountFragment())
        }

        binding.resendButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                authenticationViewModel.sendConfirmation().flowOn(Dispatchers.IO).collect {
                    OpacityAnimator.fadeInOut(binding.verifyResendMessage, 3000)
                }
            }
        }

        binding.continueButton.setOnClickListener {
            // login user

            SecuredSharedPref.create(requireContext())?.apply {
                val userCredEmail = getSecureString(SP_KEY_USER)
                val userCredPW = getSecureString(SP_KEY_PW)
                if (userCredEmail.isNotEmpty() && userCredPW.isNotEmpty()) {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        authenticationViewModel.loginAfterCreateAccount(userCredEmail, userCredPW)
                            .apply {
                                removeSecureKey(SP_KEY_USER)
                                removeSecureKey(SP_KEY_PW)
                            }
                            .onSuccess {
                                authenticationViewModel.checkEcommerceAccountCreation()
                            }
                            .onFailure {error ->
                                if (error.message?.contains("You have to confirm") == true) {
                                    Toast.makeText(requireContext(), "Must confirm account", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(requireContext(), "Wrong email or password", Toast.LENGTH_LONG).show()
                                    findNavController().safeNavigate(EmailVerificationFragmentDirections.actionEmailVerificationFragmentToSignInFragment())
                                }
                            }
                    }
                }
            }
        }
    }

    private fun subscribeToVm() {
        authenticationViewModel.isAuthProgressVisible.onEach {
            if(it) {
                binding.emailVerificationContainer.isVisible = false
            }
            binding.loadingContainer.isVisible = it
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        authenticationViewModel.isAccountVerified.filterNotNull().onEach {
            with(binding){
                emailVerificationContainer.isVisible = it.not()
                successContainer.isVisible = it
                if(it.not()) {
                    OpacityAnimator.fadeInOut(confirmAccountLabel, 3000)
                }
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        authenticationViewModel.isLoggedIn.onEach {isLoggedIn ->
            if(isLoggedIn) {
                authenticationViewModel.setBugSnagUser()
                findNavController().safeNavigate(EmailVerificationFragmentDirections.actionEmailVerificationFragmentToHomeActivity())
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }
}