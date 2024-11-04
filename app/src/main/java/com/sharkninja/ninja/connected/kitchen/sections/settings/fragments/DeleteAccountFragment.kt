package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.models.ProfileAccountChangeEvents
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentDeleteAccountBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DeleteAccountFragment: BaseSettingsFragment() {
    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }
    private lateinit var binding: FragmentDeleteAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        overrideBackPressed()
        settingsViewModel.deleteAccount()
        initBindings()
        observeViewModel()
    }

    private fun initBindings() {
        binding.continueButton.setOnClickListener {
            findNavController().safeNavigate(DeleteAccountFragmentDirections.actionAuthentication())
            (activity as HomeActivity).finish()
        }
    }

    private fun observeViewModel() {
        settingsViewModel.profileState.onEach {
            checkDeleteAccountState(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun checkDeleteAccountState(state: ProfileAccountChangeEvents) {
        when(state) {
            is ProfileAccountChangeEvents.DeleteAccountLoading -> {
                binding.txtConfirmMsg.visibility = View.GONE
                binding.verifiedCheck.visibility = View.GONE
                binding.continueButton.visibility = View.GONE
                binding.ellipseWaiting.visibility = View.VISIBLE
            }
            is ProfileAccountChangeEvents.DeleteAccountError -> {
                // no reqs for this - it will continue to show loading but the user will
                //be able to go back
            }
            is ProfileAccountChangeEvents.DeleteAccountSuccess -> {
                binding.txtConfirmMsg.visibility = View.VISIBLE
                binding.verifiedCheck.visibility = View.VISIBLE
                binding.ellipseWaiting.visibility = View.GONE
                binding.continueButton.visibility = View.VISIBLE
                settingsViewModel.updateProfileState(ProfileAccountChangeEvents.ActionProcessed)
            }
            else -> {}
        }
    }

    private fun overrideBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //allow user to go back if delete account fails
                    if(settingsViewModel.profileState.value == ProfileAccountChangeEvents.DeleteAccountError) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

}