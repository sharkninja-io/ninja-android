package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.BuildConfig
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.models.Appliance
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentApplianceDetailBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import com.sharkninja.ninja.connected.kitchen.ui.views.VerticalTwoButtonWarningDialog
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ApplianceDetailFragment : BaseSettingsFragment() {

    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }
    private lateinit var binding: FragmentApplianceDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApplianceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsViewModel.resetDeleteApplianceSuccess()
        observeViewModel()
        initClickListeners()
    }

    private fun observeViewModel() {
        settingsViewModel.currentAppliance.onEach {
            addDetails(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun addDetails(appliance: Appliance) {
        binding.dsnCard.setDetailValue(appliance.details.dsn)
        binding.macAddressCard.setDetailValue(appliance.details.mac)
        binding.appVersionCard.setDetailValue("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
        binding.firmwareVersionCard.setDetailValue(appliance.details.otaFirmwareVersion)
    }

    private fun initClickListeners() {
//        binding.restoreFactorySettingsCard.setOnClick { showResetFactorySettingsDialog() }
        binding.deleteApplianceCard.setOnClick { showDeleteApplianceDialog() }
    }

    private fun showDeleteApplianceDialog() {
        VerticalTwoButtonWarningDialog {
            title = getString(R.string.dialog_delete_appliance_title)
            description =
                getString(R.string.dialog_delete_appliance_description)
            topButton = VerticalTwoButtonWarningDialog.ButtonConfiguration(
                text = getString(R.string.delete_appliance_btn),
                action = {
                    showLoading()
                    settingsViewModel.deleteAppliance()
                }
            )
            bottomButton = VerticalTwoButtonWarningDialog.ButtonConfiguration(
                text = getString(R.string.cancel)
            )
        }.show(parentFragmentManager, DELETE_APPLIANCE_TAG)
    }

    private fun showResetFactorySettingsDialog() {
        VerticalTwoButtonWarningDialog {
            title = getString(R.string.dialog_restore_factory_settings_title)
            description =
                getString(R.string.dialog_restore_factory_settings_description)
            topButton = VerticalTwoButtonWarningDialog.ButtonConfiguration(
                text = getString(R.string.restore_settings_btn),
                action = {
                    showLoading()
                    settingsViewModel.restoreFactorySettings()
                }
            )
            bottomButton = VerticalTwoButtonWarningDialog.ButtonConfiguration(
                text = getString(R.string.cancel)
            )
        }.show(parentFragmentManager, RESTORE_FACTORY_SETTINGS_TAG)
    }

    private fun showLoading() {
        findNavController().safeNavigate(ApplianceDetailFragmentDirections.actionShowProgressBar())
    }

    companion object {
        private const val DELETE_APPLIANCE_TAG = "DELETE_APPLIANCE"
        private const val RESTORE_FACTORY_SETTINGS_TAG = "RESTORE_FACTORY_SETTINGS"
    }
}