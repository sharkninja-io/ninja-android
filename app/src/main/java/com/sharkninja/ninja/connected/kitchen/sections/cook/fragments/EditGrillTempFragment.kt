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
import com.sharkninja.ninja.connected.kitchen.data.enums.getToolbarTitle
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentGrillTempBinding
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.OnSnapPositionChangeListener
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.services.attachSnapHelperWithListener
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.adapters.GrillTempListAdapter
import com.sharkninja.ninja.connected.kitchen.ui.views.SnapOnScrollListener
import com.sharkninja.ninja.connected.kitchen.ui.views.VerticalTwoButtonPositiveDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditGrillTempFragment : DialogFragment() {

    private lateinit var binding: FragmentGrillTempBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var grillTempListAdapter: GrillTempListAdapter
    private val tempLayoutManager by lazy { LinearLayoutManager(requireContext()) }
    private val snapHelper by lazy { LinearSnapHelper() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGrillTempBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initClickListeners()
        initGrillTemperatureDial()
        subscribeToVm()
    }

    override fun getTheme(): Int {
        return R.style.DialogThemeFullScreen
    }

    private fun initToolbar() {
        binding.cookToolbar.setNavIconOnClickListener {
            dismiss()
        }
    }

    private fun initClickListeners() {
        binding.cookToolbar.setNavIconOnClickListener {
            if (checkForChanges()) showGoBackDialog() else dismiss()
        }
        binding.saveButton.setOnClickListener { onSave() }
    }


    private fun subscribeToVm() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    homeViewModel.userTemperatureUnit.collectLatest {
                        grillTempListAdapter.updateTemperatureUnit(it)
                    }
                }

                launch {
                    homeViewModel.cookMode.collectLatest {
                        binding.cookToolbar.setEditScreenToolbarTitle(it.getToolbarTitle(requireContext()))
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
            }
        }
    }

    private fun initGrillTemperatureDial() {
        binding.grillTemperatureDial.temperaturesRv.layoutManager = tempLayoutManager
        grillTempListAdapter = GrillTempListAdapter(homeViewModel.thermometerCookTempList.value)
        binding.grillTemperatureDial.temperaturesRv.adapter = grillTempListAdapter
        grillTempListAdapter.cookMode = homeViewModel.cookMode.value

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
        val grillTemp = homeViewModel.preCookDashboardUiState.value.grillTemp
        val index = homeViewModel.thermometerCookTempList.value.indexOfFirst { it.tempString == grillTemp }
        binding.grillTemperatureDial.temperaturesRv.post {
            binding.grillTemperatureDial.temperaturesRv.scrollToPosition(index)
            updateTemperatureList(index)
        }
    }

    fun updateTemperatureList(selectedPosition: Int) {
        val mList = homeViewModel.thermometerCookTempList.value
        val selectedItem = mList.getOrNull(selectedPosition)
        selectedItem?.let {
            val newList = mList.map { it.copy(isSelected = it.tempString == selectedItem.tempString) }
            grillTempListAdapter.updateList(newList)
        }
    }

    private fun onSave() {
        val selectedTemperature = grillTempListAdapter.tempList.firstOrNull { tempItem -> tempItem.isSelected}
        if (selectedTemperature != null) {
            homeViewModel.saveThermometerCookGrillTemp(selectedTemperature.tempString)
            dismiss()
        }
    }

    private fun checkForChanges(): Boolean {
        val currentTemp = homeViewModel.preCookDashboardUiState.value.grillTemp
        val selectedTemperature = grillTempListAdapter.tempList.firstOrNull { tempItem -> tempItem.isSelected}
        return currentTemp != selectedTemperature?.tempString
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
        }.show(parentFragmentManager, EditCookTimeTempFragment.COOK_TIME_GRILL_TEMP_TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as HomeActivity).showBottomTabNavigation()
    }

}