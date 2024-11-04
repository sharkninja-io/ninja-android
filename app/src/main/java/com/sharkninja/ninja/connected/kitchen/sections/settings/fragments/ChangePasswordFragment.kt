package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentChangePasswordBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditText
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditTextInterface
import com.sharkninja.ninja.connected.kitchen.ui.views.VerticalTwoButtonWarningDialog
import kotlinx.coroutines.launch

class ChangePasswordFragment : BaseSettingsFragment(), PasswordEditTextInterface {
    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsViewModel.resetPasswordChange()
        settingsViewModel.resetErrorDialog()

        initBindings()
        observeViewModel()
        setUpNewPasswordField()
    }

    private fun initBindings() {
        binding.saveButton.isEnabled = false

        binding.oldPassword.apply {
            enableToggleButton()
            delegate = this@ChangePasswordFragment
            placeholder = getString(R.string.enter_your_password_hint)
        }

        binding.password.apply {
            showCriteriaBoxes = true
            enableToggleButton()
            delegate = this@ChangePasswordFragment
            placeholder = getString(R.string.enter_your_new_password_hint)
        }

        binding.confirmPassword.apply {
            enableToggleButton()
            delegate = this@ChangePasswordFragment
            placeholder = getString(R.string.enter_your_new_password_hint)

        }

        binding.saveButton.setOnClickListener {
            settingsViewModel.changePassword(
                binding.oldPassword.editText.text.toString(),
                binding.password.editText.text.toString()
            )
            binding.saveButton.isEnabled = false
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingsViewModel.passwordChangeSuccessful.collect {
                        if (it) {
                            val directions =
                                ChangePasswordFragmentDirections.actionAuthentication()
                            findNavController().safeNavigate(directions)
                        }
                    }
                }
                launch {
                    settingsViewModel.showErrorDialog.collect {
                        if (it) {
                            showErrorDialog()
                        }
                    }
                }
            }
        }
    }

    private fun setUpNewPasswordField() {
        binding.password.editText.setOnEditorActionListener { _, _, event ->
            if (event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(binding.password.windowToken, 0)
            }
            return@setOnEditorActionListener false
        }
    }

    override fun passwordEditTextDidChangedHandler(chars: CharSequence) {
    }

    override fun passwordStateDidChangeHandler(state: PasswordEditText.PasswordState) {
        val passwordsMatch =
            binding.password.editText.text.toString() == binding.confirmPassword.editText.text.toString()
        val isValid = binding.password.passwordState == PasswordEditText.PasswordState.VALID
                && passwordsMatch
        binding.saveButton.isEnabled = isValid
        binding.confirmPasswordErrorText.isVisible =
            passwordsMatch.not() && binding.confirmPassword.editText.text.isNullOrEmpty().not()
    }

    private fun retry() {
        settingsViewModel.resetErrorDialog()
        settingsViewModel.changePassword(
            binding.oldPassword.editText.text.toString(),
            binding.password.editText.text.toString()
        )
    }

    private fun showErrorDialog() {
        VerticalTwoButtonWarningDialog {
            title = getString(R.string.something_went_wrong_title)
            description =
                getString(R.string.something_went_wrong_description)
            topButton = VerticalTwoButtonWarningDialog.ButtonConfiguration(
                text = getString(R.string.retry_btn),
                action = { retry() }
            )
            bottomButton = VerticalTwoButtonWarningDialog.ButtonConfiguration(
                text = getString(R.string.cancel),
                action = {}
            )
        }.show(parentFragmentManager, CHANGE_PASSWORD_ERROR_DIALOG)
    }

    companion object {
        private const val CHANGE_PASSWORD_ERROR_DIALOG = "CHANGE_PASSWORD_ERROR"
    }

}