package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.State
import com.sharkninja.ninja.connected.kitchen.data.enums.State.Companion.getEnumByDisplayName
import com.sharkninja.ninja.connected.kitchen.data.models.ProfileAccountChangeEvents
import com.sharkninja.ninja.connected.kitchen.data.models.UserProfile
import com.sharkninja.ninja.connected.kitchen.databinding.DialogSaveChangesBinding
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentProfileBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.ext.toPx
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import com.sharkninja.ninja.connected.kitchen.ui.views.VerticalTwoButtonWarningDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


const val US_COUNTRY = "US"
const val CA_COUNTRY = "CA"

class ProfileFragment : BaseSettingsFragment() {

    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }
    private lateinit var binding: FragmentProfileBinding

    private var country = ""
    private var state = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initClickListeners()
        setUpFilters()
        setUpEditTextListeners()
        initToolbar()
    }

    private fun initToolbar() {
        (requireActivity() as? HomeActivity)?.setToolbarNavCustomClickListener {
            if (checkForChanges() && checkRequiredFields()) initSaveChangesDialog()
            else findNavController().popBackStack()
        }
    }

    private fun initClickListeners() {
        binding.saveButton.setOnClickListener { saveProfileChanges() }
        binding.profileItems.deleteAccount.setOnClickListener { showDeleteAccountDialog() }
    }

    private fun observeViewModel() {

        settingsViewModel.profileState.onEach {
            checkProfileAction(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        settingsViewModel.currentUser.onEach {
            populateProfileFields(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        settingsViewModel.countrySelection.onEach {
                country = it
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        settingsViewModel.stateSelection.onEach {
            state = it
            setUpStateField(country, it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        settingsViewModel.requiredFieldsValid.onEach {
            binding.saveButton.isEnabled = it
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }


    private fun checkProfileAction(action: ProfileAccountChangeEvents) {
        when(action) {
            is ProfileAccountChangeEvents.UpdateAccountLoading -> {
                binding.saveButton.isEnabled = false
            }
            is ProfileAccountChangeEvents.UpdateAccountSuccess -> {
                findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToAccountFragment())
                settingsViewModel.updateProfileState(ProfileAccountChangeEvents.ActionProcessed)
            }
            is ProfileAccountChangeEvents.UpdateAccountError -> {
                showErrorDialog()
                settingsViewModel.updateProfileState(ProfileAccountChangeEvents.ActionProcessed)
            }
            else -> {}
        }
    }

    private fun populateProfileFields(user: UserProfile) {
        binding.profileItems.firstNameText.setText(user.firstName)
        binding.profileItems.lastNameText.setText(user.lastName)
        binding.profileItems.addressText.setText(user.address)
        binding.profileItems.addressTwoText.setText(user.addressLine2)
        binding.profileItems.cityText.setText(user.city)
        binding.profileItems.stateText.setText(user.state)
        binding.profileItems.zipCodeText.setText(user.zipCode)
        binding.profileItems.phoneNumberText.setText(user.phoneNumber)
    }

    private fun setUpFilters() {
        setZipCodeFilters()
        setNameFilters()
        setAddressFilters()
        setCityFilters()
        setUpStateField(country, state)
        setUpPhoneNumberField()
    }

    private fun setZipCodeFilters() {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(5)
        binding.profileItems.zipCodeText.filters = filterArray
    }

    private fun setNameFilters() {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(25)
        binding.profileItems.firstNameText.filters = filterArray
        binding.profileItems.lastNameText.filters = filterArray
    }

    private fun setAddressFilters() {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(50)
        binding.profileItems.addressText.filters = filterArray
        binding.profileItems.addressTwoText.filters = filterArray
    }

    private fun setCityFilters() {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(30)
        binding.profileItems.cityText.filters = filterArray
    }

    private fun setUpPhoneNumberField() {
        binding.profileItems.phoneNumberText.setOnEditorActionListener { _, _, event ->
            if (event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(binding.profileItems.phoneNumberText.windowToken, 0)
            }
            return@setOnEditorActionListener false
        }
    }

    private fun setPhoneNumberFilters() {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(10)
        binding.profileItems.phoneNumberText.filters = filterArray
    }

    private fun setUpEditTextListeners() {
        binding.profileItems.firstNameText.setFocusAndTextChangeListener(binding.profileItems.errorMsgFirstName)
        binding.profileItems.lastNameText.setFocusAndTextChangeListener(binding.profileItems.errorMsgLastName)
        binding.profileItems.addressText.setFocusAndTextChangeListener(binding.profileItems.errorMsgAddress)
        binding.profileItems.cityText.setFocusAndTextChangeListener(binding.profileItems.errorMsgCity)
        binding.profileItems.zipCodeText.setFocusAndTextChangeListener(binding.profileItems.errorMsgZipCode)
        binding.profileItems.stateText.setFocusAndTextChangeListener(binding.profileItems.errorMsgState)
    }

    private fun EditText.setFocusAndTextChangeListener(errorTextView: TextView) {
        var textWatcher: TextWatcher? = null
        this.setOnFocusChangeListener { view, hasFocus ->
            view as EditText
            if (hasFocus) {
                textWatcher = view.doAfterTextChanged { text ->
                    errorTextView.isVisible = text.isNullOrEmpty()
                    checkRequiredFields()
                }
            } else {
                if (textWatcher != null) {
                    view.removeTextChangedListener(textWatcher)
                    textWatcher = null
                }
                errorTextView.isVisible = view.text.isNullOrEmpty()
            }
        }
    }

    private fun initSaveChangesDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogSaveChangesBinding.inflate(layoutInflater)

        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 24.toPx())
        dialog.window?.setBackgroundDrawable(inset)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }
        dialogBinding.saveButton.setOnClickListener {
            dialog.dismiss()
            saveProfileChanges()
        }
        dialogBinding.discardButton.setOnClickListener {
            dialog.dismiss()
            findNavController().popBackStack()
        }
        dialog.show()
    }

    private fun setUpStateField(country: String = US_COUNTRY, state: String) {

        if (state.isNotEmpty()) {
            val displayText = State.getEnumByName(state)?.getDisplayName()
            displayText?.let { binding.profileItems.stateText.setText(displayText) }
        }
        binding.profileItems.stateText.setOnClickListener { openStateList(country) }
        binding.profileItems.stateDropdownIcon.setOnClickListener { openStateList(country) }
    }

    private fun openStateList(country: String) {
        val directions =
            ProfileFragmentDirections.actionProfileFragmentToStateSelectionBottomSheet(country)
        findNavController().safeNavigate(directions)
    }

    private fun checkRequiredFields(): Boolean {
        val requiredFieldsFilled = binding.profileItems.firstNameText.text.isNotEmpty()
                && binding.profileItems.lastNameText.text.isNotEmpty()
                && binding.profileItems.addressText.text.isNotEmpty()
                && binding.profileItems.cityText.text.isNotEmpty()
                && binding.profileItems.stateText.text.isNotEmpty()
                && binding.profileItems.zipCodeText.text.isNotEmpty()

        settingsViewModel.updateRequiredFieldState(requiredFieldsFilled)
        return requiredFieldsFilled
    }

    private fun checkForChanges(): Boolean {
        val currentUserState = settingsViewModel.currentUser.value
        return (binding.profileItems.firstNameText.text.toString() != currentUserState.firstName
                || binding.profileItems.lastNameText.text.toString() != currentUserState.lastName
                || binding.profileItems.addressText.text.toString() != currentUserState.address
                || binding.profileItems.cityText.text.toString() != currentUserState.city
                || getEnumByDisplayName(binding.profileItems.stateText.text.toString()).toString() != currentUserState.state
                || binding.profileItems.zipCodeText.text.toString() != currentUserState.zipCode)
    }

    private fun saveProfileChanges() {
        settingsViewModel.updateUserProfile(
            firstName = binding.profileItems.firstNameText.text.toString(),
            lastName = binding.profileItems.lastNameText.text.toString(),
            address = binding.profileItems.addressText.text.toString(),
            addressLine2 = binding.profileItems.addressTwoText.text.toString(),
            city = binding.profileItems.cityText.text.toString(),
            zipCode = binding.profileItems.zipCodeText.text.toString(),
            phoneNumber = binding.profileItems.phoneNumberText.text.toString()
        )
    }

    private fun showDeleteAccountDialog() {
        VerticalTwoButtonWarningDialog {
            title = getString(R.string.delete_account_dialog_title_text)
            description =
                getString(R.string.delete_account_dialog_description_text)
            topButton = VerticalTwoButtonWarningDialog.ButtonConfiguration(
                text = getString(R.string.delete_account_button_text),
                action = {
                    findNavController().safeNavigate(ProfileFragmentDirections.actionProfileFragmentToDeleteAccountFragment())
                }
            )
            bottomButton = VerticalTwoButtonWarningDialog.ButtonConfiguration(
                text = getString(R.string.cancel)
            )
        }.show(parentFragmentManager, DELETE_ACCOUNT_TAG)
    }

    private fun showErrorDialog() {
        VerticalTwoButtonWarningDialog {
            title = getString(R.string.something_went_wrong_title)
            description =
                getString(R.string.something_went_wrong_description)
            topButton = VerticalTwoButtonWarningDialog.ButtonConfiguration(
                text = getString(R.string.retry_btn),
                action = { saveProfileChanges() }
            )
            bottomButton = VerticalTwoButtonWarningDialog.ButtonConfiguration(
                text = getString(R.string.cancel),
                action = { binding.saveButton.isEnabled = true }
            )
        }.show(parentFragmentManager, UPDATE_ACCOUNT_ERROR_TAG)
    }

    companion object {
        private const val DELETE_ACCOUNT_TAG = "DELETE_ACCOUNT"
        private const val UPDATE_ACCOUNT_ERROR_TAG = "UPDATE_ACCOUNT_ERROR"

    }
}