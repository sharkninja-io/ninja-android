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
import com.sharkninja.ninja.connected.kitchen.data.enums.SettingsEvent
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentAccountBinding
import com.sharkninja.ninja.connected.kitchen.ext.hide
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.ext.show
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AccountFragment : BaseSettingsFragment() {

    private lateinit var binding: FragmentAccountBinding
    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    private fun initBindings() {
        binding.changeEmailCard.setOnClick {
            val directions = AccountFragmentDirections.actionAccountFragmentToChangeEmailFragment()
            findNavController().safeNavigate(directions)
        }
        binding.changePasswordCard.setOnClick {
            val directions =
                AccountFragmentDirections.actionAccountFragmentToChangePasswordFragment()
            findNavController().safeNavigate(directions)
        }
        binding.profileCard.setOnClick {
            val directions = AccountFragmentDirections.actionAccountFragmentToProfileFragment()
            findNavController().safeNavigate(directions)
        }
        binding.logOutCard.setOnClick { showSignOutDialog() }

        subscribeToVm()
    }

    private fun showSignOutDialog() {
        val directions = AccountFragmentDirections.actionAccountFragmentToSignOutDialog()
        findNavController().safeNavigate(directions)
    }

    private fun subscribeToVm() {
        settingsViewModel.settingsEvent.onEach {
            checkSettingsEvent(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

   private fun checkSettingsEvent(event: SettingsEvent) {
       when(event) {
           SettingsEvent.SignOut -> {
               overrideBackPressed()
               binding.loadingOverlay.show()
               settingsViewModel.unSubscribeToPushForDevice()
               settingsViewModel.signOut()
           }
           SettingsEvent.UserSignOutError -> {
                settingsViewModel.updateSettingsEvent(SettingsEvent.ActionProcessed)
                binding.loadingOverlay.hide()
           }
           SettingsEvent.UserSignedOut -> {
               findNavController().safeNavigate(AccountFragmentDirections.actionAuthentication())
               (activity as HomeActivity).finishAffinity()
           }
           SettingsEvent.ActionProcessed -> {}
       }
   }

    private fun overrideBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //allow user to go back if delete account fails
                    if(settingsViewModel.settingsEvent.value != SettingsEvent.SignOut) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

}