package com.sharkninja.ninja.connected.kitchen.sections.authentication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_ACTION_NEXT
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentCreateAccountBinding
import com.sharkninja.ninja.connected.kitchen.sections.authentication.activities.AuthenticationDeepLinkHelper.Companion.SP_KEY_PW
import com.sharkninja.ninja.connected.kitchen.sections.authentication.activities.AuthenticationDeepLinkHelper.Companion.SP_KEY_USER
import com.sharkninja.ninja.connected.kitchen.sections.authentication.bottomsheets.PrivacyNoticeBottomSheet
import com.sharkninja.ninja.connected.kitchen.sections.authentication.bottomsheets.TermsAndConditionsBottomSheet
import com.sharkninja.ninja.connected.kitchen.sections.authentication.helper.SecuredSharedPref
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModel
import com.sharkninja.ninja.connected.kitchen.sections.explore.bottomsheets.CookModeSelectorBottomSheet
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.EmailEditText
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.EmailEditTextInterface
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditText
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditTextInterface
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.isValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class CreateAccountFragment : Fragment(), EmailEditTextInterface, PasswordEditTextInterface {

    private lateinit var binding: FragmentCreateAccountBinding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()
    var passwordState = PasswordEditText.PasswordState.EMPTY
    var emailState = EmailEditText.EmailState.EMPTY
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.createEmailEditText.apply {
            editText.setText(authenticationViewModel.authUser.value.username)
            placeholder = resources.getString(R.string.auth_username_hint)
            delegate = this@CreateAccountFragment
        }

        binding.createAccountButton.isEnabled = passwordState === PasswordEditText.PasswordState.VALID &&
                binding.privacyNoticeCheckbox.isChecked &&
                binding.passwordTermsCheckbox.isChecked
    }

    private fun initBindings() {
        binding.textAgreeTerms.setOnClickListener {
            val termsAndConditionsBottomSheet = TermsAndConditionsBottomSheet()
            termsAndConditionsBottomSheet.show(parentFragmentManager, CookModeSelectorBottomSheet.TAG)
        }

        binding.textPrivacyNotice.setOnClickListener {
            val privacyNoticeBottomSheet = PrivacyNoticeBottomSheet()
            privacyNoticeBottomSheet.show(parentFragmentManager, CookModeSelectorBottomSheet.TAG)
        }

        binding.createEmailEditText.apply {
            editText.imeOptions = IME_ACTION_NEXT
            editText.setText(authenticationViewModel.authUser.value.username)
            placeholder = resources.getString(R.string.auth_username_hint)
            delegate = this@CreateAccountFragment
        }

        binding.createPasswordEditText.apply {
            editText.imeOptions = IME_ACTION_DONE
            placeholder = resources.getString(R.string.auth_password_create_hint)
            delegate = this@CreateAccountFragment
            isValidationEnabled = true
            showCriteriaBoxes = true
            enableToggleButton()
        }

        binding.passwordTermsCheckbox.apply {
            setOnClickListener {
                checkRegisterButtonShouldBeEnabled()
            }
        }

        binding.privacyNoticeCheckbox.apply {
            setOnClickListener {
                checkRegisterButtonShouldBeEnabled()
            }
        }

        binding.createAccountButton.setOnClickListener {
            authenticationViewModel.setUser(binding.createEmailEditText.editText.text.toString())
            authenticationViewModel.setPassword(binding.createPasswordEditText.editText.text.toString())

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                authenticationViewModel.createAccount().flowOn(Dispatchers.IO).collect {
                    when (it) {
                        is Throwable -> Toast.makeText(context, "An account is already associated with this email. Try signing into your account.", Toast.LENGTH_SHORT).show()
                        is Unit -> {
                            storeTemporaryCreds(
                                binding.createEmailEditText.editText.text.toString(),
                                binding.createPasswordEditText.editText.text.toString()
                            )
                            authenticationViewModel.createEcommerceAccount()
                            findNavController().navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToEmailVerificationFragment())
                        }
                    }
                }
            }
        }

        binding.logInText.setOnClickListener {
            findNavController().navigate(CreateAccountFragmentDirections.actionCreateAccountFragmentToSignInFragment())
        }

//        binding.whyCreateAccountButton.setOnClickListener {
//            val directions = CreateAccountFragmentDirections.actionCreateAccountFragmentToWhyCreateAccountFragment()
//            findNavController().navigate(directions)
//        }


    }

    override fun emailEditTextDidChangedHandler(chars: CharSequence) { }

    override fun emailStateDidChangeHandler(state: EmailEditText.EmailState) {
        emailState = state
        checkRegisterButtonShouldBeEnabled()
    }

    override fun passwordEditTextDidChangedHandler(chars: CharSequence) { }

    override fun passwordStateDidChangeHandler(state: PasswordEditText.PasswordState) {
        passwordState = state
        checkRegisterButtonShouldBeEnabled()
    }

    private fun storeTemporaryCreds(username: String, password: String) {
        SecuredSharedPref.create(requireContext())?.apply {
            putSecureString(SP_KEY_USER, username)
            putSecureString(SP_KEY_PW, password)
        }
    }

    private fun checkRegisterButtonShouldBeEnabled() {
        binding.createAccountButton.isEnabled = binding.passwordTermsCheckbox.isChecked && binding.privacyNoticeCheckbox.isChecked && emailState.isValid() && passwordState == PasswordEditText.PasswordState.VALID
    }
}