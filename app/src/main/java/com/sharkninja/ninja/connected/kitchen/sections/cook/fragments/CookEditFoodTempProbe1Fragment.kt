package com.sharkninja.ninja.connected.kitchen.sections.cook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.sharkninja.grillcore.Food
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentProbeDarkmodeBinding
import com.sharkninja.ninja.connected.kitchen.ext.logBreadCrumbClickEvent
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.OnSnapPositionChangeListener
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.services.attachSnapHelperWithListener
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.adapters.FoodTempListAdapter
import com.sharkninja.ninja.connected.kitchen.ui.views.DarkThemeDialog
import com.sharkninja.ninja.connected.kitchen.ui.views.SnapOnScrollListener
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CookEditFoodTempProbe1Fragment : DialogFragment() {

    private lateinit var binding: FragmentProbeDarkmodeBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: FoodTempListAdapter
    private val layoutManager by lazy { LinearLayoutManager(requireContext()) }
    private val snapHelper by lazy { LinearSnapHelper() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProbeDarkmodeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
        getFoodList()
        initCustomDial()
        subscribeToVm()
        calculateSpacingForHorizontalModeSelector()
        initClickListeners()
    }

    override fun getTheme(): Int {
        return R.style.DialogThemeFullScreenDarkTheme
    }

    private fun initBindings() {
        binding.foodSelectorMenu.apply {
            isUSGrill = homeViewModel.isUSGrill()
        }
    }
    private fun initCustomDial() {
        binding.temperatureDial.temperaturesRv.layoutManager = layoutManager

        adapter = FoodTempListAdapter(homeViewModel.probeFoodList.value)
        binding.temperatureDial.temperaturesRv.adapter = adapter
        adapter.setColorTheme(isDarkTheme = true)
        binding.temperatureDial.temperaturesRv.attachSnapHelperWithListener(snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, layoutManager, object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    updateList(position)
                }
            })

    }

    fun updateList(selectedPosition: Int) {
        val mList = homeViewModel.probeFoodList.value
        val selectedItem = mList.getOrNull(selectedPosition)
        selectedItem?.let {
            val newList = mList.map {
                it.copy(isSelected = it.tempString == selectedItem.tempString)
            }
            adapter.updateList(newList)
        }
    }

    private fun snapToSelectedTemperature() {
        val desiredTemp = homeViewModel.selectedGrillState.value.probe2.desiredTemp
        var index = homeViewModel.probeFoodList.value.indexOfFirst { it.tempString == desiredTemp.toString() }
        if (index == -1) index = 0
        binding.temperatureDial.temperaturesRv.post {
            binding.temperatureDial.temperaturesRv.scrollToPosition(index)
            updateList(index)
        }
    }

    private fun subscribeToVm() {

        homeViewModel.probeFoodList.onEach {
            adapter.updateList(it)
            snapToSelectedTemperature()
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)


        homeViewModel.userTemperatureUnit.onEach {
            adapter.updateTemperatureUnit(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)


        homeViewModel.connectionStatusToolbar.onEach {
            binding.cookToolbar.setConnectionStatus(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.selectedGrillState.onEach {
            setSaveButtonEnabled(it.probe2.pluggedIn)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun setSaveButtonEnabled(isPluggedIn: Boolean) {
        val isButtonEnabled = if(isPluggedIn) true else !homeViewModel.shouldShowThermometerNotPluggedIn()
        binding.saveButton.isClickable = isButtonEnabled
        if(isButtonEnabled) {
            binding.saveButton.alpha = 1F
            binding.saveButton.text = getString(R.string.save_changes_btn_cook_edit_probe)
        } else {
            binding.saveButton.alpha = .5F
            binding.saveButton.text = getString(R.string.save_changes_btn_cook_edit_probe_two_disabled)
        }
    }

    private fun initClickListeners() {
        binding.cookToolbar.setEditScreenToolbarTitle(getString(R.string.probe1_manual_title))

        binding.cookToolbar.setNavIconOnClickListener {
            if(checkForChanges()) showGoBackDialog() else dismiss()
        }

        binding.cookTip.setArrowOnClick()

        binding.saveButton.setOnClickListener {
            logBreadCrumbClickEvent("UPDATE PROTEIN/TEMP")
            onSave()
        }

        binding.foodSelectorMenu.setProteinMenuItemOnClicksListeners {
            homeViewModel.getFoodListProbe(it)
            setDialLabel(it)
        }
    }

    private fun setDialLabel(food: Food) {
        if(food == Food.Manual || food == Food.NotSet) showManualThermometerDialLabel()
        else showRoastSettingsDialLabel()
    }

    private fun getFoodList() {
        val currentFood = homeViewModel.selectedGrillState.value.probe2.food.protein
        homeViewModel.getFoodListProbe(currentFood)
        setDialLabel(currentFood)
        val selectedFood = if(currentFood == Food.NotSet) Food.Manual else currentFood
        binding.foodSelectorMenu.apply {
            foodSelection = selectedFood
        }
    }

    private fun showManualThermometerDialLabel() {
        binding.temperatureDial.dialLabelLeft.visibility = View.INVISIBLE
        binding.temperatureDial.dialLabelRight.visibility = View.INVISIBLE
        binding.temperatureDial.dividerVertical.visibility = View.INVISIBLE
        binding.temperatureDial.dialLabelCenter.visibility = View.VISIBLE
    }

    private fun showRoastSettingsDialLabel() {
        binding.temperatureDial.dialLabelCenter.visibility = View.INVISIBLE
        binding.temperatureDial.dialLabelLeft.visibility = View.VISIBLE
        binding.temperatureDial.dialLabelRight.visibility = View.VISIBLE
        binding.temperatureDial.dividerVertical.visibility = View.VISIBLE
    }

    private fun onSave() {
        val probe = homeViewModel.selectedGrillState.value.probe2
        if (probe.pluggedIn.not() && homeViewModel.shouldShowThermometerNotPluggedIn()) {
            showThermometerNotPluggedInDialog()
        } else {
            val selectedTemperature =
                adapter.internalTempList.firstOrNull { temp -> temp.isSelected }
            if (selectedTemperature != null) {
                homeViewModel.editSecondThermometerSettings(
                    selectedTemperature.tempString,
                    binding.foodSelectorMenu.foodSelection,
                    selectedTemperature.presetIndex
                )
                dismiss()
            }
        }
    }

    private fun calculateSpacingForHorizontalModeSelector() {
        binding.foodSelectorMenu.calculateSpacingForHorizontalModeSelector(
            homeViewModel.density,
            homeViewModel.xPix,
            homeViewModel.isUSGrill()
        )
    }

    private fun showThermometerNotPluggedInDialog() {
        DarkThemeDialog {
            title = getString(R.string.dialog_thermometer_not_plugged_in_title)
            description = getString(R.string.dialog_thermometer_not_plugged_in_description)
            topButton = DarkThemeDialog.ButtonConfiguration(
                text = getString(R.string.dialog_thermometer_not_plugged_in_button),
                action = { dismiss() }
            )
        }.show(parentFragmentManager, COOK_EDIT_PROBE1_TAG)
    }

    private fun checkForChanges(): Boolean {
        var currentDesiredTemp = homeViewModel.selectedGrillState.value.probe2.desiredTemp
        if(currentDesiredTemp == 0) currentDesiredTemp = 100
        var currentProtein = homeViewModel.selectedGrillState.value.probe2.food.protein
        if(currentProtein == Food.NotSet) currentProtein = Food.Manual
        val selectedDesiredTemp = adapter.internalTempList.firstOrNull { tempItem -> tempItem.isSelected }?.tempString?.toInt()
        val selectedProtein = binding.foodSelectorMenu.foodSelection
        return if(currentProtein == selectedProtein) currentDesiredTemp != selectedDesiredTemp else true
    }

    private fun showGoBackDialog() {
        DarkThemeDialog {
            title = getString(R.string.cook_settings_go_back_dialog_title)
            description = getString(R.string.cook_settings_go_back_dialog_description)
            topButton = DarkThemeDialog.ButtonConfiguration(
                text = getString(R.string.cook_settings_go_back_dialog_top_button),
                action = { dismiss() }
            )
            bottomButton = DarkThemeDialog.ButtonConfiguration(
                text = getString(R.string.cancel_upper)
            )
            topButtonColor = R.color.ninja_button_red_background
        }.show(parentFragmentManager, COOK_EDIT_PROBE1_TAG)
    }

    companion object {
        const val COOK_EDIT_PROBE1_TAG = "FRAGMENT_COOK_EDIT_PROBE1"
    }
}