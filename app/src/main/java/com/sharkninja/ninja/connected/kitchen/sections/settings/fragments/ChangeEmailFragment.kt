package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentChangeEmailBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import com.sharkninja.ninja.connected.kitchen.ui.views.VerticalTwoButtonWarningDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChangeEmailFragment : BaseSettingsFragment() {

    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }
    private lateinit var binding: FragmentChangeEmailBinding

    private var newEmailValid = false
    private var emailsMatch = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsViewModel.resetErrorDialog()
        settingsViewModel.resetEmailChange()

        initBindings()
        observeViewModel()
        observeNewEmail()
        observeConfirmEmail()
        setUpConfirmEmailField()
    }

    private fun initBindings() {
        binding.saveButton.isEnabled = false
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingsViewModel.userEmail.collectLatest {
                        displayCurrentEmail(it)
                    }
                }
                launch {
                    settingsViewModel.newEmailValid.collect {
                        newEmailValid = it
                        setSubmitEnabled()
                    }
                }
                launch {
                    settingsViewModel.emailChangeSuccessful.collect {
                        if (it) {
                            signOut()
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

    private fun displayCurrentEmail(currentEmail: String) {
        binding.currentEmail.text = currentEmail
    }

    private fun observeNewEmail() {
        binding.newEmailText.doAfterTextChanged {
            settingsViewModel.checkValidEmail(it.toString())
            checkMatchingEmails()
        }
    }

    private fun setUpConfirmEmailField() {
        binding.confirmEmailText.setOnEditorActionListener { _, _, event ->
            if (event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(binding.confirmEmailText.windowToken, 0)
            }
            return@setOnEditorActionListener false
        }
    }

    private fun observeConfirmEmail() {
        binding.confirmEmailText.doAfterTextChanged {
            checkMatchingEmails()
        }
    }

    private fun checkMatchingEmails() {
        emailsMatch =
            binding.newEmailText.text.toString() == binding.confirmEmailText.text.toString()
        setSubmitEnabled()
    }

    private fun setSubmitEnabled() {
        binding.saveButton.isEnabled = newEmailValid && emailsMatch
        binding.saveButton.setOnClickListener {
            settingsViewModel.changeEmail(
                binding.newEmailText.text.toString(),
            )
            binding.saveButton.isEnabled = false
        }
    }

    private fun signOut() {
        val directions = ChangeEmailFragmentDirections.actionAuthentication()
        findNavController().safeNavigate(directions)

        requireActivity().finish()
    }

    private fun retry() {
        settingsViewModel.resetErrorDialog()
        settingsViewModel.changeEmail(
            binding.newEmailText.text.toString(),
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
                action = { binding.saveButton.isEnabled = true}
            )
        }.show(parentFragmentManager, CHANGE_EMAIL_ERROR_TAG)
    }

    companion object {
        private const val CHANGE_EMAIL_ERROR_TAG = "CHANGE_EMAIL_ERROR"
    }
}