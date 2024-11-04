package com.sharkninja.ninja.connected.kitchen.sections.cook.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.grillcore.CalculatedState
import com.sharkninja.grillcore.CookMode
import com.sharkninja.grillcore.CookType
import com.sharkninja.grillcore.Grill
import com.sharkninja.grillcore.GrillState
import com.sharkninja.grillcore.GrillThermometer
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.COLD_SMOKE_DISPLAY
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.COLD_SMOKE_TEMP_FAREN
import com.sharkninja.ninja.connected.kitchen.data.enums.*
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardInfoAction.ActionProcessed
import com.sharkninja.ninja.connected.kitchen.data.models.grillOfflineNotification
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentCookBinding
import com.sharkninja.ninja.connected.kitchen.databinding.LayoutProgressBarDialogBinding
import com.sharkninja.ninja.connected.kitchen.ext.canGoBackFromCookScreen
import com.sharkninja.ninja.connected.kitchen.ext.getDesiredTemp
import com.sharkninja.ninja.connected.kitchen.ext.hide
import com.sharkninja.ninja.connected.kitchen.ext.logBreadCrumbClickEvent
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.ext.setIconTintColor
import com.sharkninja.ninja.connected.kitchen.ext.shouldNavigateFromCookToPrecook
import com.sharkninja.ninja.connected.kitchen.ext.show
import com.sharkninja.ninja.connected.kitchen.ext.toTimeDisplay
import com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs.COUNT_DOWN_INTERVAL
import com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs.COUNT_DOWN_LENGTH
import com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs.SwitchToThermCookDialog
import com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs.SwitchToTimedCookDialog
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.TextGradients
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters.AllPermissionsChecker
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.views.DarkThemeDialog
import com.sharkninja.ninja.connected.kitchen.ui.views.DialState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription


class CookFragment : Fragment() {

    private lateinit var binding: FragmentCookBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private val allPermissionsChecker = AllPermissionsChecker(this)

    private var levelDrawable: Drawable? = null

    private var selectedThermometerBackground: Int = R.drawable.preheat_thermometer_selector_background_plugged_in
    private var unselectedThermometerBackground: Int = R.drawable.thermometer_selector_background_un_plugged

    private val switchToThermCookDialog by lazy { SwitchToThermCookDialog() }
    private val switchToTimedCookDialog by lazy { SwitchToTimedCookDialog() }
    private val editWoodFireSettingLoadingDialog by lazy { Dialog(requireContext()) }

    private val notificationTimer by lazy {
        object: CountDownTimer(COUNT_DOWN_LENGTH, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                binding.notificationBanner.root.visibility = View.GONE
                binding.toolbar.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCookBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.isNavigatingPreCookToCook = false
        homeViewModel.resetBannerEvent()
        initBindings()
        subscribeToVM()
        initOnClickedListeners()
        setInitialGradient()
        overrideBackPressed()
    }

    private fun initBindings() {
        setInitialToolbarState()
        homeViewModel.observeCookBannerNotifications()
        homeViewModel.updatePermissionsGranted(allPermissionsChecker.areAllPermissionsGranted(requireContext()))
        binding.switchTabSelection.setOnToggleSwitchChangeListener {
            val currentTab = binding.switchTabSelection.getCurrentTab()
            if(it != currentTab) {
                if(it == CookDashboardTabs.Thermometer) {
                    if(homeViewModel.isCookModeUnavailableForThermCook() ) {
                        showNotSupportedDialog()
                    } else {
                        showSwitchToThermCookDialog()
                    }
                } else {
                    showSwitchToTimedCookDialog()
                }
            }
        }
    }
    private fun removeClickListenersOnDone() {
        binding.probeOneCard.removeClickListener()
        binding.probeTwoCard.removeClickListener()
        binding.editTempCardView.removeClickListener()
        binding.editTimeTempCardView.removeClickListener()
        binding.cookModeCardView.removeClickListener()
        binding.woodFireCardView.removeCheckChangeListener()
    }

    private fun setInitialToolbarState() {
        binding.switchTabSelection.setChecked(homeViewModel.dashboardTabSelectedState.value)
    }

    private fun overrideBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val currentState = homeViewModel.selectedGrillState.value.state
                    if(currentState.canGoBackFromCookScreen()) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    private fun subscribeToVM() {
        homeViewModel.cookDashboardUiState.onEach {
            updateColorsAndBackground(it)
            updateDashboardViews(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.grills.onSubscription {
            homeViewModel.getCachedGrills()
        }.onEach {
            setupDropdownAppliancePicker(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.selectedGrillState.onEach {
            checkGrillActivity(it.state)
            setUpCardValues(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.connectionStatusToolbar.onEach {
            binding.toolbar.setConnectionStatus(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.infoAction.onEach {
            checkInfoAction(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.bannerEvent.onEach {
            checkBannerEvent(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.isDevicePushEnabled.onEach {
            homeViewModel.observeCookBannerNotifications()
//            if(it) homeViewModel.observeCookBannerNotifications()
//            else homeViewModel.cancelCookBannerNotificationJob()
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.cookTypeTabState.onEach {
            binding.switchTabSelection.apply {
                tabState = it
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.userTemperatureUnit.onEach {
            setTempUnit(getString(it.getDisplayName()))
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.localNotificationEvent.onEach {
            checkLocalNotificationEvent(it)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

    }

    private fun updateColorsAndBackground(cookDashboardUi: CookUiState) {
        //selected tab background
        binding.switchTabSelection.apply {
            drawableSelected = cookDashboardUi.selectedCookTypeTabBackground
        }
        // set background drawable
        levelDrawable = ContextCompat.getDrawable(requireContext(), cookDashboardUi.levelListBackground)
        transitionBackground(cookDashboardUi.progress)
        //set selected thermometer background
        selectedThermometerBackground = cookDashboardUi.selectedThermometerTabDrawableBackground
        unselectedThermometerBackground = cookDashboardUi.unselectedThermometerTabDrawableBackground
        // set miniThermTextAndIconColors
        setMiniThermometerTextAndIconColor(if(cookDashboardUi.isOnline) R.color.mc_text_and_icon_color else R.color.dashboard_tab_unselected_offline)

        updateDialStateAndProgress(cookDashboardUi)
    }

    private fun updateSkipButton(isButtonVisible: Boolean) {
        binding.skipButton.isVisible = isButtonVisible
        var textShader: Shader? = null
        val paint = binding.skipButton.paint
        val textSize = binding.skipButton.textSize

        if(isButtonVisible) {
            val background =
            if(homeViewModel.isPreheating()) {
                binding.skipButton.text = getString(R.string.skip_preheat)
                val width = paint.measureText(binding.skipButton.text.toString())
                textShader = TextGradients().getPreheatGradient(width, textSize)
                R.drawable.skip_preheat_button
            } else if(homeViewModel.isGrillIgniting()) {
                binding.skipButton.text = getString(R.string.skip_ignition)
                val width = paint.measureText(binding.skipButton.text.toString())
                val skipButtonTextSize = binding.skipButton.textSize
                textShader = TextGradients().getCookGradient(width, skipButtonTextSize)
                R.drawable.skip_ignition_button

            } else null

            background?.let {
                binding.skipButton.background = ContextCompat.getDrawable(requireContext(), it)
                binding.skipButton.paint.shader = textShader
                val typeface = ResourcesCompat.getFont(requireContext(), R.font.gotham_book_medium)
                binding.skipButton.typeface = typeface
            }
        }
    }

    private fun updateDashboardViews(cookDashboardUi: CookUiState) {
        updateSkipButton(cookDashboardUi.skipPreheatButtonVisible)
        binding.dialOne.progressValueSub.visibility = if(cookDashboardUi.progressSubVisibility) View.VISIBLE else View.GONE
        binding.dialOne.progressState.visibility = if(cookDashboardUi.showDialState) View.VISIBLE else View.GONE
        val grillState = homeViewModel.selectedGrillState.value.state
        if(cookDashboardUi.dialState == DialState.AddFood || cookDashboardUi.dialState == DialState.LidOpen) {
            binding.dialOne.progressValue.visibility = View.GONE
            binding.dialOne.progressAction.visibility = View.VISIBLE
        } else if(grillState == CalculatedState.FlipFood) {
            binding.dialOne.progressValue.visibility = View.GONE
            binding.dialOne.progressState.visibility = View.GONE

            binding.dialOne.progressAction.visibility = View.VISIBLE
        }
        else {
            binding.dialOne.progressValue.visibility = View.VISIBLE
            binding.dialOne.progressState.visibility = if(cookDashboardUi.showDialState) View.VISIBLE else View.GONE

            binding.dialOne.progressAction.visibility = View.GONE
        }

        binding.cookModeCardView.apply {
            isViewEnabled =  isViewEnabled.copy(
                isOnline = cookDashboardUi.isOnline
            )
        }

        if(cookDashboardUi.isTimedCook) {
            binding.switchTabSelection.setChecked(CookDashboardTabs.TimeCook)

            binding.editTimeTempCardView.apply {
                isViewEnabled =  isViewEnabled.copy(
                    isOnline = cookDashboardUi.isOnline
                )
            }

            showTimeCookDashboardTab()
        } else {
            binding.switchTabSelection.setChecked(CookDashboardTabs.Thermometer)

            binding.editTempCardView.apply {
                isViewEnabled =  isViewEnabled.copy(
                    isOnline = cookDashboardUi.isOnline
                )
            }

            showThermometerDashboardTab()
            // show selected probe card
            binding.probeOneCard.apply { isViewEnabled = isViewEnabled.copy(
                isOnline = cookDashboardUi.isOnline,
                isEnabled = cookDashboardUi.probeOneCardEnabled
            ) }
            binding.probeTwoCard.apply { isViewEnabled = isViewEnabled.copy(
                isOnline = cookDashboardUi.isOnline,
                isEnabled = cookDashboardUi.probeTwoCardEnabled
            ) }

            binding.probeOneCard.visibility = if(cookDashboardUi.isThermometer2Selected) View.GONE else View.VISIBLE
            binding.probeTwoCard.visibility = if(cookDashboardUi.isThermometer2Selected) View.VISIBLE else View.GONE

            // set mini probe selection with backgrounds
            val miniProbeOneBackground = if(cookDashboardUi.isThermometer2Selected) unselectedThermometerBackground else selectedThermometerBackground
            val miniProbeTwoBackground = if(cookDashboardUi.isThermometer2Selected) selectedThermometerBackground else unselectedThermometerBackground
            binding.thermCookMiniThermometers.thermometerOne.setBackgroundResource(miniProbeOneBackground)
            binding.thermCookMiniThermometers.thermometerTwo.setBackgroundResource(miniProbeTwoBackground)

        }

            setCookDashboardActionButton(cookDashboardUi.cookComplete)


    }

    private fun updateDialStateAndProgress(cookDashboardUi: CookUiState) {
        setDialState(cookDashboardUi.dialState)
        binding.dialOne.ninjaProgressDial.setProgress(cookDashboardUi.progress.toFloat())
        binding.dialOne.progressValue.text = cookDashboardUi.progressDisplayValue?.getFormattedString(requireContext())
        binding.dialOne.progressAction.text = cookDashboardUi.progressDisplayValue?.getFormattedString(requireContext())
        binding.dialOne.progressValueSub.text = getString(cookDashboardUi.progressSubText)
    }

    private fun checkGrillActivity(state: CalculatedState) {
        if(state.shouldNavigateFromCookToPrecook(homeViewModel.isNavigatingCookToPreCook)) {
            findNavController().safeNavigate(CookFragmentDirections.actionToPreCook())
        }
    }

    private fun setUpCardValues(grillState: GrillState) {
        updateCookCardValues(grillState)
        setThermCellValues(grillState)
        setUpWoodFireCard(grillState)
        setCookModeCardView(grillState)
    }

    private fun setCookModeCardView(grillState: GrillState) {
        binding.cookModeCardView.setRightIconAndLabelForCookMode(grillState.cookMode)
    }

    private fun setUpWoodFireCard(grillState: GrillState) {
        if(editWoodFireSettingLoadingDialog.isShowing.not()) {
            binding.woodFireCardView.setWoodfireStateMonitorAndControl(
                grillState.cookMode,
                grillState.woodFire,
                grillState.state,
                grillState.connectedToInternet || grillState.connectedToBluetooth
            )
        }
    }

    private fun updateCookCardValues(grillState: GrillState) {
        // timed cook cards

        val desiredTemp = grillState.getDesiredTemp(requireContext(), homeViewModel.coldSmokeTemp())
        binding.editTimeTempCardView.setCardValues(
            valueLeft = grillState.oven.timeSet.toTimeDisplay(grillState.cookMode, false),
            valueRight = desiredTemp
        )

        if (grillState.cookMode == CookMode.Smoke && desiredTemp == COLD_SMOKE_DISPLAY) {
            binding.editTimeTempCardView.showUnitRight(false)
        } else {
            binding.editTimeTempCardView.showUnitRight(grillState.cookMode != CookMode.Grill)
        }

        binding.probeOneCard.showUnitLeft(false)
        val probeOneCardValue = if(grillState.probe1.active.not()) {
            binding.probeOneCard.showUnitRight(false)
            getString(R.string.double_dash)
        } else {
            binding.probeOneCard.showUnitRight(true)
            grillState.probe1.desiredTemp.toString()
        }
        binding.probeOneCard.setCardValues(
            valueLeft = grillState.probe1.food.doneness.name,
            valueRight = probeOneCardValue
        )

        //therm cook cards
        binding.probeTwoCard.showUnitLeft(false)
        val probeTwoCardValue = if(grillState.probe2.active.not()) {
            binding.probeTwoCard.showUnitRight(false)
            getString(R.string.double_dash)
        } else {
            binding.probeTwoCard.showUnitRight(true)
            grillState.probe2.desiredTemp.toString()
        }
        binding.probeTwoCard.setCardValues(
            valueLeft = grillState.probe2.food.doneness.name,
            valueRight = probeTwoCardValue
        )

        binding.probeOneCard.showTargetTempProbeCook(grillState.probe1.state == CalculatedState.Resting || grillState.probe1.state == CalculatedState.GetFood)
        binding.probeTwoCard.showTargetTempProbeCook(grillState.probe2.state == CalculatedState.Resting || grillState.probe2.state == CalculatedState.GetFood)

        if (grillState.cookMode == CookMode.Smoke && desiredTemp == COLD_SMOKE_TEMP_FAREN)
            binding.editTempCardView.showUnitLeft(false)
        else
            binding.editTempCardView.showUnitLeft(grillState.cookMode != CookMode.Grill)

        binding.editTempCardView.setCardValues(
            valueLeft = desiredTemp,
            valueRight = null
        )
        binding.cookModeCardView.showGrillState(grillState.state == CalculatedState.Resting)
    }
    private fun setDialState(dialState: DialState) {
        val isOfflineTimedAndCooking = homeViewModel.cookDashboardUiState.value.isTimedCook && homeViewModel.selectedGrillState.value.state == CalculatedState.Cooking
        val dialStateText = if(isOfflineTimedAndCooking) {
            getString(R.string.dial_state_offline_timed_cooking)
        } else {
            getString(dialState.dialSubText).uppercase()
        }
        binding.dialOne.ninjaProgressDial.setDialState(dialState)
        binding.dialOne.progressState.text = dialStateText
        binding.dialOne.progressAction.text = dialStateText


        val width = binding.dialOne.progressState.paint.measureText(
            dialStateText
        )
        val textSize = binding.dialOne.progressState.textSize

      val shader =  when(dialState) {
            DialState.Preheating,
            DialState.Ignition -> { TextGradients().getPreheatGradient(width, textSize) }
            DialState.Cooking -> { TextGradients().getCookGradient(width, textSize) }
            DialState.Resting-> { TextGradients().getRestGradient(width, textSize) }
            DialState.Complete -> {
                TextGradients().getDoneGradient(width, textSize)
            }
          DialState.Offline -> {
              if(isOfflineTimedAndCooking) {
                  TextGradients().getCookGradient(width, textSize)
              } else {
                  TextGradients().getErrorGradient(width, textSize)
              }
          }
            else -> null
        }

        shader?.let {
            binding.dialOne.progressState.paint.shader = it
        }
    }

    private fun transitionBackground(percent: Int) {
                    val startDrawable = binding.cookSettingsContainer.background
                    levelDrawable?.level = percent
                    val endDrawable = levelDrawable

                    val backgroundTransitionArray = arrayOf(startDrawable, endDrawable)
                    val transitionDrawable = TransitionDrawable(backgroundTransitionArray)

                    binding.cookSettingsContainer.background = transitionDrawable
                    transitionDrawable.startTransition(500)
                    transitionDrawable.isCrossFadeEnabled = false
    }

    private fun setCookDashboardActionButton(isCookComplete: Boolean) {
        if(isCookComplete) {
            removeClickListenersOnDone()
            binding.buttonStopCooking.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.ninja_green))
            binding.buttonStopCooking.text = getString(R.string.back_to_dashboard)
            binding.buttonStopCooking.setOnClickListener {
                logBreadCrumbClickEvent("STOP COOKING")
                homeViewModel.stopCooking()
            }
        } else {
            binding.buttonStopCooking.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.ninja_button_orange_background))
            binding.buttonStopCooking.text = getString(R.string.stop_cooking)
            binding.buttonStopCooking.setOnClickListener {
                findNavController().safeNavigate(CookFragmentDirections.actionShowProgressBar())
                homeViewModel.stopCooking()
            }
        }
    }

    private fun showTimeCookDashboardTab() {
        binding.editTempCardView.hide()
        binding.editTimeTempCardView.show()
        binding.probeOneCard.hide()
        binding.probeTwoCard.hide()
        binding.thermCookMiniThermometers.root.hide()

        binding.cookModeCardView.setOnCardContainerOnClick {
            findNavController().safeNavigate(CookFragmentDirections.actionCookFragmentToEditTimeTempFragment())
        }
    }

    private fun showThermometerDashboardTab() {
        binding.labelThermometers.show()
        binding.editTimeTempCardView.hide()
        binding.editTempCardView.show()
        binding.timedCookMiniThermometers.root.hide()
        binding.thermCookMiniThermometers.root.show()

        binding.cookModeCardView.setOnCardContainerOnClick {
            findNavController().safeNavigate(CookFragmentDirections.actionCookFragmentToEditGrillTempFragment())
        }
    }

    private fun setThermCellValues(grillState: GrillState) {
        binding.thermCookMiniThermometers.thermometerOneTemp.text = if (grillState.probe1.pluggedIn) grillState.probe1.currentTemp.toString() else getString(R.string.double_dash)
        binding.thermCookMiniThermometers.thermometerOneText.text = getString(grillState.probe1.food.protein.getCookDashBoardCardDisplayName())
        binding.thermCookMiniThermometers.unitLeft.visibility = if (grillState.probe1.pluggedIn) View.VISIBLE else View.GONE

        binding.thermCookMiniThermometers.thermometerTwoTemp.text = if (grillState.probe2.pluggedIn) grillState.probe2.currentTemp.toString() else getString(R.string.double_dash)
        binding.thermCookMiniThermometers.thermometerTwoText.text = getString(grillState.probe2.food.protein.getCookDashBoardCardDisplayName())
        binding.thermCookMiniThermometers.unitRight.visibility = if (grillState.probe2.pluggedIn) View.VISIBLE else View.GONE

        binding.thermCookMiniThermometers.thermometerOneImage.setImageResource(grillState.probe1.getThermCellIcon())
        binding.thermCookMiniThermometers.thermometerTwoImage.setImageResource(grillState.probe2.getThermCellIcon())

        binding.timedCookMiniThermometers.thermometerOneTemp.text = if (grillState.probe1.pluggedIn) grillState.probe1.currentTemp.toString() else getString(R.string.double_dash)
        binding.timedCookMiniThermometers.thermometerOneText.text = getString(R.string.double_dash)
        binding.timedCookMiniThermometers.unitLeft.visibility = if (grillState.probe1.pluggedIn) View.VISIBLE else View.GONE

        binding.timedCookMiniThermometers.thermometerTwoTemp.text = if (grillState.probe2.pluggedIn) grillState.probe2.currentTemp.toString() else getString(R.string.double_dash)
        binding.timedCookMiniThermometers.thermometerTwoText.text = getString(R.string.double_dash)
        binding.timedCookMiniThermometers.unitRight.visibility = if (grillState.probe2.pluggedIn) View.VISIBLE else View.GONE

        binding.timedCookMiniThermometers.thermometerOneImage.setImageResource(grillState.probe1.getThermCellIcon())
        binding.timedCookMiniThermometers.thermometerTwoImage.setImageResource(grillState.probe2.getThermCellIcon())

        if (grillState.cookType == CookType.Timed) {
            setThermometerCellsVisibilityTimedCook(grillState.probe1.pluggedIn || grillState.probe2.pluggedIn)
        }
    }

    private fun setTempUnit(tempUnit: String) {
        binding.thermCookMiniThermometers.unitLeft.text = tempUnit
        binding.thermCookMiniThermometers.unitRight.text = tempUnit
        binding.timedCookMiniThermometers.unitLeft.text = tempUnit
        binding.timedCookMiniThermometers.unitRight.text = tempUnit
        binding.editTempCardView.setUnitLeft(tempUnit)
        binding.editTimeTempCardView.setUnitRight(tempUnit)
        binding.probeOneCard.setUnitRight(tempUnit)
        binding.probeTwoCard.setUnitRight(tempUnit)

    }


    private fun setThermometerCellsVisibilityTimedCook(isVisible: Boolean) {
        if (isVisible) {
            binding.labelThermometers.visibility = View.VISIBLE
            binding.timedCookMiniThermometers.root.visibility = View.VISIBLE
        } else {
            binding.labelThermometers.visibility = View.GONE
            binding.timedCookMiniThermometers.root.visibility = View.GONE
        }
    }

    private fun GrillThermometer.getThermCellIcon(): Int {
        return if (this.pluggedIn) R.drawable.ic_thermometer_white else R.drawable.ic_unpluged_thermometer_white
    }

    private fun initOnClickedListeners() {
        binding.editTempCardView.setOnCardContainerOnClick {
            findNavController().safeNavigate(
                CookFragmentDirections.actionCookFragmentToEditGrillTempFragment()
            )
        }
        binding.editTimeTempCardView.setOnCardContainerOnClick {
            findNavController().safeNavigate(
                CookFragmentDirections.actionCookFragmentToEditTimeTempFragment()
            )
        }
        binding.probeOneCard.setOnCardContainerOnClick {
            findNavController().safeNavigate(
                CookFragmentDirections.actionCookFragmentToEditFoodTempProbe0Fragment()
            )
        }
        binding.probeTwoCard.setOnCardContainerOnClick {
            findNavController().safeNavigate(
                CookFragmentDirections.actionCookFragmentToEditFoodTempProbe1Fragment()
            )
        }

        binding.thermCookMiniThermometers.thermometerOne.setOnClickListener {
            homeViewModel.selectMiniThermometer(false)
        }

        binding.thermCookMiniThermometers.thermometerTwo.setOnClickListener {
            homeViewModel.selectMiniThermometer(true)
        }

        binding.skipButton.setOnClickListener {
            logBreadCrumbClickEvent("SKIP PREHEAT/IGNITION")
            homeViewModel.skipPreheat()
        }

        binding.woodFireCardView.setWoodFireToggleOnCheckChangedCook {
            logBreadCrumbClickEvent("UPDATE WOODFIRE SETTING")
            showLoadingScreen()
            homeViewModel.editWoodfireSetting(it)
        }

        binding.notificationBanner.closeButton.setOnClickListener {
            notificationTimer.cancel()
            binding.notificationBanner.root.visibility = View.GONE
            binding.toolbar.visibility = View.VISIBLE
        }
    }

    private fun setupDropdownAppliancePicker(grills: List<Grill>) {

        var currentGrill = homeViewModel.currentGrill.value

        if (currentGrill == null && grills.isNotEmpty()) {
                    homeViewModel.setCurrentGrillGC(grills[0])
                    currentGrill = grills[0]
        }

        binding.toolbar.setUpApplianceListRv(grills.toMutableList()) {
                    homeViewModel.setCurrentGrillGC(it)
                    binding.grillSelectorBackground.visibility = View.GONE
        }

        binding.toolbar.setApplianceSelectorOnClickListener { isOpen ->
                    binding.grillSelectorBackground.isVisible = isOpen
        }

        binding.grillSelectorBackground.setOnClickListener {
            it.hide()
            binding.toolbar.closeApplianceSelector()
        }

        currentGrill?.let {
                    binding.toolbar.setCurrentApplianceName(it)
        }
    }

    private fun checkInfoAction(infoAction: CookDashboardInfoAction) {
        when (infoAction) {
            is CookDashboardInfoAction.StopCook -> {
//                homeViewModel.updateAction(ActionProcessed)
//                findNavController().safeNavigate(CookFragmentDirections.actionToPreCook())
            }

            is CookDashboardInfoAction.ShowThermometer1NotPluggedIn -> {
                homeViewModel.setTherm1NotPluggedInHasShown(true)
                homeViewModel.updateAction(ActionProcessed)
                findNavController().safeNavigate(CookFragmentDirections.actionShowPlugInTherm1())
            }
            is CookDashboardInfoAction.ShowThermometer2NotPluggedIn -> {
                homeViewModel.setTherm2NotPluggedInHasShown(true)
                homeViewModel.updateAction(ActionProcessed)
                findNavController().safeNavigate(CookFragmentDirections.actionShowPlugInTherm2())
            }

            is CookDashboardInfoAction.ShowProbeOneDoneModal -> {
                showThermometerDoneDialog("1")
            }

            is CookDashboardInfoAction.ShowProbeTwoDoneModal -> {
                showThermometerDoneDialog("2")
            }
            is CookDashboardInfoAction.SwitchCookTypeOpenEditTimeTemp -> {
                if(switchToTimedCookDialog.isAdded) {
                    switchToTimedCookDialog.dismissNow()
                }
                homeViewModel.updateAction(ActionProcessed)
                findNavController().safeNavigate(CookFragmentDirections.actionCookFragmentToEditTimeTempFragment())
            }
            is CookDashboardInfoAction.SwitchCookTypeOpenProbe1 -> {
                homeViewModel.updateAction(ActionProcessed)
                if(switchToThermCookDialog.isAdded) {
                    switchToThermCookDialog.dismissNow()
                }
                findNavController().safeNavigate(CookFragmentDirections.actionCookFragmentToEditFoodTempProbe0Fragment())
            }
            is CookDashboardInfoAction.SwitchCookTypeOpenProbe2 -> {
                homeViewModel.updateAction(ActionProcessed)
                if(switchToThermCookDialog.isAdded) {
                    switchToThermCookDialog.dismissNow()
                }
                findNavController().safeNavigate(CookFragmentDirections.actionCookFragmentToEditFoodTempProbe1Fragment())
            }

            is CookDashboardInfoAction.EditWoodFireSettingSuccess,
            is CookDashboardInfoAction.EditWoodFireSettingError -> {
                homeViewModel.updateAction(ActionProcessed)
                editWoodFireSettingLoadingDialog.dismiss()
            }

            else -> {}
        }
    }

    private fun checkLocalNotificationEvent(localNotification: LocalNotification) {
        when(localNotification) {
            LocalNotification.ActionProcessed -> {}
            LocalNotification.OfflineNotification -> showGrillOfflineNotification()
        }
    }

    private fun setInitialGradient() {
        binding.dialOne.progressState.text = getString(DialState.Preheating.dialSubText).uppercase()

        val width = binding.dialOne.progressState.paint.measureText(
            getString(DialState.Preheating.dialSubText)
        )
        val textSize = binding.dialOne.progressState.textSize

        binding.dialOne.progressState.paint.shader =
            TextGradients().getPreheatGradient(width, textSize)
    }

    private fun setMiniThermometerTextAndIconColor(color: Int) {
        binding.timedCookMiniThermometers.thermometerOneText.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.timedCookMiniThermometers.thermometerOneImage.setIconTintColor(requireContext(), color)
        binding.timedCookMiniThermometers.thermometerOneTemp.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.timedCookMiniThermometers.unitLeft.setTextColor(ContextCompat.getColor(requireContext(), color))

        binding.timedCookMiniThermometers.thermometerTwoText.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.timedCookMiniThermometers.thermometerTwoImage.setIconTintColor(requireContext(), color)
        binding.timedCookMiniThermometers.thermometerTwoTemp.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.timedCookMiniThermometers.unitRight.setTextColor(ContextCompat.getColor(requireContext(), color))

        binding.thermCookMiniThermometers.thermometerOneText.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.thermCookMiniThermometers.thermometerOneImage.setIconTintColor(requireContext(), color)
        binding.thermCookMiniThermometers.thermometerOneTemp.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.thermCookMiniThermometers.unitLeft.setTextColor(ContextCompat.getColor(requireContext(), color))

        binding.thermCookMiniThermometers.thermometerTwoText.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.thermCookMiniThermometers.thermometerTwoImage.setIconTintColor(requireContext(), color)
        binding.thermCookMiniThermometers.thermometerTwoTemp.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.thermCookMiniThermometers.unitRight.setTextColor(ContextCompat.getColor(requireContext(), color))

    }

    // in app banners
    private fun checkBannerEvent(bannerEvent: BannerEvent) {
        when(bannerEvent) {
            is BannerEvent.AddFood -> {
                showNotification(CookNotification.AddFood, getCookBackgroundColorsToast(requireContext()))
            }

            is BannerEvent.CloseLid -> {
                showNotification(CookNotification.CloseLid, getCloseLidBackgroundColorsToast(requireContext()))
                // has to be able to fire multiple times
                homeViewModel.resetBannerEvent()
            }
            is BannerEvent.FlipFood -> {
                showNotification(CookNotification.FlipFood, getCookBackgroundColorsToast(requireContext()))
            }
            is BannerEvent.ShowTherm1GetFood -> {
                showNotification(CookNotification.GetFoodThermometerOne, getRestBackgroundColorsToast(requireContext()))
            }
            is BannerEvent.ShowTherm2GetFood -> {
                showNotification(CookNotification.GetFoodThermometerTwo, getRestBackgroundColorsToast(requireContext()))
            }
            else -> {}
        }
    }

    private fun showNotification(cookNotification: CookNotification, bgColors: IntArray) {
        setNotificationView(cookNotification, bgColors)
        binding.toolbar.visibility = View.GONE
        binding.notificationBanner.root.visibility = View.VISIBLE
        notificationTimer.start()
    }

    private fun setNotificationView(cookNotification: CookNotification, bgColors: IntArray) {
        val gradientBG = GradientDrawable().apply {
            colors = bgColors
            orientation = GradientDrawable.Orientation.LEFT_RIGHT
            gradientType = GradientDrawable.LINEAR_GRADIENT
            shape = GradientDrawable.RECTANGLE
        }

        binding.notificationBanner.title.text = getString(cookNotification.title)
        binding.notificationBanner.body.text = getString(cookNotification.body)
        binding.notificationBanner.iconLeft.setImageResource(cookNotification.iconLeft)
        binding.notificationBanner.root.background = gradientBG
    }

    private fun showThermometerDoneDialog(probeNumber: String) {
        DarkThemeDialog {
            title = getString(R.string.cook_complete)
            description = getString(R.string.therm_has_reached_temp, probeNumber)
            topButton = DarkThemeDialog.ButtonConfiguration(
                text = getString(R.string.check_it_out)
            )
            bottomButton = DarkThemeDialog.ButtonConfiguration(
                text = getString(R.string.okay_button)
            )
            topButtonColor = R.color.ninja_green
        }.show(parentFragmentManager, CookEditTimeTempFragment.COOK_TIME_GRILL_TEMP_TAG)

    }

    private fun showNotSupportedDialog() {
       val notSupportedDialog =  DarkThemeDialog {
            title = getString(R.string.dialog_not_supported_title)
            description = if(homeViewModel.isUSGrill()) getString(R.string.dialog_not_supported_body) else getString(R.string.dialog_not_supported_body_intl)
            topButton = DarkThemeDialog.ButtonConfiguration(
                text = getString(R.string.dialog_not_supported_primary)
            )
            topButtonColor = R.color.ninja_green
        }
        if(notSupportedDialog.isAdded.not()) {
            notSupportedDialog.show(parentFragmentManager, CookEditTimeTempFragment.COOK_TIME_GRILL_TEMP_TAG)
        }

    }

    private fun showSwitchToThermCookDialog() {
        if(switchToThermCookDialog.isAdded.not()) {
            switchToThermCookDialog.show(parentFragmentManager, SWITCH_TO_THERM_DIALOG)
        }
    }

    private fun showSwitchToTimedCookDialog() {
        if(switchToTimedCookDialog.isAdded.not()) {
            switchToTimedCookDialog.show(parentFragmentManager, SWITCH_TO_TIMED_COOK)
        }
    }

    private fun showLoadingScreen() {
        val dialogBinding = LayoutProgressBarDialogBinding.inflate(layoutInflater)
        editWoodFireSettingLoadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        editWoodFireSettingLoadingDialog.setCancelable(false)
        editWoodFireSettingLoadingDialog.setContentView(dialogBinding.root)
        editWoodFireSettingLoadingDialog.show()
    }

    private fun showGrillOfflineNotification() {
        val notification = grillOfflineNotification(requireContext())
        (requireActivity() as HomeActivity).createLocalNotification(
                notification
        )
    }

    companion object {
        const val COOK_FRAGMENT_TAG = "COOK_FRAGMENT"
        const val SWITCH_TO_THERM_DIALOG = "switchToThermCookDialog"
        const val SWITCH_TO_TIMED_COOK = "switchToTimedCookDialog"
    }

}