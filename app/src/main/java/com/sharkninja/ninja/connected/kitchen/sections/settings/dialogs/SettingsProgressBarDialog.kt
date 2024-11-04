package com.sharkninja.ninja.connected.kitchen.sections.settings.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.LayoutProgressBarDialogBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs.COUNT_DOWN_INTERVAL
import com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs.COUNT_DOWN_LENGTH
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SettingsProgressBarDialog: BaseDialogFragment() {


    private lateinit var binding: LayoutProgressBarDialogBinding
    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }


    private val timer by lazy {
        object: CountDownTimer(COUNT_DOWN_LENGTH, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() { dismiss() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutProgressBarDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToVM()
    }

    override fun onResume() {
        super.onResume()
        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun subscribeToVM() {
        settingsViewModel.deleteApplianceSuccess.filterNotNull().onEach { isSuccess ->
                if (isSuccess) onDeleteApplianceSuccess()
                else onDeleteApplianceFailure()
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        settingsViewModel.restoreFactorySettingsSuccessful.filterNotNull().onEach {
            if(it) onRestoreFactorySettingsSuccess()
            else onRestoreFactorySettingsFailure()
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun onDeleteApplianceSuccess() {
        settingsViewModel.resetDeleteApplianceSuccess()
        findNavController().safeNavigate(SettingsProgressBarDialogDirections.actionLoadingToAppliancesFragment())
    }

    private fun onDeleteApplianceFailure() {
        dismiss()
        Toast.makeText(requireContext(), "Failed To Delete Appliance", Toast.LENGTH_LONG).show()
    }

    private fun onRestoreFactorySettingsSuccess() {
        settingsViewModel.resetRestoreFactorySettingsSuccessful()
        findNavController().safeNavigate(SettingsProgressBarDialogDirections.actionLoadingToAppliancesFragment())
    }

    private fun onRestoreFactorySettingsFailure() {
        dismiss()
        Toast.makeText(requireContext(), "Failed To Restore Factory Settings", Toast.LENGTH_LONG).show()
    }
}
