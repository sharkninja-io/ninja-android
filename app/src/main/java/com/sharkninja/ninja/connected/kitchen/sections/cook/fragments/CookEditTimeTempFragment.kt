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
import com.sharkninja.grillcore.CookMode
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.getToolbarTitle
import com.sharkninja.ninja.connected.kitchen.data.models.Duration
import com.sharkninja.ninja.connected.kitchen.data.models.cookitems.TimerItem
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentCookEditTimeTempBinding
import com.sharkninja.ninja.connected.kitchen.ext.logBreadCrumbClickEvent
import com.sharkninja.ninja.connected.kitchen.ext.toSplitDurationMC
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.OnSnapPositionChangeListener
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.services.attachSnapHelperWithListener
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.adapters.GrillTempListAdapter
import com.sharkninja.ninja.connected.kitchen.ui.adapters.TimerListAdapter
import com.sharkninja.ninja.connected.kitchen.ui.views.DarkThemeDialog
import com.sharkninja.ninja.connected.kitchen.ui.views.SnapOnScrollListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CookEditTimeTempFragment : DialogFragment() {

    private lateinit var binding: FragmentCookEditTimeTempBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var grillTempListAdapter: GrillTempListAdapter
    private lateinit var timeListAdapterLeft: TimerListAdapter
    private lateinit var timeListAdapterRight: TimerListAdapter

    private val snapHelper by lazy { LinearSnapHelper() }
    private var splitDuration: Duration = Duration()
    private var timeListRight: List<TimerItem>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCookEditTimeTempBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGrillTemperatureRv()
        initTimerRvRight()
        initTimerRvLeft()
        subscribeToVm()
        initBindings()
        initOnClickListeners()
    }

    override fun getTheme(): Int {
        return R.style.DialogThemeFullScreenDarkTheme
    }
    private fun initBindings() {
        binding.menuCookSettings.setCookModeMenuOptions(homeViewModel.isUSGrill())

        binding.menuCookSettings.calculateSpacingForHorizontalModeSelector(
            homeViewModel.density,
            homeViewModel.xPix
        )
        grillTempListAdapter.cookMode = homeViewModel.selectedGrillState.value.cookMode
        homeViewModel.selectDashboardCookMode(homeViewModel.selectedGrillState.value.cookMode)
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
                        binding.menuCookSettings.apply {
                            cookMode = it
                        }
                    }
                }

                launch {
                    homeViewModel.timerMap.collectLatest {
                        timeListAdapterLeft.updateList(it.keys.toList())
                        snapToSelectedTimerNumberLeft()
                    }
                }

                launch {
                    homeViewModel.timeCookTempList.collectLatest {
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

    private fun initOnClickListeners() {
        binding.cookToolbar.setNavIconOnClickListener {
            if(checkForChanges()) showGoBackDialog() else dismiss()
        }
        binding.saveChangesButton.setOnClickListener {
            logBreadCrumbClickEvent("UPDATE TIME/TEMP")
            onSave()
        }
        binding.menuCookSettings.setMenuItemOnClicksListeners {
            grillTempListAdapter.cookMode = it
            homeViewModel.selectDashboardCookMode(it)
        }
    }

    private fun initGrillTemperatureRv() {
        val tempLayoutManager = LinearLayoutManager(requireContext())
        binding.grillTemperatureDial.temperaturesRv.layoutManager = tempLayoutManager
        grillTempListAdapter = GrillTempListAdapter(homeViewModel.timeCookTempList.value)
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

    private fun initTimerRvLeft() {
        val timeLayoutManager = LinearLayoutManager(requireContext())
        binding.dialTimer.timerValueLeftRv.layoutManager = timeLayoutManager
        timeListAdapterLeft = TimerListAdapter(homeViewModel.timerMap.value.keys.toList())
        binding.dialTimer.timerValueLeftRv.adapter = timeListAdapterLeft

        timeListAdapterLeft.setColorTheme(isDarkTheme = true)

        binding.dialTimer.timerValueLeftRv.attachSnapHelperWithListener(
            snapHelper = snapHelper,
            behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
            layoutManager = timeLayoutManager,
            onSnapPositionChangeListener = object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    updateTimeListLeftWithSelectedPosition(position)
                }
            })
    }

    private fun initTimerRvRight() {
        val timeLayoutManager = LinearLayoutManager(requireContext())
        binding.dialTimer.timerValueRightRv.layoutManager = timeLayoutManager
        timeListAdapterRight = TimerListAdapter(emptyList())
        binding.dialTimer.timerValueRightRv.adapter = timeListAdapterRight

        timeListAdapterRight.setColorTheme(isDarkTheme = true)

        binding.dialTimer.timerValueRightRv.attachSnapHelperWithListener(
            snapHelper = snapHelper,
            behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
            layoutManager = timeLayoutManager,
            onSnapPositionChangeListener = object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    updateTimeListRightWithSelectedPosition(position)
                }
            })
    }


    fun updateTemperatureList(selectedPosition: Int) {
        val mList = homeViewModel.timeCookTempList.value
        val selectedItem = mList.getOrNull(selectedPosition)
        selectedItem?.let {
            val newList =
                mList.map { it.copy(isSelected = it.tempString == selectedItem.tempString) }
            grillTempListAdapter.updateList(newList)
        }
    }

    fun updateTimeListLeftWithSelectedPosition(selectedPosition: Int) {
        val mList = homeViewModel.timerMap.value.keys.toList()
        val selectedItem = mList.getOrNull(selectedPosition)
        selectedItem?.let {timerItem ->
            timeListRight = homeViewModel.timerMap.value[timerItem]
            val newList = mList.map { it.copy(isSelected = it.time == selectedItem.time) }
            timeListAdapterLeft.updateList(newList)
            snapToSelectedTimerNumberRight()
        }
    }

    fun updateTimeListRightWithSelectedPosition(selectedPosition: Int) {
        timeListRight?.let { list ->
            val selectedItem = list.getOrNull(selectedPosition)
            selectedItem?.let {
                val newList = list.map { it.copy(isSelected = it.time == selectedItem.time) }
                timeListAdapterRight.updateList(newList)
            }
        }
    }

    private fun snapToSelectedTemperature() {
        val desiredTemp = homeViewModel.selectedGrillState.value.oven.desiredTemp
        var index = homeViewModel.timeCookTempList.value.indexOfFirst { it.temp == desiredTemp }
        if (index == -1) {
            index = 0
        }
        binding.grillTemperatureDial.temperaturesRv.post {
            binding.grillTemperatureDial.temperaturesRv.scrollToPosition(index)
            updateTemperatureList(index)
        }
    }

    private fun snapToSelectedTimerNumberLeft() {
        val cookMode = homeViewModel.cookMode.value
        splitDuration = homeViewModel.selectedGrillState.value.oven.timeSet.toSplitDurationMC(cookMode)
        var index = homeViewModel.timerMap.value.keys.toList().indexOfFirst { it.displayTimeString == splitDuration.defaultLeft.toString() }

        if (index == -1) index = 0

        if(cookMode == CookMode.Broil) {
            binding.dialTimer.valueLeftLabel.text = getString(R.string.minutes_label)
            binding.dialTimer.valueRightLabel.text = getString(R.string.seconds_label)
        } else {
            binding.dialTimer.valueLeftLabel.text = getString(R.string.hours_label)
            binding.dialTimer.valueRightLabel.text = getString(R.string.minutes_label)
        }

        binding.dialTimer.timerValueLeftRv.post {
            binding.dialTimer.timerValueLeftRv.scrollToPosition(index)
            updateTimeListLeftWithSelectedPosition(index)
        }
    }

    private fun snapToSelectedTimerNumberRight() {
        var index = timeListRight?.let { it.indexOfFirst { item -> item.time == splitDuration.defaultRight } } ?: 0

        if (index == -1) {
            index = 0
        }

        binding.dialTimer.timerValueRightRv.post {
            binding.dialTimer.timerValueRightRv.scrollToPosition(index)
            updateTimeListRightWithSelectedPosition(index)
        }
    }

    private fun onSave() {
        val selectedTemperature = grillTempListAdapter.tempList.firstOrNull { tempItem -> tempItem.isSelected }
        val selectedTimeLeft = timeListAdapterLeft.timerList.firstOrNull { timeItem -> timeItem.isSelected }
        val selectedTimeRight = timeListAdapterRight.timerList.firstOrNull { timeItem -> timeItem.isSelected }
        if (selectedTemperature != null && selectedTimeLeft != null && selectedTimeRight != null) {
            val duration = selectedTimeLeft.time + selectedTimeRight.time
            homeViewModel.editTimeCook(
                binding.menuCookSettings.cookMode,
                selectedTemperature.tempString,
                duration
            )
            dismiss()
        }
    }

    private fun checkForChanges(): Boolean {
        val currentDesiredTemp = homeViewModel.selectedGrillState.value.oven.desiredTemp
        val currentCookMode = homeViewModel.selectedGrillState.value.cookMode
        val selectedDesiredTemp = grillTempListAdapter.tempList.firstOrNull { tempItem -> tempItem.isSelected }?.temp
        val selectedCookMode = binding.menuCookSettings.cookMode
        val selectedTimerValueLeft = timeListAdapterLeft.timerList.firstOrNull { it.isSelected }?.displayTimeString?.toInt()
        val selectedTimerValueRight = timeListAdapterRight.timerList.firstOrNull { it.isSelected }?.time

        return currentCookMode != selectedCookMode || currentDesiredTemp != selectedDesiredTemp
                || selectedTimerValueLeft != splitDuration.defaultLeft || selectedTimerValueRight != splitDuration.defaultRight
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
        }.show(parentFragmentManager, COOK_TIME_GRILL_TEMP_TAG)

    }

    companion object {
        const val COOK_TIME_GRILL_TEMP_TAG = "COOK_TIME_GRILL_TEMP_FRAGMENT"
    }
}