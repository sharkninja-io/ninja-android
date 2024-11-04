package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.State
import com.sharkninja.ninja.connected.kitchen.databinding.BottomSheetStateSelectionBinding
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.EcommerceProfileService
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SettingsService
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SignOutService
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModelFactory

class StateSelectionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetStateSelectionBinding
    private val settingsFactory by lazy {
        SettingsViewModelFactory(
            SettingsService(
                requireActivity().applicationContext,
                CacheService(),
                SignOutService(requireActivity().applicationContext),
                EcommerceProfileService.createInstance(requireActivity().applicationContext)
            ),
            CacheService()
        )
    }
    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }

    private lateinit var country: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetStateSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // These allow us to see the rounded corners
        val bottomsheet = (view.parent as View)
        bottomsheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomsheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomsheet.setBackgroundColor(Color.TRANSPARENT)

        BottomSheetBehavior.from(bottomsheet).state = BottomSheetBehavior.STATE_EXPANDED

        val args: StateSelectionBottomSheetArgs by navArgs()
        country = args.countryArg

        initPicker()
    }

    private fun initPicker() {
        val stateList = if (country == "CA") {
            State.values().drop(55)
        } else {
            State.values().dropLast(13)
        }
        val stateArray = stateList.map { it.name }.toTypedArray()
        val displayArray = stateList.map { it.getDisplayName() }.toTypedArray()

        val picker = binding.numberPicker
        picker.minValue = 0
        picker.maxValue = displayArray.size - 1

        picker.displayedValues = displayArray

        initClickListeners(stateArray)
    }

    private fun initClickListeners(stateList: Array<String>) {
        binding.confirmButton.setOnClickListener {
            val selectedState = stateList[binding.numberPicker.value]
            settingsViewModel.saveStateSelection(selectedState)
            this.dismiss()
        }
        binding.cancelButton.setOnClickListener { this.dismiss() }
    }

}