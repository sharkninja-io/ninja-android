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
//import com.sharkninja.ninja.connected.kitchen.data.enums.TemperatureUnit
//import com.sharkninja.ninja.connected.kitchen.data.enums.WeightUnit
import com.sharkninja.ninja.connected.kitchen.databinding.BottomSheetPreferenceSelectionBinding
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.EcommerceProfileService
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SettingsService
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SignOutService
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModelFactory

class PreferenceSelectionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetPreferenceSelectionBinding
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

    private lateinit var preferenceType: String
    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetPreferenceSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // These allow us to see the rounded corners
        val bottomsheet = (view.parent as View)
        bottomsheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomsheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomsheet.setBackgroundColor(Color.TRANSPARENT)

        BottomSheetBehavior.from(bottomsheet).state = BottomSheetBehavior.STATE_EXPANDED

        val args: PreferenceSelectionBottomSheetArgs by navArgs()
        preferenceType = args.preferenceType

        initPicker()
    }

    private fun initPicker() {

//        when (preferenceType) {
//            PreferencesFragment.TEMPERATURE_UNIT_SELECTION -> {
////                val tempList = TemperatureUnit.values()
////                val displayArray = tempList.map { it.displayName }.toTypedArray()
//
//                val picker = binding.numberPicker
//                picker.minValue = 0
//                picker.maxValue = displayArray.size - 1
//
//                picker.displayedValues = displayArray
//
//                initClickListeners(displayArray)
//            }

//            PreferencesFragment.WEIGHT_UNIT_SELECTION -> {
//                val weightUnitList = WeightUnit.values()
//                val weightArray = weightUnitList.map { it.selection }.toTypedArray()
//
//                val picker = binding.numberPicker
//                picker.minValue = 0
//                picker.maxValue = weightArray.size - 1
//
//                picker.displayedValues = weightArray
//
//                initClickListeners(weightArray)
//            }
//        }
    }

    private fun initClickListeners(preferenceList: Array<String>) {
//        binding.numberPicker.setOnClickListener {  } //click swallower to prevent text edit option
//
//        binding.confirmButton.setOnClickListener {
//            val selection = preferenceList[binding.numberPicker.value]
//            if (preferenceType == PreferencesFragment.TEMPERATURE_UNIT_SELECTION) {
//                settingsViewModel.saveTemperatureSelection(selection)
//            } else if (preferenceType == PreferencesFragment.WEIGHT_UNIT_SELECTION) {
//                settingsViewModel.saveWeightSelection(selection)
//            }
//            this.dismiss()
//        }
//        binding.cancelButton.setOnClickListener { this.dismiss() }
    }

}