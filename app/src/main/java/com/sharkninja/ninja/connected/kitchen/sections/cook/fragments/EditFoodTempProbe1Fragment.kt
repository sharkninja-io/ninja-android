package com.sharkninja.ninja.connected.kitchen.sections.cook.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.sharkninja.grillcore.Food
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.PROBE_1
import com.sharkninja.ninja.connected.kitchen.data.enums.getToolbarTitle
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentProbeBinding
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.OnSnapPositionChangeListener
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.services.attachSnapHelperWithListener
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.adapters.FoodTempListAdapter
import com.sharkninja.ninja.connected.kitchen.ui.views.SnapOnScrollListener
import com.sharkninja.ninja.connected.kitchen.ui.views.VerticalTwoButtonPositiveDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class EditFoodTempProbe1Fragment : DialogFragment() {

    private lateinit var binding: FragmentProbeBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var adapter: FoodTempListAdapter
    private val snapHelper by lazy { LinearSnapHelper() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProbeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
        initCustomDial()
        subscribeToVm()
        calculateSpacingForHorizontalModeSelector()
        initClickListeners()
    }

    override fun getTheme(): Int {
        return R.style.DialogThemeFullScreen
    }

    private fun initBindings() {
        binding.foodSelectorMenu.apply {
            isUSGrill = homeViewModel.isUSGrill()
        }
    }

    private fun initCustomDial() {
        // get initial list
        homeViewModel.getFoodListProbe(homeViewModel.probe1FoodPreset.value.food)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.temperatureDial.temperaturesRv.layoutManager = layoutManager

        adapter = FoodTempListAdapter(homeViewModel.probeFoodList.value)
        binding.temperatureDial.temperaturesRv.adapter = adapter
        binding.temperatureDial.temperaturesRv.attachSnapHelperWithListener(snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, layoutManager, object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    Log.d("internalTemperaturesRv", "onSnapPositionChange: ${position}")
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
        val currentTemp = if (homeViewModel.isProbeSet(PROBE_1)) {
            homeViewModel.preCookDashboardUiState.value.probe1FoodTempValue
        } else {
            if(isManualSelected()) {
                homeViewModel.getProbeDefaultValue()
            } else {
                homeViewModel.probeFoodList.value[0].tempString
            }
        }
        var index = homeViewModel.probeFoodList.value.indexOfFirst { it.tempString == currentTemp }
        if (index == -1) index = 0
        binding.temperatureDial.temperaturesRv.post {
            binding.temperatureDial.temperaturesRv.scrollToPosition(index)
            updateList(index)
        }
    }

    private fun isManualSelected(): Boolean {
        return binding.foodSelectorMenu.foodSelection == Food.Manual || binding.foodSelectorMenu.foodSelection == Food.NotSet
    }

    private fun subscribeToVm() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    homeViewModel.cookMode.collectLatest {
                        binding.cookToolbar.setEditScreenToolbarTitle(it.getToolbarTitle(requireContext()))
                    }
                }

                launch {
                    homeViewModel.probe1FoodPreset.collectLatest {
                        binding.foodSelectorMenu.apply {
                            foodSelection = it.food
                        }
                    }
                }

                launch {
                    homeViewModel.probeFoodList.collectLatest {
                        adapter.updateList(it)
                        snapToSelectedTemperature()
                    }
                }

                launch {
                    homeViewModel.userTemperatureUnit.collectLatest {
                        adapter.updateTemperatureUnit(it)
                    }
                }

                launch {
                    homeViewModel.connectionStatusToolbar.collectLatest {
                        binding.cookToolbar.setConnectionStatus(it)
                    }
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.cookToolbar.setNavIconOnClickListener {
            if (checkForChanges()) showGoBackDialog() else findNavController().popBackStack()
        }
        binding.cookTip.setArrowOnClick()
        binding.saveButton.setOnClickListener { onSave() }

        binding.foodSelectorMenu.setProteinMenuItemOnClicksListeners {
            homeViewModel.getFoodListProbe(it)
            if (it == Food.Manual || it == Food.NotSet) showManualThermometerDialLabel()
            else showRoastSettingsDialLabel()
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
        val foodItem = adapter.internalTempList.firstOrNull{ temp -> temp.isSelected}
        if (foodItem?.presetIndex != null) {
            homeViewModel.selectFoodProbe1(binding.foodSelectorMenu.foodSelection)
            homeViewModel.saveProbe1Selection(foodItem)
            homeViewModel.updateIsProbe1ToggleStatus(true)
            dismiss()
        }
    }

    private fun calculateSpacingForHorizontalModeSelector() {
       binding.foodSelectorMenu.calculateSpacingForHorizontalModeSelector(
           homeViewModel.density,
           homeViewModel.xPix,
           homeViewModel.isUSGrill()
       )
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as HomeActivity).showBottomTabNavigation()
    }

    private fun checkForChanges(): Boolean {
        val savedProtein = homeViewModel.probe1FoodPreset.value.food
        val savedTemperature = homeViewModel.preCookDashboardUiState.value.probe1FoodTempValue

        val selectedTemperature = adapter.internalTempList.firstOrNull { tempItem -> tempItem.isSelected}?.tempString
        val selectedProtein = binding.foodSelectorMenu.foodSelection
        return savedProtein != selectedProtein || savedTemperature != selectedTemperature
    }

    private fun showGoBackDialog() {
        VerticalTwoButtonPositiveDialog {
            title = getString(R.string.cook_settings_go_back_dialog_title)
            description = getString(R.string.cook_settings_go_back_dialog_description)
            topButton = VerticalTwoButtonPositiveDialog.ButtonConfiguration(
                text = getString(R.string.cook_settings_go_back_dialog_top_button),
                action = { dismiss() }
            )
            bottomButton = VerticalTwoButtonPositiveDialog.ButtonConfiguration(
                text = getString(R.string.cancel_upper)
            )
        }.show(parentFragmentManager, EDIT_FOOD_TEMP_PROBE_1_TAG)
    }

    companion object {
        private const val EDIT_FOOD_TEMP_PROBE_1_TAG = "editFoodTempProbe1Fragment"
    }

}