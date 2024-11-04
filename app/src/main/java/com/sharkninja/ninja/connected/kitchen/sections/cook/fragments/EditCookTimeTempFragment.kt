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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.sharkninja.grillcore.CookMode
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.getToolbarTitle
import com.sharkninja.ninja.connected.kitchen.data.models.cookitems.TimerItem
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentCookTimeGrillTempBinding
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.OnSnapPositionChangeListener
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.services.attachSnapHelperWithListener
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.adapters.GrillTempListAdapter
import com.sharkninja.ninja.connected.kitchen.ui.adapters.TimerListAdapter
import com.sharkninja.ninja.connected.kitchen.ui.views.SnapOnScrollListener
import com.sharkninja.ninja.connected.kitchen.ui.views.VerticalTwoButtonPositiveDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditCookTimeTempFragment : DialogFragment() {

    private lateinit var binding: FragmentCookTimeGrillTempBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var grillTempListAdapter: GrillTempListAdapter
    private lateinit var timeListAdapterLeft: TimerListAdapter
    private lateinit var timeListAdapterRight: TimerListAdapter

    var isNavigatingToMiniCharts = false

    private var timeListRight: List<TimerItem>? = null
    private var numberOfLoads = 0

    private val snapHelper by lazy { LinearSnapHelper() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCookTimeGrillTempBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGrillTemperatureRv()
        initTimerRvRight()
        initTimerRvLeft()
        subscribeToVm()
        initCookTip()
        initOnClickListeners()
        checkForCharts()
    }

    override fun getTheme(): Int {
        return R.style.DialogThemeFullScreen
    }

    private fun checkForCharts() {
        if (homeViewModel.checkIfModeHasChart(homeViewModel.cookMode.value)) {
            binding.cookingTip.visibility = View.VISIBLE
        } else {
            binding.cookingTip.visibility = View.GONE
        }
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
                        setTimeUnitLabels(it)
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

    private fun initOnClickListeners() {
        binding.cookToolbar.setNavIconOnClickListener {
            if (checkForChanges()) showGoBackDialog() else findNavController().popBackStack()
        }
        binding.saveChangesButton.setOnClickListener { onSave() }
    }

    private fun initCookTip() {
        binding.cookingTip.setCloseButtonOnClick { hideCookTip() }
        binding.cookingTip.setLinkOnClick {
            isNavigatingToMiniCharts = true
            findNavController().navigate(
                EditCookTimeTempFragmentDirections.editCookTimeTempFragmentToMiniChartsPreview()
            )
        }
    }

    private fun hideCookTip() {
        binding.cookingTip.visibility = View.GONE
    }

    private fun setTimeUnitLabels(cookMode: CookMode) {
        if(cookMode == CookMode.Broil) {
            binding.dialTimer.valueLeftLabel.text = getString(R.string.minutes_label)
            binding.dialTimer.valueRightLabel.text = getString(R.string.seconds_label)
        } else {
            binding.dialTimer.valueLeftLabel.text = getString(R.string.hours_label)
            binding.dialTimer.valueRightLabel.text = getString(R.string.minutes_label)
        }
    }

    private fun initGrillTemperatureRv() {
        val tempLayoutManager = LinearLayoutManager(requireContext())
        binding.grillTemperatureDial.temperaturesRv.layoutManager = tempLayoutManager
        grillTempListAdapter = GrillTempListAdapter(homeViewModel.timeCookTempList.value)
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
        snapToSelectedTemperature()
    }

    private fun initTimerRvLeft() {
        val timeLayoutManager = LinearLayoutManager(requireContext())
        binding.dialTimer.timerValueLeftRv.layoutManager = timeLayoutManager
        timeListAdapterLeft = TimerListAdapter(homeViewModel.timerMap.value.keys.toList())
        binding.dialTimer.timerValueLeftRv.adapter = timeListAdapterLeft
        binding.dialTimer.timerValueLeftRv.attachSnapHelperWithListener(
            snapHelper = snapHelper,
            behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
            layoutManager = timeLayoutManager,
            onSnapPositionChangeListener = object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    numberOfLoads += 1
                    updateTimeListLeftWithSelectedPositionChange(position)
                }
            })
        snapToSelectedTimerNumberLeft()
    }

    private fun initTimerRvRight() {
        val timeLayoutManagerRight = LinearLayoutManager(requireContext())
        binding.dialTimer.timerValueRightRv.layoutManager = timeLayoutManagerRight
        timeListAdapterRight = TimerListAdapter(emptyList())
        binding.dialTimer.timerValueRightRv.adapter = timeListAdapterRight
        binding.dialTimer.timerValueRightRv.attachSnapHelperWithListener(
            snapHelper = snapHelper,
            behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
            layoutManager = timeLayoutManagerRight,
            onSnapPositionChangeListener = object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    updateTimeListRightWithSelectedPositionChange(position)
                }
            })
    }


    fun updateTemperatureList(selectedPosition: Int) {
        val mList = homeViewModel.timeCookTempList.value
        val selectedItem = mList.getOrNull(selectedPosition)
        selectedItem?.let {
            val newList = mList.map { it.copy(isSelected = it.tempString == selectedItem.tempString) }
            grillTempListAdapter.updateList(newList)
        }
    }

    fun updateTimeListLeftWithSelectedPositionChange(selectedPosition: Int) {
        val mList = homeViewModel.timerMap.value.keys.toList()
        val selectedItem = mList.getOrNull(selectedPosition)
        selectedItem?.let { timerItem ->
            timeListRight = homeViewModel.timerMap.value[timerItem]
            val newList = mList.map { it.copy(isSelected = it.time == selectedItem.time) }
            timeListAdapterLeft.updateList(newList)
            snapToSelectedTimerNumberRight()
        }
    }

    fun updateTimeListRightWithSelectedPositionChange(selectedPosition: Int) {

        val selectedItem = timeListRight?.getOrNull(selectedPosition)
        selectedItem?.let {
            val newList = timeListRight!!.map {
                it.copy(isSelected = it.time == selectedItem.time)
            }
            timeListAdapterRight.updateList(newList)
        }
    }


    private fun snapToSelectedTemperature() {
        val currentTemp = homeViewModel.preCookDashboardUiState.value.grillTemp
        val index = homeViewModel.timeCookTempList.value.indexOfFirst { it.tempString == currentTemp}

        binding.grillTemperatureDial.temperaturesRv.post {
            binding.grillTemperatureDial.temperaturesRv.scrollToPosition(index)
            updateTemperatureList(index)
        }
    }

    private fun snapToSelectedTimerNumberLeft() {
        val durationToSelect = homeViewModel.editScreenDuration.value.valueLeft ?: homeViewModel.editScreenDuration.value.defaultLeft
        var index = homeViewModel.timerMap.value.keys.toList().indexOfFirst { it.displayTimeString.toInt() == durationToSelect }

        binding.dialTimer.timerValueLeftRv.post {
            updateTimeListLeftWithSelectedPositionChange(index)
            binding.dialTimer.timerValueLeftRv.scrollToPosition(index)
        }
    }

    private fun snapToSelectedTimerNumberRight() {
        var index = 0
        val durationToSelect = homeViewModel.editScreenDuration.value.valueRight ?: homeViewModel.editScreenDuration.value.defaultRight

        timeListRight?.let { timeList ->
                index = timeList.indexOfFirst { it.time == durationToSelect }
            }
        if(index == -1) index = 0


        binding.dialTimer.timerValueRightRv.post {
            binding.dialTimer.timerValueRightRv.scrollToPosition(index)
            updateTimeListRightWithSelectedPositionChange(index)
        }
    }

    private fun onSave() {
        val selectedTemperature = grillTempListAdapter.tempList.firstOrNull { tempItem -> tempItem.isSelected}
        val selectedTimeLeft = timeListAdapterLeft.timerList.firstOrNull { timeItem -> timeItem.isSelected}
        val selectedTimeRight = timeListAdapterRight.timerList.firstOrNull { timeItem -> timeItem.isSelected}
        if (selectedTemperature != null && selectedTimeLeft != null && selectedTimeRight != null) {
            homeViewModel.saveTimeCookGrillTimeAndTemp(selectedTimeLeft.time, selectedTimeRight.time, selectedTemperature.tempString)
            dismiss()
        }
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
        }.show(parentFragmentManager, COOK_TIME_GRILL_TEMP_TAG)
    }

    private fun checkForChanges(): Boolean {
        val currentTemp = homeViewModel.preCookDashboardUiState.value.grillTemp
        val currentTime = homeViewModel.preCookDashboardUiState.value.duration
        val selectedTemperature = grillTempListAdapter.tempList.firstOrNull { tempItem -> tempItem.isSelected}?.tempString
        val selectedTimeLeft = timeListAdapterLeft.timerList.firstOrNull { timeItem -> timeItem.isSelected }?.time
        val selectedTimeRight = timeListAdapterRight.timerList.firstOrNull { timeItem -> timeItem.isSelected }?.time
        val duration = if(selectedTimeLeft != null && selectedTimeRight != null) {
            selectedTimeLeft + selectedTimeRight
        } else null
       return currentTime != duration || currentTemp != selectedTemperature
    }
    override fun onDestroy() {
        super.onDestroy()
        if(isNavigatingToMiniCharts.not()) {
            (requireActivity() as HomeActivity).showBottomTabNavigation()
        }
    }

    companion object {
        const val COOK_TIME_GRILL_TEMP_TAG = "COOK_TIME_GRILL_TEMP_FRAGMENT"
    }
}