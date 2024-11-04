package com.sharkninja.ninja.connected.kitchen.sections.cook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.IconButtonState
import com.sharkninja.ninja.connected.kitchen.data.enums.getToolbarTitle
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentCookEditTempBinding
import com.sharkninja.ninja.connected.kitchen.ext.logBreadCrumbClickEvent
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.OnSnapPositionChangeListener
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.services.attachSnapHelperWithListener
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.adapters.GrillTempListAdapter
import com.sharkninja.ninja.connected.kitchen.ui.views.DarkThemeDialog
import com.sharkninja.ninja.connected.kitchen.ui.views.SnapOnScrollListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CookEditTempFragment : DialogFragment() {

    private lateinit var binding: FragmentCookEditTempBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var grillTempListAdapter: GrillTempListAdapter
    private val snapHelper by lazy { LinearSnapHelper() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCookEditTempBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initClickListeners()
        initGrillTemperatureDial()
        initBindings()
        subscribeToVm()
    }

    override fun getTheme(): Int {
        return R.style.DialogThemeFullScreenDarkTheme
    }

    private fun initToolbar() {
        binding.cookToolbar.setNavIconOnClickListener {
            if(checkForChanges()) showGoBackDialog() else dismiss()
        }
    }

    private fun initBindings() {
        binding.menuCookMode.setCookModeMenuOptions(homeViewModel.isUSGrill())

        binding.menuCookMode.apply {
            cookModesUnavailableInThermometerState = IconButtonState.DarkDisabled
        }
        binding.menuCookMode.calculateSpacingForHorizontalModeSelector(
            homeViewModel.density,
            homeViewModel.xPix
        )
        grillTempListAdapter.cookMode = homeViewModel.selectedGrillState.value.cookMode
        homeViewModel.selectDashboardCookMode(homeViewModel.selectedGrillState.value.cookMode)
    }

    private fun initClickListeners() {
        binding.saveButton.setOnClickListener {
            logBreadCrumbClickEvent("UPDATE GRILL TEMP")
            onSave()
        }
        binding.menuCookMode.setMenuItemOnClicksListeners {
            grillTempListAdapter.cookMode = it
            homeViewModel.selectDashboardCookMode(it)
        }
    }


    private fun subscribeToVm() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    homeViewModel.cookMode.collectLatest {
                        binding.cookToolbar.setEditScreenToolbarTitle(it.getToolbarTitle(requireContext()))
                        binding.menuCookMode.apply {
                            cookMode = it
                        }
                    }
                }

                launch {
                    homeViewModel.thermometerCookTempList.collectLatest {
                        grillTempListAdapter.updateList(it)
                        snapToSelectedTemperature()
                    }
                }

                launch {
                    homeViewModel.connectionStatusToolbar.collectLatest {
                        binding.cookToolbar.setConnectionStatus(it)
                    }
                }

                launch {
                    homeViewModel.userTemperatureUnit.collectLatest {
                        grillTempListAdapter.updateTemperatureUnit(it)
                    }
                }
            }
        }
    }

    private fun initGrillTemperatureDial() {
        val tempLayoutManager = LinearLayoutManager(requireContext())
        binding.grillTemperatureDial.temperaturesRv.layoutManager = tempLayoutManager
        grillTempListAdapter = GrillTempListAdapter(homeViewModel.thermometerCookTempList.value)
        binding.grillTemperatureDial.temperaturesRv.adapter = grillTempListAdapter

        grillTempListAdapter.setColorTheme(isDarkTheme = true)

        binding.grillTemperatureDial.temperaturesRv.attachSnapHelperWithListener(
            snapHelper = snapHelper,
            behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
            layoutManager = tempLayoutManager,
            onSnapPositionChangeListener = object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    updateTemperatureList(position)
                }
            })
    }

    private fun snapToSelectedTemperature() {
        val desiredTemp = homeViewModel.selectedGrillState.value.oven.desiredTemp
        var index = homeViewModel.thermometerCookTempList.value.indexOfFirst { it.temp == desiredTemp }
        if (index == -1) {
            index = 0
        }
        binding.grillTemperatureDial.temperaturesRv.post {
            binding.grillTemperatureDial.temperaturesRv.scrollToPosition(index)
            updateTemperatureList(index)
        }
    }

    fun updateTemperatureList(selectedPosition: Int) {
        val mList = homeViewModel.thermometerCookTempList.value
        val selectedItem = mList.getOrNull(selectedPosition)
        selectedItem?.let {
            val newList =
                mList.map { it.copy(isSelected = it.tempString == selectedItem.tempString) }
            grillTempListAdapter.updateList(newList)
        }
    }

    private fun onSave() {
        val selectedTemperature = grillTempListAdapter.tempList.firstOrNull { tempItem -> tempItem.isSelected }
        if (selectedTemperature != null) {
            homeViewModel.editThermometerCookSettings(
                grillTempListAdapter.cookMode,
                selectedTemperature.tempString
            )
            dismiss()
        }
    }

    private fun checkForChanges(): Boolean {
        val currentDesiredTemp = homeViewModel.selectedGrillState.value.oven.desiredTemp
        val currentCookMode = homeViewModel.selectedGrillState.value.cookMode
        val selectedDesiredTemp = grillTempListAdapter.tempList.firstOrNull { tempItem -> tempItem.isSelected }?.temp
        val selectedCookMode = binding.menuCookMode.cookMode

        return currentCookMode != selectedCookMode || currentDesiredTemp != selectedDesiredTemp
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
        }.show(parentFragmentManager, CookFragment.COOK_FRAGMENT_TAG)
    }
}