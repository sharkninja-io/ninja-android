package com.sharkninja.ninja.connected.kitchen.sections.home.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sharkninja.grillcore.CookMode
import com.sharkninja.grillcore.Doneness
import com.sharkninja.grillcore.Food
import com.sharkninja.grillcore.Grill
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.COLD_SMOKE_DISPLAY
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.PROBE_0
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.PROBE_1
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardInfoAction
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardInfoAction.ActionProcessed
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardInfoAction.ShowAccessoryModal
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardTabs
import com.sharkninja.ninja.connected.kitchen.data.enums.IconButtonState
import com.sharkninja.ninja.connected.kitchen.data.enums.getCookDashBoardCardDisplayName
import com.sharkninja.ninja.connected.kitchen.data.enums.getDisplayName
import com.sharkninja.ninja.connected.kitchen.data.enums.SurveyAction
import com.sharkninja.ninja.connected.kitchen.data.enums.getDisplayTemp
import com.sharkninja.ninja.connected.kitchen.data.models.PreCookDashboardButtonState
import com.sharkninja.ninja.connected.kitchen.data.models.PreCookDashboardUiState
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentPreCookBinding
import com.sharkninja.ninja.connected.kitchen.ext.displayName
import com.sharkninja.ninja.connected.kitchen.ext.hide
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.ext.shouldNavigateFromPreCookToCook
import com.sharkninja.ninja.connected.kitchen.ext.toTimeDisplay
import com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs.AppRatingPromptDialogFragment
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters.LocationPermissionRequester
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters.NearbyDevicesPermissionRequester
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.views.CookToggleCardItemView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch

class PreCookFragment : Fragment() {
    private lateinit var binding: FragmentPreCookBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private var hasChecked = false
    private var isCheckingPermissions = true

    private val appRatingDialog: DialogFragment by lazy { AppRatingPromptDialogFragment()}

    private val nearbyDevicesPermissionRequester = NearbyDevicesPermissionRequester(this, ::onNearbyDevicesGranted, ::onNearbyDevicesDenied)
    private val locationPermissionRequester = LocationPermissionRequester(this, ::onLocationGranted, ::onLocationDenied)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setStatusBar()
        binding = FragmentPreCookBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setStatusBar() {
        (requireActivity() as HomeActivity).setStatusBarColorLight()
        (requireActivity() as HomeActivity).showBottomTabNavigation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.isNavigatingCookToPreCook = false
        homeViewModel.getCurrentGrill()
//        setupDropdownAppliancePicker()
        initBindings()
        initCookModeMenu()
        subscribeToVM()
        cookSettingsSetup()
        setUpProbeCardOnClickListeners()
        overrideBackPressed()
        homeViewModel.cancelCookBannerNotificationJob()
    }

    private fun initBindings() {
        binding.grillSettingsMenu.setCookModeMenuOptions(homeViewModel.isUSGrill())
        homeViewModel.getShouldShowAppRatingPrompt()
        homeViewModel.getShouldShowPlaceYourThermometerDialog()

        binding.buttonStartCooking.setOnClickListener {
            homeViewModel.showAccessoryModal()
        }

        binding.editTempCardView.setOnCardContainerOnClick {
            findNavController().safeNavigate(PreCookFragmentDirections.actionPreCookFragmentToEditGrillTempFragment())
        }

        binding.editTimeTempCardView.setOnCardContainerOnClick {
            findNavController().safeNavigate(PreCookFragmentDirections.actionPreCookFragmentToEditCookTimeTempFragment())
        }
    }

    private fun subscribeToVM() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.selectedGrillState.collectLatest {
                        if(it.state.shouldNavigateFromPreCookToCook(homeViewModel.isNavigatingPreCookToCook) && isCheckingPermissions.not()) {
                            homeViewModel.isNavigatingPreCookToCook = true
                            homeViewModel.observeDevicePushEnabled()
                            findNavController().safeNavigate(PreCookFragmentDirections.actionPreCookFragmentToCookFragment())
                        }
                    }
                }

                launch {
                    homeViewModel.dashboardTabSelectedState.collectLatest {
                        setUpTabSelection(it)
                        binding.cookTypeTabs.switchTabSelection.setChecked(it)
                    }
                }

                launch {
                    homeViewModel.cookMode.collectLatest {
                        binding.grillSettingsMenu.apply {
                            cookMode = it
                        }
                        binding.editTempCardView.showUnitLeft(it != CookMode.Grill)
                        binding.editTimeTempCardView.showUnitRight(it != CookMode.Grill)
                        binding.editTimeTempCardView.showUnitLeft(false)
                        binding.woodFireCardView.setWoodFireStatePreCook(it)
                    }
                }

                launch {
                    homeViewModel.userTemperatureUnit.collectLatest {
                        val tempUnit = getString(it.getDisplayName())
                        binding.editTempCardView.setUnitLeft(tempUnit)
                        binding.editTimeTempCardView.setUnitRight(tempUnit)
                        binding.probe0.setTempUnit(tempUnit)
                        binding.probe1.setTempUnit(tempUnit)
                    }
                }
                launch {
                    homeViewModel.preCookDashboardUiState.collectLatest {
                        initCardViews(it)
                    }
                }

                launch {
                    homeViewModel.infoAction.collectLatest {
                        handleDashboardInfoActions(it)
                    }
                }

                launch {
                    homeViewModel.probe0FoodPreset.collectLatest {
                        setProbeName(it.food, binding.probe0, R.string.probe0_manual_title)
                    }
                }

                launch {
                    homeViewModel.probe1FoodPreset.collectLatest {
                        setProbeName(it.food, binding.probe1, R.string.probe1_manual_title)
                    }
                }

                launch {
                    homeViewModel.isProbe0ToggleOn.collectLatest {
                        binding.probe0.setToggleStateProbe(it)
                    }
                }

                launch {
                    homeViewModel.isProbe1ToggleOn.collectLatest {
                        binding.probe1.setToggleStateProbe(it)
                    }
                }

                launch {
                    homeViewModel.isWoodFireToggleOn.collectLatest {
                        binding.woodFireCardView.setWoodFireToggleStateAndStatus(it)
                    }
                }

                launch {
                    homeViewModel.grills.onSubscription {
                        homeViewModel.getCachedGrills()
                    }.collect {
                        if (it.isEmpty()) redirectToBTPairing() else {
                            setupDropdownAppliancePicker(it)
                            if (!hasChecked) {
                                hasChecked = true
                                isCheckingPermissions = true
                                checkNearbyPermissions()
                            }

                            if (homeViewModel.askForNotifications) {
                                findNavController().safeNavigate(PreCookFragmentDirections.actionPreCookFragmentToRequestNotificationsDialog())
                            }
                        }
                    }
                }

                launch {
                    homeViewModel.connectionStatusToolbar.collectLatest {
                        binding.toolbar.setConnectionStatus(it)
                    }
                }

                launch {
                    homeViewModel.preCookDashboardButtonState.collectLatest {
                        setStartCookingButtonState(it)
                    }
                }

                launch {
                    homeViewModel.surveyAction.collectLatest {
                        handleSurveyAction(it)
                    }
                }
            }
        }
    }

    private fun overrideBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    private fun initCardViews(uiState: PreCookDashboardUiState) {

        val displayTimeValue = uiState.duration.toTimeDisplay(homeViewModel.cookMode.value, true)
        val displayTemperature = if(homeViewModel.cookMode.value == CookMode.Grill) getString(getDisplayTemp(uiState.grillTemp)).uppercase() else uiState.grillTemp


        setProbeCardValues(uiState)

        when (homeViewModel.cookMode.value) {
            CookMode.Smoke -> {
                val coldSmokeTemp = homeViewModel.coldSmokeTemp()
                binding.editTimeTempCardView.setCardValues(
                    displayTimeValue,
                    if (displayTemperature == coldSmokeTemp) COLD_SMOKE_DISPLAY else displayTemperature
                )
                binding.editTimeTempCardView.showUnitRight((displayTemperature == coldSmokeTemp).not())

                binding.editTempCardView.setCardValues(
                    if (displayTemperature == coldSmokeTemp) COLD_SMOKE_DISPLAY else displayTemperature,
                    null
                )
                binding.editTempCardView.showUnitLeft((displayTemperature == coldSmokeTemp).not())
            }
            else -> {
                binding.editTimeTempCardView.setCardValues(displayTimeValue, displayTemperature)
                binding.editTempCardView.setCardValues(displayTemperature, null)
            }
        }
    }

    private fun setProbeCardValues(uiState: PreCookDashboardUiState) {
        setProbe0Value(uiState.probe0Doneness, uiState.probe0FoodTempValue)
        setProbe1Value(uiState.probe1Doneness, uiState.probe1FoodTempValue)
        checkProbePluggedInState(uiState.probe0PluggedIn, uiState.probe1PluggedIn)
    }

    private fun setProbe0Value(doneness: Doneness, probeTemp: String) {
        binding.probe0.setProbeValue(
            probeDisplayValue(
                doneness,
                probeTemp,
                PROBE_0
            ))
        binding.probe0.showTempUnit(doneness == Doneness.NotSet)
    }

    private fun setProbe1Value(doneness: Doneness, probeTemp: String) {
        binding.probe1.setProbeValue(
            probeDisplayValue(
                doneness,
                probeTemp,
                PROBE_1
            ))
        binding.probe1.showTempUnit(doneness == Doneness.NotSet)
    }

    private fun probeDisplayValue(doneness: Doneness, probeTemp: String, probeNumber: Int): String {
        return if(homeViewModel.isProbeSet(probeNumber).not()) {
            homeViewModel.getProbeDefaultValue()
        } else {
            if(doneness != Doneness.NotSet) getString(doneness.displayName()) else probeTemp
        }
    }

    private fun checkProbePluggedInState(probeOnePluggedIn: Boolean, probeTwoPluggedIn: Boolean) {
        binding.probe0.setProbePluggedIn(probeOnePluggedIn)
        binding.probe1.setProbePluggedIn(probeTwoPluggedIn)
    }

    private fun setProbeName(food: Food, view: CookToggleCardItemView, probeNumber: Int) {
        val probeName = when (food) {
            Food.Chicken,
            Food.Beef,
            Food.Pork,
            Food.Fish,
            Food.Lamb -> getString(food.getCookDashBoardCardDisplayName())

            else -> getString(probeNumber)
        }

        view.setProbeName(probeName)
    }

    private fun setUpProbeCardOnClickListeners() {
        binding.probe0.setProbeToggleOnCheckChangedListener { isActive ->
            homeViewModel.updateIsProbe0ToggleStatus(isActive)
        }
        binding.probe0.setProbeOnClickListener {
            findNavController().safeNavigate(
                PreCookFragmentDirections.actionPreCookFragmentToEditFoodTempProbe0Fragment()
            )
        }

        binding.probe1.setProbeToggleOnCheckChangedListener { isActive ->
            homeViewModel.updateIsProbe1ToggleStatus(isActive)
        }
        binding.probe1.setProbeOnClickListener {
            findNavController().safeNavigate(
                PreCookFragmentDirections.actionPreCookFragmentToEditFoodTempProbe1Fragment()
            )
        }
        binding.woodFireCardView.setWoodFireToggleOnCheckChangedPreCook {
                homeViewModel.updateWoodfireToggleStatus(it)
        }
    }

    private fun handleDashboardInfoActions(action: CookDashboardInfoAction) {
        when (action) {
            is ShowAccessoryModal -> {
                findNavController().safeNavigate(PreCookFragmentDirections.actionPreCookFragmentToGrillAccessoryDialog())
                homeViewModel.updateAction(ActionProcessed)
            }

            is CookDashboardInfoAction.StartCook -> {
                homeViewModel.sendCookCommandToGrillCore()
                findNavController().safeNavigate(PreCookFragmentDirections.actionShowProgressBar())
            }

            is CookDashboardInfoAction.StartCookFromChart -> {
                homeViewModel.sendCookCommandToGrillCore()
                findNavController().safeNavigate(PreCookFragmentDirections.actionShowProgressBar())
            }

            is CookDashboardInfoAction.ShowChartsAccessoryModal -> {
                findNavController().safeNavigate(PreCookFragmentDirections.actionPreCookFragmentToChartAccessoryDialog())
            }

            is ActionProcessed -> {}
            else -> {}
        }
    }

    private fun handleSurveyAction(surveyAction: SurveyAction) {
        when(surveyAction) {
            SurveyAction.ShowAppRatingSurvey -> {
                homeViewModel.updateSurveyAction(SurveyAction.SurveyActionProcessed)
                showAppRatingDialog()
            }
            SurveyAction.AppRatingPositiveAction -> {
                homeViewModel.updateSurveyAction(SurveyAction.SurveyActionProcessed)
                openPlayStore()
            }
            SurveyAction.AppRatingNegativeAction -> {
                homeViewModel.updateSurveyAction(SurveyAction.SurveyActionProcessed)
                goToSupport()

            }
            SurveyAction.SurveyActionProcessed -> {}
        }
    }

    private fun cookSettingsSetup() {
        binding.cookTypeTabs.switchTabSelection.setOnToggleSwitchChangeListener {
            homeViewModel.setDashboardTabSelectedState(it)
        }
    }

    private fun setUpTabSelection(dashboardTab: CookDashboardTabs) {
        when (dashboardTab) {
            CookDashboardTabs.TimeCook -> showTimeCookDashboardTab()
            CookDashboardTabs.Thermometer -> showThermometerDashboardTab()
        }
    }

    private fun showTimeCookDashboardTab() {
        binding.grillSettingsMenu.apply {
            cookModesUnavailableInThermometerState = IconButtonState.LightEnabled
        }
        binding.probe0.visibility = View.GONE
        binding.probe1.visibility = View.GONE
        binding.editTempCardView.visibility = View.GONE
        binding.editTimeTempCardView.visibility = View.VISIBLE
    }

    private fun showThermometerDashboardTab() {
        binding.grillSettingsMenu.apply {
            cookModesUnavailableInThermometerState = IconButtonState.LightDisabled
        }
        binding.probe0.visibility = View.VISIBLE
        binding.probe1.visibility = View.VISIBLE
        binding.editTimeTempCardView.visibility = View.GONE
        binding.editTempCardView.visibility = View.VISIBLE
    }

    private fun setStartCookingButtonState(buttonState: PreCookDashboardButtonState) {
        binding.buttonStartCooking.isEnabled = buttonState.getDashboardButtonEnabled()
        binding.buttonStartCooking.text = getString(buttonState.getDashboardButtonText())
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

    private fun initCookModeMenu() {
        binding.grillSettingsMenu.setMenuItemOnClicksListeners {
            homeViewModel.selectDashboardCookMode(it)
        }

        binding.grillSettingsMenu.calculateSpacingForHorizontalModeSelector(
            homeViewModel.density,
            homeViewModel.xPix
        )
    }

    private fun redirectToBTPairing() {
        findNavController().safeNavigate(PreCookFragmentDirections.actionPreCookFragmentToHome())
    }

    private fun checkNearbyPermissions() {
        nearbyDevicesPermissionRequester.checkPermissions(requireContext())
    }

    private fun checkLocationPermission() {
        locationPermissionRequester.checkPermissions(requireContext())
    }

    private fun onNearbyDevicesGranted() {
        println("PERMISSION CHECK - NEARBY DEVICES - GRANTED")
        checkLocationPermission()
    }

    private fun onNearbyDevicesDenied() {
        println("PERMISSION CHECK - NEARBY DEVICES - DENIED")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            checkLocationPermission()
        } else {
            homeViewModel.updatePermissionsGranted(false)
            isCheckingPermissions = false
        }
    }

    private fun onLocationGranted() {
        println("PERMISSION CHECK - LOCATION - GRANTED")
        homeViewModel.updatePermissionsGranted(true)
        homeViewModel.checkBluetoothAdapterOn()
        isCheckingPermissions = false
    }

    private fun onLocationDenied() {
        println("PERMISSION CHECK - LOCATION - DENIED")
        homeViewModel.updatePermissionsGranted(false)

        isCheckingPermissions = false
    }

    private fun showAppRatingDialog() {
        appRatingDialog.show(childFragmentManager, AppRatingPromptDialogFragment.TAG)
    }
    private fun openPlayStore() {
        appRatingDialog.dismissNow()
        try {
            actionOpenPlayStore(AppRatingPromptDialogFragment.URI)

        } catch (e: ActivityNotFoundException) {
            actionOpenPlayStore(AppRatingPromptDialogFragment.FULL_URI)
        }
    }

    private fun goToSupport() {
        findNavController().safeNavigate(PreCookFragmentDirections.actionPreCookFragmentToSupportFragmentCook())
    }

    private fun actionOpenPlayStore(uri: String) {
        val flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.flags = flags
        startActivity(intent)
    }
}