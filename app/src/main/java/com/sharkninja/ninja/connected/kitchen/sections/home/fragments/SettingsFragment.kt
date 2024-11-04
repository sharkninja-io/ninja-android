package com.sharkninja.ninja.connected.kitchen.sections.home.fragments

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentSettingsBinding
import com.sharkninja.ninja.connected.kitchen.databinding.LayoutProgressBarDialogBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.sections.settings.fragments.BaseSettingsFragment
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SettingsFragment : BaseSettingsFragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }
    private val homeViewModel: HomeViewModel by activityViewModels()

    private val loadingDialog by lazy { Dialog(requireContext()) }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
                showLoadingScreen()
                settingsViewModel.subscribeToPushForDevices()
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                showLoadingScreen()
                settingsViewModel.subscribeToPushForDevices()
            } else {
                binding.cookingNotificationsCard.setChecked(false)
                showNotificationModal()
            }
            // Can't show!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        loadSettings()
        initBindings()
        overrideBackPressed()
    }

    private fun observeViewModel() {
        settingsViewModel.isCookingPushNotificationsEnabled.onEach {
            loadingDialog.dismiss()
            binding.cookingNotificationsCard.setChecked(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun loadSettings() {
        settingsViewModel.fetchGrills()
        if(homeViewModel.isBackFromAddAppliance) {
            homeViewModel.isBackFromAddAppliance = false
            findNavController().safeNavigate(SettingsFragmentDirections.actionSettingsFragmentToAppliancesFragment())
        }
        settingsViewModel.fetchSelectedWeightUnit()
//        settingsViewModel.fetchSelectedTemperatureUnit()
        settingsViewModel.getCurrentEmail()
        settingsViewModel.fetchProfile()
        settingsViewModel.fetchRegion()

        settingsViewModel.observeDevicePushEnabled()
    }

    private fun initBindings() {
        binding.accountCard.setOnClick {
            val directions = SettingsFragmentDirections.actionSettingsFragmentToAccountFragment()
            findNavController().safeNavigate(directions)
        }
        binding.aboutThisAppCard.setOnClick {
            val directions =
                SettingsFragmentDirections.actionSettingsFragmentToAboutThisAppFragment()
            findNavController().safeNavigate(directions)
        }
        binding.yourApplianceCard.setOnClick {
            val directions = SettingsFragmentDirections.actionSettingsFragmentToAppliancesFragment()
            findNavController().safeNavigate(directions)
        }
        binding.cookingNotificationsCard.setOnCheckedChangeListener { isEnable ->
            if(isEnable) {
                askNotificationPermission()
            } else {
                showLoadingScreen()
                settingsViewModel.setPushEnabledUserCache(false)
                settingsViewModel.unSubscribeToPushForDevice()
            }
        }
    }

    private fun askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            showLoadingScreen()
            settingsViewModel.subscribeToPushForDevices()

            // FCM SDK (and your app) can post notifications.
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            binding.cookingNotificationsCard.setChecked(false)
            showNotificationModal()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun showLoadingScreen() {
        val dialogBinding = LayoutProgressBarDialogBinding.inflate(layoutInflater)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setCancelable(false)
        loadingDialog.setContentView(dialogBinding.root)
        loadingDialog.show()
    }

    private fun showNotificationModal() {
        findNavController().safeNavigate(SettingsFragmentDirections
            .actionSettingsFragmentToNotificationPermissionDialog())
    }

    private fun overrideBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }
}