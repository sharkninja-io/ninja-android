package com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharkninja.cloudcore.WifiNetwork
import com.sharkninja.grillcore.*
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.COLD_SMOKE_TEMP_CELSIUS
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.COLD_SMOKE_TEMP_FAREN
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.PROBE_0
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants.Companion.PROBE_1
import com.sharkninja.ninja.connected.kitchen.data.enums.BannerEvent
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardInfoAction
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardTabs
import com.sharkninja.ninja.connected.kitchen.data.enums.CookUiState
import com.sharkninja.ninja.connected.kitchen.data.enums.LocalNotification
import com.sharkninja.ninja.connected.kitchen.data.enums.ProgressDialFormattedString
import com.sharkninja.ninja.connected.kitchen.data.enums.ProgressDialFormattedString.SimpleStringProgressDial
import com.sharkninja.ninja.connected.kitchen.data.enums.ProgressDialFormattedString.TimerStringProgressDial
import com.sharkninja.ninja.connected.kitchen.data.enums.ProgressDialFormattedString.NumberPercent
import com.sharkninja.ninja.connected.kitchen.data.enums.ProgressDialFormattedString.NumberTempUnit
import com.sharkninja.ninja.connected.kitchen.data.enums.RegionCode
import com.sharkninja.ninja.connected.kitchen.data.enums.SurveyAction
import com.sharkninja.ninja.connected.kitchen.data.enums.getCookModeFromCookMethod
import com.sharkninja.ninja.connected.kitchen.data.enums.getDisplayName
import com.sharkninja.ninja.connected.kitchen.data.models.ConnectionStatusToolbar
import com.sharkninja.ninja.connected.kitchen.data.models.Duration
import com.sharkninja.ninja.connected.kitchen.data.models.PreCookDashboardUiState
import com.sharkninja.ninja.connected.kitchen.data.models.ProbePreset
import com.sharkninja.ninja.connected.kitchen.data.models.PreCookDashboardButtonState
import com.sharkninja.ninja.connected.kitchen.data.models.cookitems.FoodTemperatureItem
import com.sharkninja.ninja.connected.kitchen.data.models.cookitems.GrillTemperatureItem
import com.sharkninja.ninja.connected.kitchen.data.models.cookitems.TimerItem
import com.sharkninja.ninja.connected.kitchen.data.models.getConnectionStatus
import com.sharkninja.ninja.connected.kitchen.data.models.pairing.BluetoothPermissionErrorState
import com.sharkninja.ninja.connected.kitchen.data.models.pairing.BluetoothPermissionsState
import com.sharkninja.ninja.connected.kitchen.data.models.pairing.PhoneToDeviceConnectionStatus
import com.sharkninja.ninja.connected.kitchen.ext.isGrillActive
import com.sharkninja.ninja.connected.kitchen.ext.timeListToMap
import com.sharkninja.ninja.connected.kitchen.ext.toSplitDuration
import com.sharkninja.ninja.connected.kitchen.ext.toTimeDisplayProgressDial
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CookingCard
import com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth.BluetoothService
import com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth.GattFlowTransmission
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.CookInterface
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CookTypeStrings
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.FoodTypeStrings
import com.sharkninja.ninja.connected.kitchen.sections.home.services.WifiManagerIoTDeviceWifiScanningService
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.PermissionStatus
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.WifiPairingInterface
import com.sharkninja.ninja.connected.kitchen.ui.views.DialState
import com.sharkninja.ninja.connected.kitchen.ui.views.TabState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class HomeViewModel(
    private val wifiPairingService: WifiPairingInterface,
    private val bluetoothService: BluetoothService,
    private val cookService: CookInterface
) : ViewModel() {
    var redirectToCook = false

    var hotspotErrorCount = 0
    var wifiErrorCount = 0

    var wasRedirectedAtLaunch = false

    private val _bluetoothErrorCount = MutableStateFlow(0)
    val bluetoothErrorCount = _bluetoothErrorCount.asStateFlow()

    private val _iotDeviceWifiNetworks = MutableStateFlow<List<String>>(listOf())
    val iotDeviceWifiNetworks = _iotDeviceWifiNetworks.asStateFlow()

    private val _selectedIoTDeviceWifiNetworkSSID = MutableStateFlow<String>("")
    val selectedIoTDeviceWifiNetworkSSID = _selectedIoTDeviceWifiNetworkSSID.asStateFlow()

    private val _wifiNetworksVisibleToIoTDevice =
        MutableStateFlow<List<WifiNetwork>>(value = listOf())
    val wifiNetworksVisibleToIoTDevice = _wifiNetworksVisibleToIoTDevice.asStateFlow()

    private val _phoneConnectionStatus =
        MutableStateFlow<PhoneToDeviceConnectionStatus>(value = PhoneToDeviceConnectionStatus.NotStarted)
    val phoneConnectionStatus = _phoneConnectionStatus.asStateFlow()

    private val _selectedUserWifiNetwork: MutableLiveData<WifiNetwork?> = MutableLiveData()
    val selectedUserWifiNetwork: LiveData<WifiNetwork?> = _selectedUserWifiNetwork

    private val _grillName = MutableStateFlow(value = "")
    val grillName = _grillName.asStateFlow()

    private val _isPermissionGranted = MutableStateFlow(value = PermissionStatus.HaventAsked)
    val isPermissionGranted = _isPermissionGranted.asStateFlow()

    private val _isBluetoothPermissionGranted = MutableStateFlow(BluetoothPermissionsState())
    val isBluetoothPermissionGranted = _isBluetoothPermissionGranted.asStateFlow()

    private var gatewayIpAddress = ""

    private val _newGrillDSN = MutableStateFlow(value = "")
    val newGrillDSN = _newGrillDSN.asStateFlow()

    private val _nearbyPermissionErrorState = MutableStateFlow(BluetoothPermissionErrorState())
    val nearbyPermissionErrorState = _nearbyPermissionErrorState.asStateFlow()

    private val _locationPermissionErrorState = MutableStateFlow(BluetoothPermissionErrorState())
    val locationPermissionErrorState = _locationPermissionErrorState.asStateFlow()

    private val _plugInTherm1HasShown = MutableStateFlow(false)
    val plugInTherm1HasShown = _plugInTherm1HasShown.asStateFlow()

    private val _plugInTherm2HasShown = MutableStateFlow(false)
    val plugInTherm2HasShown = _plugInTherm2HasShown.asStateFlow()

    private val _editScreenDuration = MutableStateFlow(Duration())
    val editScreenDuration = _editScreenDuration.asStateFlow()

    private val _bannerEvent = MutableStateFlow<BannerEvent>(BannerEvent.ActionProcessed)
    val bannerEvent = _bannerEvent.asStateFlow()

    private val _infoModal = MutableStateFlow<BannerEvent>(BannerEvent.ActionProcessed)
    val infoModal = _infoModal.asStateFlow()

    private val _regionCode = MutableStateFlow<RegionCode>(RegionCode.US)
    val regionCode = _regionCode.asStateFlow()

    private val _surveyAction = MutableStateFlow<SurveyAction>(SurveyAction.SurveyActionProcessed)
    val surveyAction = _surveyAction.asStateFlow()


    var deviceMACAddresses: MutableList<String?> = mutableListOf()
    var selectedDevice: String? = null
    var allCharts: MutableList<CookingCard> = mutableListOf()
    val blankCard = CookingCard(0,"","","","","",0,"","","")

    private val _selectedChartFlow = MutableStateFlow<CookingCard>(blankCard)
    val selectedChartFlow = _selectedChartFlow.asStateFlow()

    var isBackFromAddAppliance = false
    var isActionAddAppliance = false

    //These are used in pixel calculations
    var density = 0F
    var xPix = 0
    var xdpi = 0F
    //end

    var filteredCharts: MutableList<CookingCard> = mutableListOf()

    //this was used when only letting the user select one time range
    //var cookTimeRangeFilter = 0

    var anyFilters = false
    var timeFilter = false
    var fifteenMinBox = false
    var fifteenToThirtyMinBox = false
    var thirtyToSixtyMinBox = false
    var sixtyToOneTwentyMinBox = false
    var oneTwentyPlusBox = false

    var smokeFlavorFilter = false

    var cookFilter = false
    var grillCookFilter = false
    var smokeCookFilter = false
    var airCrispCookFilter = false
    var roastCookFilter = false
    var bakeCookFilter = false
    var reheatCookFilter = false
    var broilCookFilter = false
    var dehydrateCookFilter = false

    var foodFilter = false
    var chickenFoodFilter = false
    var beefFoodFilter = false
    var porkFoodFilter = false
    var fishFoodFilter = false
    var veggieFoodFilter = false
    var breadFoodFilter = false

    var askForNotifications = false

    var fruitFoodFilter = false
    var lambFoodFilter = false
    var otherFoodFilter = false

    private val _deviceRegistrationSuccess = MutableSharedFlow<Boolean?>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val deviceRegistrationSuccess = _deviceRegistrationSuccess.asSharedFlow()


    private val _currentGrill = MutableStateFlow<Grill?>(null)
    val currentGrill = _currentGrill.asStateFlow()

    private val _grills = MutableSharedFlow<List<Grill>>(extraBufferCapacity = 1)
    val grills = _grills.asSharedFlow()

    private val _currentGrillState = MutableStateFlow<GrillState?>(null)
    val currentGrillState = _currentGrillState.asStateFlow()

    private val _userTemperatureUnit = MutableStateFlow<DegreeType>(DegreeType.Fahrenheit)
    val userTemperatureUnit = _userTemperatureUnit.asStateFlow()

    private val _infoAction = MutableStateFlow<CookDashboardInfoAction>(CookDashboardInfoAction.ActionProcessed)
    val infoAction = _infoAction.asStateFlow()

    private val _dashboardTabSelectedState = MutableStateFlow(CookDashboardTabs.TimeCook)
    val dashboardTabSelectedState = _dashboardTabSelectedState.asStateFlow()

    private val _preCookDashboardButtonState = MutableStateFlow(PreCookDashboardButtonState())
    val preCookDashboardButtonState = _preCookDashboardButtonState.asStateFlow()

    private val _shouldShowPlaceYourThermometerDialog = MutableStateFlow<Boolean>(true)
    val shouldShowPlaceYourThermometerDialog = _shouldShowPlaceYourThermometerDialog.asStateFlow()

    //GC integration
    private val _preCookDashboardUiState = MutableStateFlow(PreCookDashboardUiState())
    val preCookDashboardUiState = _preCookDashboardUiState.asStateFlow()

    private val _probe0FoodPreset = MutableStateFlow(ProbePreset())
    val probe0FoodPreset = _probe0FoodPreset.asStateFlow()

    private val _probe1FoodPreset = MutableStateFlow(ProbePreset())
    val probe1FoodPreset = _probe1FoodPreset.asStateFlow()

    private val _isProbe0ToggleOn = MutableStateFlow(false)
    val isProbe0ToggleOn = _isProbe0ToggleOn.asStateFlow()

    private val _isProbe1ToggleOn = MutableStateFlow(false)
    val isProbe1ToggleOn = _isProbe1ToggleOn.asStateFlow()

    private val _isWoodFireToggleOn = MutableStateFlow(false)
    val isWoodFireToggleOn = _isWoodFireToggleOn.asStateFlow()


    private val _timeUnit = MutableStateFlow("MINUTES")
    val timeUnit = _timeUnit.asStateFlow()

    private val _probeFoodList = MutableStateFlow<List<FoodTemperatureItem>>(listOf())
    val probeFoodList = _probeFoodList.asStateFlow()

//    private val _probe1FoodList = MutableStateFlow<List<FoodTemperatureItem>>(listOf())
//    val probe1FoodList = _probe1FoodList.asStateFlow()

    private val _cookMode = MutableStateFlow(CookMode.Grill)
    val cookMode = _cookMode.asStateFlow()

    private val _timerMap = MutableStateFlow<Map<TimerItem, List<TimerItem>>>(emptyMap())
    val timerMap = _timerMap.asStateFlow()

    private val _timeCookTempList = MutableStateFlow<List<GrillTemperatureItem>>(listOf())
    val timeCookTempList = _timeCookTempList.asStateFlow()

    private val _thermometerCookTempList = MutableStateFlow<List<GrillTemperatureItem>>(listOf())
    val thermometerCookTempList = _thermometerCookTempList.asStateFlow()


    //COOK
    private val _grillCookMode = MutableStateFlow<CookMode>(CookMode.NotSet)
    val grillCookMode = _grillCookMode.asStateFlow()
    var currentCookMode = CookMode.Smoke

    private val _grillFoodType = MutableStateFlow<Food>(Food.NotSet)
    val grillFoodType = _grillFoodType.asStateFlow()

//    private val _grillFoodTemp = MutableStateFlow<Doneness>(Doneness.NotSet)
//    val grillFoodTemp = _grillFoodTemp.asStateFlow()

    private val _userConfirmedAccessoryDialogue = MutableStateFlow(false)
    val userConfirmedAccessoryDialogue = _userConfirmedAccessoryDialogue.asStateFlow()

    private val _connectionStatusToolbar = MutableStateFlow<ConnectionStatusToolbar>(ConnectionStatusToolbar.Offline)
    val connectionStatusToolbar = _connectionStatusToolbar.asStateFlow()

    private val _selectedGrillState = MutableStateFlow(GrillState.new())
    val selectedGrillState = _selectedGrillState.asStateFlow()

    private val _cookDashboardUiState = MutableStateFlow(CookUiState())
    val cookDashboardUiState = _cookDashboardUiState.asStateFlow()

    private val _isDevicePushEnabled = MutableStateFlow(false)
    val isDevicePushEnabled = _isDevicePushEnabled.asStateFlow()

    private val _cookTypeTabState = MutableStateFlow(TabState.OfflineDisabled)
    val cookTypeTabState = _cookTypeTabState.asStateFlow()

    // bluetooth checks for flow with grill

    private val _isBluetoothAdapterOn = MutableStateFlow(false)
    val  isBluetoothAdapterOn = _isBluetoothAdapterOn.asStateFlow()

    private val _permissionAllGranted = MutableStateFlow<Boolean>(true)
    val permissionAllGranted = _permissionAllGranted.asStateFlow()

    private val _isBluetoothAvailable = MutableStateFlow(false)
    val  isBluetoothAvailable = _isBluetoothAvailable.asStateFlow()

    private val _isPushEnabled = MutableStateFlow(false)
    val isPushEnabled = _isPushEnabled.asStateFlow()

    private val _localNotificationEvent = MutableStateFlow<LocalNotification>(LocalNotification.ActionProcessed)
    val localNotificationEvent = _localNotificationEvent.asStateFlow()

    var isNavigatingPreCookToCook = false
    var isNavigatingCookToPreCook = false




    private var notificationJob = Job()

    init {
        fetchRegion()
        observeSelectedGrillState()
        observeDefaultTime()
        observeTimeUnit()
        observeTimeList()
        observeDefaultTemp()
        observeGrillTempList()
        observePermissionsEnabled()
        observeBluetoothEnabled()
    }

    fun setSelectedChartFlow(newSelectedChart: CookingCard) {
        _selectedChartFlow.value = newSelectedChart
    }

    fun isUserRegionUS(): Boolean = regionCode.value == RegionCode.US

    fun isUSGrill(): Boolean {
        return User.userGrillType() == UserGrillType.US
    }

    fun postUserConfirmedAccessoryDialogue() {
        _userConfirmedAccessoryDialogue.value = true
    }
    fun flushUserConfirmedAccessoryDialogue() {
        _userConfirmedAccessoryDialogue.value = false
    }

    fun clearAllFilters() {
        timeFilter = false
        fifteenMinBox = false
        fifteenToThirtyMinBox = false
        thirtyToSixtyMinBox = false
        sixtyToOneTwentyMinBox = false
        oneTwentyPlusBox = false
        smokeFlavorFilter = false
        cookFilter = false
        grillCookFilter = false
        smokeCookFilter = false
        airCrispCookFilter = false
        roastCookFilter = false
        bakeCookFilter = false
        broilCookFilter = false
        dehydrateCookFilter = false
        reheatCookFilter = false
        foodFilter = false
        chickenFoodFilter = false
        beefFoodFilter = false
        porkFoodFilter = false
        fishFoodFilter = false
        veggieFoodFilter = false
        breadFoodFilter = false
        fruitFoodFilter = false
        otherFoodFilter = false
        lambFoodFilter = false
    }


    fun isCookModeUnavailableForThermCook(): Boolean {
        return selectedGrillState.value.cookMode == CookMode.Broil ||
                selectedGrillState.value.cookMode == CookMode.Dehydrate || selectedGrillState.value.cookMode == CookMode.Reheat
    }

    private fun observeSelectedGrillState() {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.getCurrentGrillStatus().collectLatest {
                if (it.state !== CalculatedState.Calculating) {
                    println("GRILL STATE: $it")
                    _selectedGrillState.emit(it)
                    if (it.state.isGrillActive()) {
                        checkConnectionStatus(it)
                        setCookDashboardBackground(it)
                        checkCookTypeTabsEnabled(it)

                        if (it.cookType !== CookType.Timed) {
                            checkProbePluggedInStateActiveCook(it.state)
                            checkUnSelectedProbeHasReachedTemp(it)
                        }
                    } else {
                        checkConnectionStatus(it)
                        checkProbePluggedInStatePreCook(it.probe1.pluggedIn, it.probe2.pluggedIn)
                    }
                }
            }
        }
    }

    private fun checkCookTypeTabsEnabled(state: GrillState) {
        val isOnline = state.connectedToInternet || state.connectedToBluetooth
        val cannotSwitchCookType = isTimedCook() && isCookModeUnavailableForThermCook()

        _cookTypeTabState.value = if(!isOnline || state.isCookComplete() ) {
            TabState.OfflineDisabled
        } else if(cannotSwitchCookType) {
            TabState.ThermometerNotSupported
        } else {
            TabState.Online
        }
    }

    private fun checkConnectionStatus(state: GrillState) {
        if(state.state == CalculatedState.PoweredOff) {
            _connectionStatusToolbar.value = ConnectionStatusToolbar.Offline
            _preCookDashboardButtonState.update {it.copy(
                isOnline = false
            )}
        } else {
            _connectionStatusToolbar.value = getConnectionStatus(state.connectedToBluetooth, state.connectedToInternet)
            _preCookDashboardButtonState.update {it.copy(
                isOnline = state.connectedToBluetooth || state.connectedToInternet
            )}
        }
    }


    fun resetBannerEvent() {
        _bannerEvent.value = BannerEvent.ActionProcessed
    }

    private fun resetLocalNotification() {
        _localNotificationEvent.value = LocalNotification.ActionProcessed
    }

    private fun checkNotificationsForCookType(
        calculatedState: CalculatedState,
        isProbeOne: Boolean
    ) {
        when (calculatedState) {
            CalculatedState.AddFood -> {
                _bannerEvent.value = BannerEvent.AddFood
            }

            CalculatedState.LidOpenDuringCook -> {
                _bannerEvent.value = BannerEvent.CloseLid
            }

            CalculatedState.GetFood -> {
                if (isTimedCook().not()) {
                    if (isProbeOne) {
                        _bannerEvent.value = BannerEvent.ShowTherm1GetFood
                    } else {
                        _bannerEvent.value = BannerEvent.ShowTherm2GetFood
                    }
                }
            }

            CalculatedState.FlipFood -> {
                _bannerEvent.value = BannerEvent.FlipFood
            }

            else -> {}

        }
    }

    private fun isTimedCook(): Boolean {
        return _selectedGrillState.value.cookType == CookType.Timed
    }

    private fun GrillState.isTimedCook(): Boolean {
        return cookType == CookType.Timed
    }

    private fun GrillState.getCookState(): CalculatedState {
        return if (isTimedCook()) {
            state
        } else {
            if (_cookDashboardUiState.value.isThermometer2Selected) {
                if (probe2.active) probe2.state else probe1.state
            } else {
                if (probe1.active) probe1.state else probe2.state
            }
        }
    }

    private fun GrillState.getSelectedProbe(): GrillThermometer? {
        return if (isTimedCook()) null
        else {
            if (cookDashboardUiState.value.isThermometer2Selected) probe2 else probe1
        }
    }

    private fun Int.getProgress(): Int {
        return if (this != 0) this
        else 1
    }

    private fun GrillThermometer.getRestingDisplayValue(): ProgressDialFormattedString {
        return if (pluggedIn) NumberTempUnit(number = currentTemp.toString(), tempUnit = userTemperatureUnit.value.getDisplayName())
        else TimerStringProgressDial(restingTimeCompleted.toTimeDisplayProgressDial(selectedGrillState.value.cookMode))
    }


    private fun setCookDashboardBackground(grillState: GrillState) {
        if (grillState.connectedToInternet.not() && grillState.connectedToBluetooth.not()) {
            setCookDashboardBackgroundOffline(grillState)
            if(_isPushEnabled.value) {
                _localNotificationEvent.value = LocalNotification.OfflineNotification
            }
            return
        }
        resetLocalNotification()
        val probe = grillState.getSelectedProbe()
        when (grillState.getCookState()) {
            CalculatedState.LidOpenBeforeCook -> {
                _cookDashboardUiState.update { it.copy(
                    isOnline = true,
                    isTimedCook = grillState.isTimedCook(),
                    levelListBackground = R.drawable.level_list_preheat,
                    selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_preheat_background,
                    showDialState = false,
                    dialState = DialState.LidOpen,
                    selectedThermometerTabDrawableBackground = R.drawable.preheat_thermometer_selector_background_plugged_in,
                    unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                    progress = 0,
                    progressDisplayValue = SimpleStringProgressDial(R.string.close_lid),
                    cookComplete = false,
                    progressSubVisibility = false,
                    progressSubText = R.string.empty
                ) }
            }

            CalculatedState.LidOpenDuringCook -> {
                _cookDashboardUiState.update { it.copy(
                    isOnline = true,
                    isTimedCook = grillState.isTimedCook(),
                    showDialState = false,
                    dialState = DialState.LidOpen,
                    progress = 0,
                    progressDisplayValue = SimpleStringProgressDial(R.string.close_lid),
                    cookComplete = false,
                    progressSubVisibility = false,
                    progressSubText = R.string.empty

                ) }
            }
            CalculatedState.Preheating -> {
                    _cookDashboardUiState.update {
                    it.copy(
                        isTimedCook = grillState.isTimedCook(),
                        isOnline = true,
                        levelListBackground = R.drawable.level_list_preheat,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_preheat_background,
                        selectedThermometerTabDrawableBackground = R.drawable.preheat_thermometer_selector_background_plugged_in,
                        unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                        skipPreheatButtonVisible = true,
                        dialState = DialState.Preheating,
                        progressSubVisibility = false,
                        probeOneCardEnabled = true,
                        probeTwoCardEnabled = true,
                        progress = grillState.preheatProgress.getProgress(),
                        progressDisplayValue = NumberPercent(number = grillState.preheatProgress.toString()),
                        cookComplete = grillState.isCookComplete(),
                        showDialState = true
                    )
                }
            }

            CalculatedState.Igniting -> {
                 _cookDashboardUiState.update {
                    it.copy(
                        isTimedCook = grillState.isTimedCook(),
                        isOnline = true,
                        levelListBackground = R.drawable.level_list_preheat,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_preheat_background,
                        selectedThermometerTabDrawableBackground = R.drawable.preheat_thermometer_selector_background_plugged_in,
                        unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                        skipPreheatButtonVisible = true,
                        showDialState = true,
                        dialState = DialState.Ignition,
                        progressSubVisibility = false,
                        progress = grillState.ignitionProgress.getProgress(),
                        progressDisplayValue = NumberPercent(number = grillState.ignitionProgress.toString()),
                        cookComplete = grillState.isCookComplete()
                    )
                }
            }

            CalculatedState.AddFood -> {
                _cookDashboardUiState.update {
                    it.copy(
                        isOnline = true,
                        isTimedCook = grillState.isTimedCook(),
                        levelListBackground = R.drawable.level_list_cook,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_cook_background,
                        selectedThermometerTabDrawableBackground = R.drawable.cook_thermometer_selector_background_plugged_in,
                        unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                        skipPreheatButtonVisible = false,
                        dialState = DialState.AddFood,
                        showDialState = false,
                        progressSubVisibility = false,
                        probeOneCardEnabled = true,
                        probeTwoCardEnabled = true,
                        progress = 0,
                        progressDisplayValue = SimpleStringProgressDial(R.string.add_food),
                        cookComplete = grillState.isCookComplete()
                    )
                }
            }

            CalculatedState.Cooking -> {
                probe?.let { gt ->
                    _cookDashboardUiState.update {
                        it.copy(
                            isOnline = true,
                            isTimedCook = false,
                            levelListBackground = R.drawable.level_list_cook,
                            selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_cook_background,
                            selectedThermometerTabDrawableBackground = R.drawable.cook_thermometer_selector_background_plugged_in,
                            unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                            skipPreheatButtonVisible = false,
                            showDialState = gt.active,
                            dialState = DialState.Cooking,
                            progressSubVisibility = true,
                            progressSubText = if (gt.active) R.string.current_temp else R.string.not_set,
                            probeOneCardEnabled = true,
                            probeTwoCardEnabled = true,
                            progress = if (gt.active) gt.cookProgress.getProgress() else 0,
                            progressDisplayValue = if (gt.active || gt.pluggedIn) NumberTempUnit(number = gt.currentTemp.toString(), tempUnit = userTemperatureUnit.value.getDisplayName()) else SimpleStringProgressDial(R.string.double_dash),
                            cookComplete = grillState.isCookComplete()
                        )
                    }
                } ?: _cookDashboardUiState.update {
                    it.copy(
                        isOnline = true,
                        isTimedCook = true,
                        levelListBackground = R.drawable.level_list_cook,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_cook_background,
                        selectedThermometerTabDrawableBackground = R.drawable.cook_thermometer_selector_background_plugged_in,
                        unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                        skipPreheatButtonVisible = false,
                        showDialState = true,
                        dialState = DialState.Cooking,
                        progressSubVisibility = true,
                        progressSubText = R.string.until_complete,
                        progress = grillState.cookProgress.getProgress(),
                        progressDisplayValue = TimerStringProgressDial(grillState.oven.timeLeft.toTimeDisplayProgressDial(
                            grillState.cookMode
                        )),
                        cookComplete = false
                    )
                }
            }

            CalculatedState.FlipFood -> {
                _cookDashboardUiState.update {
                    it.copy(
                        isTimedCook = grillState.isTimedCook(),
                        isOnline = true,
                        levelListBackground = R.drawable.level_list_cook,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_cook_background,
                        selectedThermometerTabDrawableBackground = R.drawable.cook_thermometer_selector_background_plugged_in,
                        unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                        skipPreheatButtonVisible = false,
                        showDialState = false,
                        dialState = DialState.Cooking,
                        progressSubVisibility = probe?.let { gt -> !gt.active } ?: false,
                        progressSubText = R.string.not_set,
                        probeOneCardEnabled = true,
                        probeTwoCardEnabled = true,
                        progress = probe?.let { gt -> if (gt.active) gt.cookProgress else 0 }
                            ?: grillState.cookProgress,
                        progressDisplayValue = SimpleStringProgressDial(stringId = if(probe != null && probe.active.not()) R.string.double_dash else R.string.flip_food),
                        cookComplete = false
                    )
                }
            }

            CalculatedState.GetFood -> {
                probe?.let { gt ->
                    _cookDashboardUiState.update {
                        it.copy(
                            isTimedCook = false,
                            isOnline = true,
                            levelListBackground = R.drawable.level_list_rest,
                            selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_resting_background,
                            selectedThermometerTabDrawableBackground = R.drawable.resting_thermometer_selector_background_plugged_in,
                            unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                            skipPreheatButtonVisible = false,
                            progressSubVisibility = true,
                            progressSubText = if (gt.active) R.string.get_food else R.string.not_set,
                            showDialState = gt.active,
                            dialState = DialState.Resting,
                            probeOneCardEnabled = true,
                            probeTwoCardEnabled = true,
                            progress = if (gt.active) grillState.restingProgress else 0,
                            progressDisplayValue = if (gt.active) gt.getRestingDisplayValue() else SimpleStringProgressDial(R.string.double_dash),
                            cookComplete = grillState.isCookComplete()
                        )
                    }
                } ?: _cookDashboardUiState.update {
                    it.copy(
                        isTimedCook = true,
                        isOnline = true,
                        levelListBackground = R.drawable.level_list_complete,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_done_background,
                        selectedThermometerTabDrawableBackground = R.drawable.done_thermometer_selector_background_plugged_in,
                        unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                        skipPreheatButtonVisible = false,
                        progressSubVisibility = true,
                        progressSubText = R.string.get_food,
                        showDialState = true,
                        dialState = DialState.Complete,
                        probeOneCardEnabled = true,
                        probeTwoCardEnabled = true,
                        progress = 100,
                        progressDisplayValue = SimpleStringProgressDial(R.string.done),
                        cookComplete = grillState.isCookComplete()
                    )
                }
            }

            CalculatedState.Resting -> {
                probe?.let { gt ->
                    _cookDashboardUiState.update {
                        it.copy(
                            isTimedCook = false,
                            isOnline = true,
                            levelListBackground = R.drawable.level_list_rest,
                            selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_resting_background,
                            selectedThermometerTabDrawableBackground = R.drawable.resting_thermometer_selector_background_plugged_in,
                            unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                            skipPreheatButtonVisible = false,
                            showDialState = gt.active,
                            dialState = DialState.Resting,
                            progressSubVisibility = gt.active.not(),
                            progressSubText = R.string.not_set,
                            probeOneCardEnabled = true,
                            probeTwoCardEnabled = true,
                            progress = if (gt.active) gt.restingProgress.getProgress() else 0,
                            progressDisplayValue = if (gt.active) gt.getRestingDisplayValue() else SimpleStringProgressDial(R.string.double_dash),
                            cookComplete = grillState.isCookComplete()
                        )
                    }
                }
            }

            CalculatedState.Done -> {
                _cookDashboardUiState.update {
                    it.copy(
                        isTimedCook = grillState.isTimedCook(),
                        isOnline = true,
                        levelListBackground = R.drawable.level_list_complete,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_done_background,
                        selectedThermometerTabDrawableBackground = R.drawable.done_thermometer_selector_background_plugged_in,
                        unselectedThermometerTabDrawableBackground = R.drawable.thermometer_selector_background_un_plugged,
                        skipPreheatButtonVisible = false,
                        showDialState = probe?.let { gt -> gt.active } ?: true,
                        dialState = DialState.Complete,
                        progressSubVisibility = false,
                        probeOneCardEnabled = true,
                        probeTwoCardEnabled = true,
                        progress = probe?.let { gt -> if (gt.active) 100 else 0 } ?: 100,
                        progressDisplayValue = SimpleStringProgressDial(stringId = if(probe != null && probe.active.not()) R.string.double_dash else R.string.done),
                        cookComplete = grillState.isCookComplete()
                    )
                }
            }

            CalculatedState.PlugInProbe1 -> {
                probe?.let { gt ->
                    _cookDashboardUiState.update {
                        it.copy(
                            isTimedCook = false,
                            isOnline = true,
                            dialState = DialState.Cooking,
                            progressSubVisibility = true,
                            progressSubText = if (gt.active) R.string.unplugged else R.string.not_set,
                            showDialState = gt.active,
                            probeOneCardEnabled = false,
                            progress = 0,
                            progressDisplayValue = if (gt.active) SimpleStringProgressDial(R.string.double_dash) else if (gt.pluggedIn) NumberTempUnit(number = gt.currentTemp.toString(), tempUnit = userTemperatureUnit.value.getDisplayName()) else SimpleStringProgressDial(R.string.double_dash)
                        )
                    }
                }
            }

            CalculatedState.PlugInProbe2 -> {
                probe?.let { gt ->
                    _cookDashboardUiState.update {
                        it.copy(
                            isTimedCook = false,
                            isOnline = true,
                            dialState = DialState.Cooking,
                            progressSubVisibility = true,
                            progressSubText = if (gt.active) R.string.unplugged else R.string.not_set,
                            showDialState = gt.active,
                            probeTwoCardEnabled = false,
                            progress = 0,
                            progressDisplayValue = if (gt.active) SimpleStringProgressDial(R.string.double_dash) else if (gt.pluggedIn) NumberTempUnit(number = gt.currentTemp.toString(), tempUnit = userTemperatureUnit.value.getDisplayName()) else SimpleStringProgressDial(R.string.double_dash)
                        )
                    }
                }
            }

            else -> {}
        }

    }

    private fun setCookDashboardBackgroundOffline(grillState: GrillState) {
        val probe = grillState.getSelectedProbe()
        probe?.let { gt ->
            _cookDashboardUiState.update {
                it.copy(
                    isTimedCook = false,
                    levelListBackground = R.drawable.level_list_offline,
                    selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_offline_background,
                    selectedThermometerTabDrawableBackground = R.drawable.offline_thermometer_selector_background,
                    unselectedThermometerTabDrawableBackground = R.drawable.offline_unselecteed_thermometer_background,
                    skipPreheatButtonVisible = false,
                    showDialState = true,
                    dialState = DialState.Offline,
                    progressSubVisibility = true,
                    progressSubText = R.string.cannot_read_temp,
                    progress = 0,
                    progressDisplayValue = SimpleStringProgressDial(R.string.double_dash),
                    cookComplete = grillState.isCookComplete(),
                    isOnline = grillState.connectedToInternet
                )
            }
        } ?: when (grillState.state) {
            CalculatedState.Preheating -> {
                _cookDashboardUiState.update {
                    it.copy(
                        isTimedCook = true,
                        levelListBackground = R.drawable.level_list_offline,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_offline_background,
                        selectedThermometerTabDrawableBackground = R.drawable.offline_thermometer_selector_background,
                        unselectedThermometerTabDrawableBackground = R.drawable.offline_unselecteed_thermometer_background,
                        skipPreheatButtonVisible = false,
                        showDialState = true,
                        dialState = DialState.Offline,
                        progressSubVisibility = true,
                        progressSubText = R.string.cannot_read_temp,
                        isOnline = grillState.connectedToInternet
                    )
                }
            }

            CalculatedState.Igniting -> {
                _cookDashboardUiState.update {
                    it.copy(
                        isTimedCook = true,
                        levelListBackground = R.drawable.level_list_offline,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_offline_background,
                        selectedThermometerTabDrawableBackground = R.drawable.offline_thermometer_selector_background,
                        unselectedThermometerTabDrawableBackground = R.drawable.offline_unselecteed_thermometer_background,
                        skipPreheatButtonVisible = false,
                        showDialState = true,
                        dialState = DialState.Offline,
                        progressSubVisibility = true,
                        progressSubText = R.string.cannot_read_temp,
                        isOnline = grillState.connectedToInternet
                    )
                }
            }

            CalculatedState.Cooking -> {
                _cookDashboardUiState.update {
                    it.copy(
                        isTimedCook = true,
                        levelListBackground = R.drawable.level_list_offline,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_offline_background,
                        selectedThermometerTabDrawableBackground = R.drawable.offline_thermometer_selector_background,
                        unselectedThermometerTabDrawableBackground = R.drawable.offline_unselecteed_thermometer_background,
                        skipPreheatButtonVisible = false,
                        showDialState = true,
                        dialState = DialState.Offline,
                        progressSubVisibility = true,
                        progressSubText = R.string.until_complete,
                        progress = grillState.cookProgress,
                        progressDisplayValue = TimerStringProgressDial(grillState.oven.timeLeft.toTimeDisplayProgressDial(
                            grillState.cookMode
                        )),
                        isOnline = grillState.connectedToInternet
                    )
                }
            }

            CalculatedState.GetFood -> {
                _cookDashboardUiState.update {
                    it.copy(
                        isTimedCook = true,
                        levelListBackground = R.drawable.level_list_offline,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_offline_background,
                        selectedThermometerTabDrawableBackground = R.drawable.offline_thermometer_selector_background,
                        unselectedThermometerTabDrawableBackground = R.drawable.offline_unselecteed_thermometer_background,
                        skipPreheatButtonVisible = false,
                        showDialState = true,
                        dialState = DialState.Offline,
                        progressSubVisibility = true,
                        progressSubText = R.string.get_food,
                        progress = 0,
                        progressDisplayValue = SimpleStringProgressDial(R.string.done),
                        isOnline = grillState.connectedToInternet
                    )
                }
            }

            CalculatedState.Done -> {
                _cookDashboardUiState.update {
                    it.copy(
                        isTimedCook = true,
                        levelListBackground = R.drawable.level_list_offline,
                        selectedCookTypeTabBackground = R.drawable.selected_cook_type_tab_offline_background,
                        selectedThermometerTabDrawableBackground = R.drawable.offline_thermometer_selector_background,
                        unselectedThermometerTabDrawableBackground = R.drawable.offline_unselecteed_thermometer_background,
                        skipPreheatButtonVisible = false,
                        showDialState = true,
                        dialState = DialState.Offline,
                        progressSubVisibility = true,
                        progress = 0,
                        progressDisplayValue = SimpleStringProgressDial(R.string.done),
                        isOnline = grillState.connectedToInternet
                    )
                }
            }

            else -> {}
        }
    }

    fun selectMiniThermometer(isTwoSelected: Boolean) {
        _cookDashboardUiState.update {
            it.copy(
                isThermometer2Selected = isTwoSelected
            )
        }
        setCookDashboardBackground(_selectedGrillState.value)
    }

    private fun checkUnSelectedProbeHasReachedTemp(grillState: GrillState) {
        if (cookDashboardUiState.value.isThermometer2Selected) {
            if (grillState.probe1.state == CalculatedState.Done) {
                _infoAction.value = CookDashboardInfoAction.ShowProbeOneDoneModal
            }
        } else {
            if (grillState.probe2.state == CalculatedState.Done) {
                _infoAction.value = CookDashboardInfoAction.ShowProbeTwoDoneModal
            }
        }
    }

    private suspend fun checkProbePluggedInStateActiveCook(state: CalculatedState) {
        if (state == CalculatedState.PlugInProbe1) {
            if (_plugInTherm1HasShown.value.not()) {
                _infoAction.emit(CookDashboardInfoAction.ShowThermometer1NotPluggedIn)
            }
        } else {
            _plugInTherm1HasShown.emit(false)
        }
        if (state == CalculatedState.PlugInProbe2) {
            if (_plugInTherm2HasShown.value.not()) {
                _infoAction.emit(CookDashboardInfoAction.ShowThermometer2NotPluggedIn)
            }
        } else {
            _plugInTherm2HasShown.emit(false)
        }
    }

    private fun checkProbePluggedInStatePreCook(probe0PluggedIn: Boolean, probe1PluggedIn: Boolean ) {
        _preCookDashboardUiState.update { it.copy(
            probe0PluggedIn = probe0PluggedIn,
            probe1PluggedIn = probe1PluggedIn
        )}
    }

    private fun GrillState.isCookComplete(): Boolean {
           return state == CalculatedState.Done || state == CalculatedState.GetFood || state == CalculatedState.Resting
    }

    fun isGrillIgniting() = selectedGrillState.value.state == CalculatedState.Igniting

    fun isPreheating() = selectedGrillState.value.state == CalculatedState.Preheating

    fun secondsToHoursAndMins(time: Int) : String {
        val hours = (time / 3600).toInt()
        var minutes = (time / 60).toInt()
        var seconds = time - (60 * minutes)

        return if (hours > 0) {
            minutes = ((time - (3600 * hours)) / 60).toInt()
            if (minutes > 0) {
                "$hours hours and $minutes minutes"
            } else {
                "$hours h"
            }
        } else {
            if (seconds > 0) {
                "$minutes minutes and $seconds seconds"
            } else {
                "$minutes m"
            }
        }
    }

    fun stopCooking() {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.stopCook()
                .catch {
                    _infoAction.emit(CookDashboardInfoAction.StopCookError)
                    Log.e("homeViewModelGrill", "stopCooking: ${it.message}")
                }
                .collect {
                    _infoAction.emit(CookDashboardInfoAction.StopCook)
                    Log.d("homeViewModelGrill", "stopCooking: STOPPED COOKING")
                }
        }
    }

    fun skipPreheat() {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.skipPreheat()
                .catch { error ->
                    Log.e(
                        HomeViewModel::class.java.name,
                        "skipPreheat: ${error.message}"
                    )
                }
                .collect {
                    Log.d(HomeViewModel::class.java.name, "skipPreheat: SUCCESS")
                }
        }
    }

    fun setDashboardTabSelectedState(selectedTab: CookDashboardTabs) {
        _dashboardTabSelectedState.value = selectedTab
        // set back to default
        _cookMode.value = CookMode.Grill
        _preCookDashboardUiState.update { it.copy(
            grillTemp = "3"
        ) }
        _preCookDashboardButtonState.update { it.copy(
            isTimedCook = selectedTab == CookDashboardTabs.TimeCook
        ) }
    }

    fun getUserTemperatureUnit() {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.getUserTemperatureUnit()
                .catch { error ->
                    Log.e(HomeViewModel::class.java.name, "getUserTemperatureUnit: ${error.message}")
                }
                .collectLatest {
                _userTemperatureUnit.value = it
            }
        }
    }

    fun setTherm1NotPluggedInHasShown(hasShown: Boolean) {
        _plugInTherm1HasShown.value = hasShown
    }

    fun setTherm2NotPluggedInHasShown(hasShown: Boolean) {
        _plugInTherm2HasShown.value = hasShown
    }

    fun saveProbe0Selection(foodItem: FoodTemperatureItem) {
        _preCookDashboardUiState.value = _preCookDashboardUiState.value.copy(
            probe0FoodTempValue = foodItem.tempString,
            probe0Doneness = foodItem.donenessLevel
        )
        var preset = foodItem.presetIndex
        if (_probe0FoodPreset.value.food == Food.Manual) {
            preset = -1
        }
        _probe0FoodPreset.update { it.copy(
            preset = preset
        ) }
    }

    fun updateIsProbe0ToggleStatus(isActive: Boolean) {
        _isProbe0ToggleOn.value = isActive
        _preCookDashboardButtonState.update { it.copy(
            isProbeOneToggleOn = isActive
        ) }
    }

    fun updateIsProbe1ToggleStatus(isActive: Boolean) {
        _isProbe1ToggleOn.value = isActive
        _preCookDashboardButtonState.update { it.copy(
            isProbeTwoToggleOn = isActive
        ) }
    }

    fun updateWoodfireToggleStatus(isChecked: Boolean) {
        _isWoodFireToggleOn.value = isChecked
        _preCookDashboardButtonState.update { it.copy(
            isSmokeFeatureOn = isChecked
        )}
    }

    fun saveThermometerCookGrillTemp(temp: String) {
        _preCookDashboardUiState.value = _preCookDashboardUiState.value.copy(
            grillTemp = temp
        )
    }

    fun saveGrillTempAndTime(time: Int, temp: String) {
        _preCookDashboardUiState.update {
            it.copy(
                grillTemp = temp,
                duration = time
            )
        }
    }

    fun saveTimeCookGrillTimeAndTemp(timeLeft: Int, timeRight: Int, temp: String) {
        _preCookDashboardUiState.update {
            it.copy(
                grillTemp = temp,
                duration = timeLeft + timeRight
            )
        }

        _editScreenDuration.update {
            it.copy(
                valueLeft = timeLeft / 60,
                valueRight = timeRight
            )
        }
    }

    fun saveProbe1Selection(foodItem: FoodTemperatureItem) {
        _preCookDashboardUiState.value = _preCookDashboardUiState.value.copy(
            probe1FoodTempValue = foodItem.tempString,
            probe1Doneness = foodItem.donenessLevel
        )

        var preset = foodItem.presetIndex
        if (_probe1FoodPreset.value.food == Food.Manual) {
            preset = -1
        }
        _probe1FoodPreset.update { it.copy(
            preset = preset
        ) }
    }

    fun getShouldShowPlaceYourThermometerDialog() {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.getShouldShowPlaceYourThermometerDialog()
                .catch { error -> Log.e("CookViewModel", error.message.toString()) }
                .collect {
                    _shouldShowPlaceYourThermometerDialog.emit(it)
                }
        }
    }

    fun setShouldShowPlaceYourThermometerDialog(shouldShow: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _shouldShowPlaceYourThermometerDialog.emit(shouldShow)
            cookService.setShowPlaceYourThermometerDialog(shouldShow)
                .catch { error ->
                    Log.e("CookViewModel", error.message.toString())
                }
                .collect()
        }
    }

    private fun setCookMode(localCookMode: CookMode) {
        _cookMode.value = localCookMode
    }

    fun selectDashboardCookMode(mCookMode: CookMode) {
        _isWoodFireToggleOn.value = false
        _cookMode.value = mCookMode

        _editScreenDuration.update {
            it.copy(
                valueLeft = null,
                valueRight = null
            )
        }
    }

    fun setCookModeFromCharts(localCookMode: CookMode) {
        _cookMode.value = localCookMode
    }

    fun showAccessoryModal() {
        _infoAction.value = CookDashboardInfoAction.ShowAccessoryModal
    }

    fun updateAction(event: CookDashboardInfoAction) {
        _infoAction.value = event
    }

    fun selectFoodProbe0(food: Food) {
        val newFood = if (food == Food.NotSet) Food.Manual else food
        if (newFood != _probe0FoodPreset.value.food) {
            _probe0FoodPreset.value = _probe0FoodPreset.value.copy(
                food = newFood
            )

            if (food == Food.Manual) {
                _probe0FoodPreset.value = _probe0FoodPreset.value.copy(
                    preset = -1
                )
            }
            updateIsProbe0ToggleStatus(false)
        }
    }

    private fun setDashboardTabToTimedCook() {
        _dashboardTabSelectedState.value = CookDashboardTabs.TimeCook
        _preCookDashboardButtonState.update { it.copy(
            isTimedCook = true
        ) }
    }

    fun canStartCookFromCharts(): Boolean {
        return selectedGrillState.value.state != CalculatedState.PoweredOff
                && (selectedGrillState.value.connectedToInternet || selectedGrillState.value.connectedToBluetooth)
    }

    fun saveCookingChart() {
            setDashboardTabToTimedCook()
            selectDashboardCookMode(selectedChartFlow.value.cookingMethod.getCookModeFromCookMethod())

            var timeUnit = ""

            runBlocking {
                timeUnit = Cook.getAvailableTimesUnit(cookMode.value)
            }

            if (selectedChartFlow.value.grillTemperatureF == 0) {
                val temp = when (selectedChartFlow.value.grillTemperatureGeneric) {
                    "HI" -> 3
                    "MED" -> 2
                    "LO" -> 1
                    else -> 3
                }

                if (timeUnit == "Minutes") {
                    saveGrillTempAndTime((selectedChartFlow.value.cookTime.toInt()/60).toInt(), temp.toString())
                } else {
                    saveGrillTempAndTime(selectedChartFlow.value.cookTime.toInt(), temp.toString())
                }

            } else {
                if (timeUnit == "Minutes") {
                    saveGrillTempAndTime((selectedChartFlow.value.cookTime.toInt()/60).toInt(), selectedChartFlow.value.grillTemperatureF.toString())
                } else {
                    saveGrillTempAndTime(selectedChartFlow.value.cookTime.toInt(), selectedChartFlow.value.grillTemperatureF.toString())
                }
            }

            if (selectedChartFlow.value.cookingMethod.lowercase() != CookMode.Broil.toString().lowercase()) {
                updateWoodfireToggleStatus(smokeFlavorFilter)
            } else {
                updateWoodfireToggleStatus(false)
            }
    }

    fun selectFoodProbe1(food: Food) {
        val newFood = if (food == Food.NotSet) Food.Manual else food
        if (newFood != _probe1FoodPreset.value.food) {
            _probe1FoodPreset.value = _probe1FoodPreset.value.copy(
                food = newFood
            )

            if (food == Food.Manual) {
                _probe1FoodPreset.value = _probe1FoodPreset.value.copy(
                    preset = -1
                )
            }
            updateIsProbe1ToggleStatus(false)
        }
    }

//GrillCore Integration

    fun fetchGrills() {
        viewModelScope.launch(Dispatchers.IO) {
            wifiPairingService.fetchGrills().collect {
                _grills.emit(it)
            }
        }
    }

    suspend fun getCachedGrills() {
        _grills.emit(wifiPairingService.getGrills())
    }

    fun getCurrentGrill() {
        viewModelScope.launch(Dispatchers.IO) {
            wifiPairingService.getCurrentGrill().collect {
                it?.let { _currentGrill.emit(it) }
            }
        }
    }

    fun setCurrentGrillGC(grill: Grill) {
        viewModelScope.launch(Dispatchers.IO) {
            wifiPairingService.setSelectedGrill(grill).catch {}
                .collect {
                    _currentGrill.emit(it)
                }
        }
    }

    fun resetCurrentGrillOnDeleteAppliance() {
        _currentGrill.value = null
    }

    fun isProbeSet(probeNumber: Int): Boolean {
        return if(probeNumber == PROBE_0) {
            _preCookDashboardUiState.value.probe0FoodTempValue != PROBE_NOT_SET
        } else {
            _preCookDashboardUiState.value.probe1FoodTempValue != PROBE_NOT_SET
        }
    }

    fun getProbeDefaultValue() = if(userTemperatureUnit.value == DegreeType.Fahrenheit) PROBE_DEFAULT_US else PROBE_DEFAULT_EU

    private fun probe0Temp(): Int {
       return if(isProbeSet(PROBE_0)) {
            preCookDashboardUiState.value.probe0FoodTempValue
        } else {
            getProbeDefaultValue()
        }.toInt()
    }

    private fun probe1Temp(): Int {
        return if(isProbeSet(PROBE_1)) {
            preCookDashboardUiState.value.probe1FoodTempValue
        } else {
            getProbeDefaultValue()
        }.toInt()
    }



    fun getFoodListProbe(food: Food) {
        viewModelScope.launch(Dispatchers.IO) {
            val mFood = if (food == Food.NotSet) Food.Manual else food
            cookService.getGrillPresetsForFoodType(mFood).collect { list ->
                val tempList = list.map {
                    FoodTemperatureItem(
                        tempString = it.temp.toString(),
                        isSelected = false,
                        donenessLevel = it.doneness,
                        isIncrementNumber = (it.temp % 5 == 0),
                        presetIndex = it.presetIndex
                    )
                }
                _probeFoodList.emit(tempList)
                _preCookDashboardUiState.value.copy(
                    probe0FoodTempValue = if (tempList.isNotEmpty()) tempList[0].tempString else "165",
                    probe1FoodTempValue = if (tempList.isNotEmpty()) tempList[0].tempString else "165"
                )
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeGrillTempList() {
        viewModelScope.launch {
            _cookMode.distinctUntilChangedBy { _cookMode.value }
                .flatMapLatest { cookMode ->
                    cookService.getGrillTempsForCookMode(cookMode)
                }.collectLatest { grillTemps ->
                    val tempList =
                        when (_cookMode.value) {
                            CookMode.Grill -> {

                                grillTemps.map { temperature ->
                                    GrillTemperatureItem(
                                        tempString = temperature.toString(),
                                        isSelected = false,
                                        temp = temperature
                                    )
                                }
                            }

                            else -> {

                                grillTemps.map { temperature ->
                                    GrillTemperatureItem(
                                        tempString = temperature.toString(),
                                        isSelected = false,
                                        temp = temperature
                                    )
                                }
                            }
                        }

                    _timeCookTempList.emit(tempList)
                    _thermometerCookTempList.emit(tempList)
                    _preCookDashboardButtonState.update { it.copy(
                        cookMode = _cookMode.value
                    ) }
                }
        }
    }

    suspend fun getRegionSelection(): Flow<RegionCode> {
        return cookService.getSelectedRegionCode()
    }

    private fun fetchRegion() {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.getSelectedRegionCode()
                .catch {  }
                .collect {
                    _regionCode.emit(it)
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTimeList() {
        viewModelScope.launch {
            _cookMode.distinctUntilChangedBy { _cookMode.value }
                .flatMapLatest { cookMode ->
                    cookService.getGrillTimesForCookMode(cookMode)
                        .catch { error -> Log.e("CookViewModel", error.message.toString()) }
                }.collectLatest { timeList ->

                    val timerMap = timeListToMap(timeList)
                    _timerMap.emit(timerMap)
                }
        }
    }

    fun observeCookBannerNotifications() {
        notificationJob = Job()
        observeTimedCookNotifications()
        observeProbe1CookNotifications()
        observeProbe2CookNotifications()
    }

    fun cancelCookBannerNotificationJob() {
        _bannerEvent.value = BannerEvent.ActionProcessed
        notificationJob.cancel()
    }

    private fun observeTimedCookNotifications() {
        viewModelScope.launch(Dispatchers.IO + notificationJob) {
            selectedGrillState.distinctUntilChangedBy { selectedGrillState.value.state }.filter {
                it.cookType == CookType.Timed
            }.collect {
                checkNotificationsForCookType(it.state, false)
            }
        }
    }

    private fun observeProbe1CookNotifications() {
        viewModelScope.launch(Dispatchers.IO + notificationJob) {
            selectedGrillState.distinctUntilChangedBy { selectedGrillState.value.probe1.state }
                .filter {
                    it.cookType != CookType.Timed
                }.collect {
                checkNotificationsForCookType(it.probe1.state, true)
            }
        }
    }

    private fun observeProbe2CookNotifications() {
        viewModelScope.launch(Dispatchers.IO + notificationJob) {
            selectedGrillState.distinctUntilChangedBy { selectedGrillState.value.probe2.state }
                .filter {
                    it.cookType != CookType.Timed
                }.collect {
                checkNotificationsForCookType(it.probe2.state, false)
            }
        }
    }

    private fun calculateCookFilter() {
        cookFilter = (grillCookFilter || smokeCookFilter ||
                airCrispCookFilter || roastCookFilter || bakeCookFilter||
                broilCookFilter || dehydrateCookFilter)
    }

    private fun calculateAnyFilters() {
        anyFilters = (timeFilter || cookFilter || foodFilter || smokeFlavorFilter)
    }

    private fun calculateTimeFilter() {
        timeFilter = (fifteenMinBox ||
                fifteenToThirtyMinBox ||
                thirtyToSixtyMinBox ||
                sixtyToOneTwentyMinBox ||
                oneTwentyPlusBox)
    }

    private fun calculateFoodFilter() {
        foodFilter = (chickenFoodFilter || beefFoodFilter || porkFoodFilter ||
                fishFoodFilter || veggieFoodFilter || breadFoodFilter ||
                fruitFoodFilter || otherFoodFilter || lambFoodFilter)
    }

    fun checkCurrentCookModeAndMeatTypeForCharts(meatType: String): Boolean {
        var chartFound = false

        if (cookMode.value == CookMode.AirCrisp) {
            for (chart in allCharts) {
                if ((chart.cookingMethod.lowercase() == CookTypeStrings.airCrisp) && (chart.meatType.lowercase() == meatType)) {
                    chartFound = true
                    break
                }
            }
        } else {
            for (chart in allCharts) {
                if ((chart.cookingMethod.lowercase() == cookMode.value.toString().lowercase()) && (chart.meatType.lowercase() == meatType)) {
                    chartFound = true
                    break
                }
            }
        }

        return chartFound
    }

    fun checkCurrentCookModeAndOtherMeatTypeForCharts(): Boolean {
        var chartFound = false

        if (cookMode.value == CookMode.AirCrisp) {
            for (chart in allCharts) {
                if ((chart.cookingMethod.lowercase() == CookTypeStrings.airCrisp) && (isOtherFoodType(chart.meatType.lowercase()))) {
                    chartFound = true
                    break
                }
            }
        } else {
            for (chart in allCharts) {
                if ((chart.cookingMethod.lowercase() == cookMode.value.toString().lowercase()) && (isOtherFoodType(chart.meatType.lowercase()))) {
                    chartFound = true
                    break
                }
            }
        }

        return chartFound
    }

    fun checkIfModeHasChart(mode: CookMode): Boolean {
        var chartFound = false

        if (mode == CookMode.AirCrisp) {
            for (chart in allCharts) {
                if (chart.cookingMethod.lowercase() == CookTypeStrings.airCrisp) {
                    chartFound = true
                    break
                }
            }
        } else {
            for (chart in allCharts) {
                if (chart.cookingMethod.lowercase() == mode.toString().lowercase()) {
                    chartFound = true
                    break
                }
            }
        }

        return chartFound
    }

    fun filterCharts() {
        calculateCookFilter()
        calculateFoodFilter()
        calculateTimeFilter()
        calculateAnyFilters()
        filteredCharts = mutableListOf<CookingCard>()

        if (foodFilter || cookFilter || timeFilter || smokeFlavorFilter) {
            var filteredByMeatCharts = mutableListOf<CookingCard>()
            var filteredByCookingTypeCharts = mutableListOf<CookingCard>()
            var filteredByTimeCharts = mutableListOf<CookingCard>()
            var filteredBySmokeCharts = mutableListOf<CookingCard>()

            if (foodFilter) {
                for (chart in allCharts) {
                    if (chickenFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.chicken) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (beefFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.beef) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (porkFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.pork) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (fishFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.seafood) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (veggieFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.veggies) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (breadFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.bread) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (fruitFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.fruit) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (lambFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.lamb) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (otherFoodFilter) {
                        if (isOtherFoodType(chart.meatType.lowercase())) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                }
            } else {
                filteredByMeatCharts = allCharts
            }

            if (cookFilter) {
                for (chart in filteredByMeatCharts) {
                    if (grillCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.grill) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (smokeCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.smoke) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (airCrispCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.airCrisp) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (roastCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.roast) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (bakeCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.bake) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (broilCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.broil) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (dehydrateCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.dehydrate) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (reheatCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.reheat) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                }
            } else {
                filteredByCookingTypeCharts = filteredByMeatCharts
            }

            if (timeFilter) {
                for (chart in filteredByCookingTypeCharts) {
                    if (fifteenMinBox) {
                        if (chart.cookTime.toInt() < 900) {
                            filteredByTimeCharts.add(chart)
                        }
                    }
                    if (fifteenToThirtyMinBox) {
                        if (chart.cookTime.toInt() in 900..1799) {
                            filteredByTimeCharts.add(chart)
                        }
                    }
                    if (thirtyToSixtyMinBox) {
                        if (chart.cookTime.toInt() in 1800..3599) {
                            filteredByTimeCharts.add(chart)
                        }
                    }
                    if (sixtyToOneTwentyMinBox) {
                        if (chart.cookTime.toInt() in 3600..7199) {
                            filteredByTimeCharts.add(chart)
                        }
                    }
                    if (oneTwentyPlusBox) {
                        if (chart.cookTime.toInt() >= 7200) {
                            filteredByTimeCharts.add(chart)
                        }
                    }
                }
            } else {
                filteredByTimeCharts = filteredByCookingTypeCharts
            }

            if (smokeFlavorFilter) {
                for (chart in filteredByTimeCharts) {
                    if (chart.cookMode.toString().lowercase() != CookTypeStrings.broil) {
                        filteredBySmokeCharts.add(chart)
                    }
                }
            } else {
                filteredBySmokeCharts = filteredByTimeCharts
            }

            filteredCharts = filteredBySmokeCharts
        }
    }

    fun isOtherFoodType(type: String): Boolean {
        return type != FoodTypeStrings.chicken &&
                type != FoodTypeStrings.beef &&
                type != FoodTypeStrings.pork &&
                type != FoodTypeStrings.seafood &&
                type != FoodTypeStrings.veggies &&
                type != FoodTypeStrings.bread &&
                type != FoodTypeStrings.fruit &&
                type != FoodTypeStrings.lamb
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTimeUnit() {
        viewModelScope.launch() {
            _cookMode.distinctUntilChangedBy { _cookMode.value }
                .flatMapLatest { cookMode ->
                    cookService.getGrillTimeUnitsForCookMode(cookMode)
                        .catch { error -> Log.e("CookViewModel", error.message.toString()) }
                }.collectLatest {
                    _timeUnit.emit(it)
                }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeDefaultTime() {
        viewModelScope.launch {
            _cookMode.distinctUntilChangedBy { _cookMode.value }
                .flatMapLatest { cookMode ->
                    cookService.getGrillDefaultTimeForCookMode(cookMode)
                        .catch { error -> Log.e("CookViewModel", error.message.toString()) }
                }.collectLatest { defaultDuration ->
                    _preCookDashboardUiState.update {
                        it.copy(
                            duration = defaultDuration
                        )
                    }

                    _editScreenDuration.emit(defaultDuration.toSplitDuration())
                }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeDefaultTemp() {
        viewModelScope.launch {
            _cookMode.distinctUntilChangedBy { _cookMode.value }
                .flatMapLatest { cookMode ->
                    cookService.getGrillDefaultTempForCookMode(cookMode)
                        .catch { error -> Log.e("CookViewModel", error.message.toString()) }
                }.collectLatest {
                    val temp = it.toString()
                    _preCookDashboardUiState.value = _preCookDashboardUiState.value.copy(
                        grillTemp = temp
                    )
                }
        }
    }

//GrillCore Integration


//    suspend fun getGrillCoreCookModes(): Flow<List<CookMode>> {
//        return cookService.getGrillCookModesUSA()
//    }

    suspend fun getGrillCoreCookModeTemps(cookMode: CookMode): Flow<List<Int>> {
        return cookService.getGrillTempsForCookMode(cookMode)
    }

    suspend fun setGrillCookMode(): Flow<Cook> {
        return cookService.setGrillCookMode(currentCookMode)
    }

    suspend fun getGrillCoreDefaultCookModeTemp(): Flow<Int> {
        return cookService.getGrillDefaultTempForCookMode(currentCookMode)
    }

    suspend fun getTemperatureUnits(): Flow<String> {
        return cookService.getGrillTempUnitsForCookMode(currentCookMode)
    }

    suspend fun setCurrentGrillCoreGrill(grill: Grill): Flow<Grill?> {
        return cookService.setSelectedGrill(grill)
    }

    fun sendCookCommandToGrillCore() {
        viewModelScope.launch(Dispatchers.IO) {
            incrementTotalCooks()
            cookService.sendGrillCookCommand(
                cookMode = cookMode.value,
                cookType = _dashboardTabSelectedState.value,
                temperatureThermCook = _preCookDashboardUiState.value.grillTemp.toInt(),
                temperatureTimeCook = _preCookDashboardUiState.value.grillTemp.toInt(),
                duration = preCookDashboardUiState.value.duration,
                smokeFeature = isWoodFireToggleOn.value,
                isProbe0Active = _isProbe0ToggleOn.value,
                isProbe1Active = _isProbe1ToggleOn.value,
                firstProbeTemperature = probe0Temp(),
                secondProbeTemperature = probe1Temp(),
                firstProbePresetIndex = _probe0FoodPreset.value.preset,
                firstProbePresetFood = _probe0FoodPreset.value.food,
                secondProbePresetIndex = _probe1FoodPreset.value.preset,
                secondProbePresetFood = _probe1FoodPreset.value.food
            ).catch {
                _infoAction.emit(CookDashboardInfoAction.StartCookError)
                Log.e(HomeViewModel::class.java.name, "sendCookCommandToGrillCore: ${it.message}")
            }.collect {
//                incrementTotalCooks()
                println("COMMAND SUCCESS")
            }
        }
    }

    // EDIT COOK DURING COOK
    fun editTimeCook(
        cookMode: CookMode,
        temperatureString: String,
        duration: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if(selectedGrillState.value.cookMode != cookMode) {
                updateWoodfireToggleStatus(false)
            }
            cookService.editTimedCook(
                cookMode,
                temperatureString.toInt(),
                duration
            ).collect {
                println("Time Cook Updated")
            }
        }
    }

    fun editThermometerCookSettings(
        cookMode: CookMode,
        temperatureString: String
    ) {
        if(selectedGrillState.value.cookMode != cookMode) {
            updateWoodfireToggleStatus(false)
        }

        viewModelScope.launch(Dispatchers.IO) {
            cookService.editThermometerCookSettings(
                cookMode = cookMode,
                temperature = temperatureString.toInt()
            ).collect {
                println("Grill Thermometer Updated")
            }
        }
    }

    fun editFirstThermometerSettings(
        temperatureString: String,
        probePreset: Food,
        presetIndex: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.editFirstThermometerSettings(
                internalTemperature = temperatureString.toInt(),
                presetIndex = presetIndex,
                presetFood = probePreset
            ).collect {
                println("First Thermometer Updated")
            }
        }
    }

    fun editSecondThermometerSettings(
        temperatureString: String,
        probePreset: Food,
        presetIndex: Int
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            cookService.editSecondThermometerSettings(
                internalTemperature = temperatureString.toInt(),
                presetIndex = presetIndex,
                presetFood = probePreset
            ).collect {
                println("Second Thermometer Updated")
            }
        }
    }

    fun shouldShowThermometerNotPluggedIn(): Boolean {
       return when(selectedGrillState.value.state) {
            CalculatedState.Preheating,
            CalculatedState.Igniting,
            CalculatedState.AddFood -> false
            else -> true
        }
    }

    fun editWoodfireSetting(smokeFeature: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.editWoodfireSetting(smokeFeature)
                .catch {
                    _infoAction.emit(CookDashboardInfoAction.EditWoodFireSettingError)
                    Log.e(HomeViewModel::class.java.name, "editWoodFireSettingsFailed: ${it.message}")
                }
                .collect {
                    _infoAction.emit(CookDashboardInfoAction.EditWoodFireSettingSuccess)
                    println("Smoke Feature Updated")
                    updateWoodfireToggleStatus(smokeFeature)
            }
        }
    }

// Pairing

    suspend fun getIoTDeviceWifiNetworks(): Flow<List<String>> {
        logPairingEvent("${HomeViewModel::class.java.simpleName}: Trying get grill wifi networks...")
        return wifiPairingService.getIoTDeviceWifiNetworks()
            .retry(GRILL_NETWORKS_RETRY_ATTEMPTS) { cause ->
                return@retry if (cause is WifiManagerIoTDeviceWifiScanningService.NoGrillNetworksFoundThrowable) {
                    delay(GRILL_NETWORKS_RETRY_DELAY)
                    Log.d(
                        HomeViewModel::class.java.simpleName,
                        "retrying get grill wifi networks..."
                    )
                    logPairingEvent("${HomeViewModel::class.java.simpleName}: retrying get grill wifi networks...")
                    true
                } else {
                    logPairingEvent("${HomeViewModel::class.java.simpleName}: Error Getting grill wifi networks: ${cause.message}")
                    false
                }
            }
    }

    fun onIoTDeviceWifiNetworkSelected(selection: String) {
        _selectedIoTDeviceWifiNetworkSSID.update { selection }
    }

    fun updateBluetoothPermissionLocation(locationPermission: Boolean) {
        _isBluetoothPermissionGranted.update { it.copy(locationPermission = locationPermission) }
    }

    fun updateBluetoothEnabled(bluetoothEnabled: Boolean) {
        _isBluetoothPermissionGranted.update { it.copy(bluetoothEnabled = bluetoothEnabled) }
    }

    fun updateBluetoothPermissionNearbyDevices(nearbyDevicePermission: Boolean) {
        _isBluetoothPermissionGranted.update { it.copy(nearbyDevicesPermission = nearbyDevicePermission) }
    }

    suspend fun connect(iotDeviceWifiNetworkSSID: String = selectedIoTDeviceWifiNetworkSSID.value!!): Flow<PhoneToDeviceConnectionStatus> {
        return callbackFlow {
            logPairingEvent("${HomeViewModel::class.java.simpleName} - connect() - start: Attempting to connect to grill SSID: $iotDeviceWifiNetworkSSID")
            wifiPairingService.connect(ssid = iotDeviceWifiNetworkSSID)
                .catch { cause: Throwable ->
                    logPairingEvent("${HomeViewModel::class.java.simpleName} - connect(): Failed to connect to ${selectedIoTDeviceWifiNetworkSSID.value}.\n${cause.message}")
                    throw Throwable("${HomeViewModel::class.java.name}: Failed to connect to ${selectedIoTDeviceWifiNetworkSSID.value}.\n${cause.message}")
                }
                .onEach { connectionStatus ->
                    if (connectionStatus is PhoneToDeviceConnectionStatus.Connected) {
                        logPairingEvent("${HomeViewModel::class.java.simpleName} - connect() - onEach(): SUCCESS! connected to grill SSID: $iotDeviceWifiNetworkSSID with GatewayIP: ${connectionStatus.gatewayIpAddress}")
                        this@HomeViewModel.gatewayIpAddress = connectionStatus.gatewayIpAddress
                    } else {
                        logPairingEvent("${HomeViewModel::class.java.simpleName} - connect() - onEach(): $connectionStatus NOT connected to grill SSID: $iotDeviceWifiNetworkSSID")
                    }
                }
                .collect { connectionStatus ->
                    logPairingEvent("${HomeViewModel::class.java.simpleName} - connect() - end: Got connectionStatus: $connectionStatus for trying to connect to grill SSID: $iotDeviceWifiNetworkSSID")
                    trySend(connectionStatus)
                }

            awaitClose()
        }
    }

    fun startPairingIoTDeviceProcess() {
        viewModelScope.launch(Dispatchers.IO) {
            observeWifiNetworksSeenByDevice()
            wifiPairingService.startPairingIoTDeviceProcess(ipAddress = gatewayIpAddress)
        }
    }

    private suspend fun observeWifiNetworksSeenByDevice() {
            wifiPairingService.observeWifiNetworksSeenByDevice()
                .onEach { wifiNetworks ->
                    logPairingEvent("${HomeViewModel::class.java.simpleName}: Device sees ${wifiNetworks.size} Wifi Networks")
                    _wifiNetworksVisibleToIoTDevice.emit(wifiNetworks)
                }
                .collect()
    }

    fun onConnectionStatus(connectionStatus: PhoneToDeviceConnectionStatus) {
        viewModelScope.launch {
            _phoneConnectionStatus.emit(value = connectionStatus)
        }
    }

    fun setConnectionStatus(connectionStatus: PhoneToDeviceConnectionStatus) {
        _phoneConnectionStatus.value = connectionStatus
    }

    fun clearUserSelectedWifiNetwork() {
        _selectedUserWifiNetwork.value = null
    }

    fun onUserWifiNetworkSelected(wifiNetwork: WifiNetwork) {
        logPairingEvent("${HomeViewModel::class.java.simpleName}: User selected Wifi: ${wifiNetwork.ssid}")
        wifiNetworksVisibleToIoTDevice.value.firstOrNull { visibleNetwork ->
            visibleNetwork.ssid == wifiNetwork.ssid
        }?.let(_selectedUserWifiNetwork::postValue)
    }

    fun onUserBTLEWifiNetworkSelected(wifiNetwork: WifiNetwork) {
        logPairingEvent("${HomeViewModel::class.java.simpleName}: User selected Wifi: ${wifiNetwork.ssid}")
        wifiNetwork?.let(_selectedUserWifiNetwork::postValue)
    }

    suspend fun sendWifiNetworkToIoTDevice(wifiNetwork: WifiNetwork? = selectedUserWifiNetwork.value) {
        return withContext(Dispatchers.IO) {
            wifiNetwork?.let { wifi ->
                setSelectedWifiNetwork(wifiNetwork = wifi)
            } ?: run {
                logPairingEvent("${HomeViewModel::class.java.simpleName}: sendWifiNetworkToIoTDevice: Error - Wifi network to be sent is null.")
            }
        }
    }

    private suspend fun setSelectedWifiNetwork(wifiNetwork: WifiNetwork) {
        Log.d(
            HomeViewModel::class.java.simpleName,
            "Setting the wifi network - ssid:${wifiNetwork.ssid}, password: ${wifiNetwork.password}"
        )
        logPairingEvent("${HomeViewModel::class.java.simpleName}: Setting the wifi network on grill to - ssid:${wifiNetwork.ssid}")
        wifiPairingService.setSelectedWifiNetwork(wifiNetwork)
    }

    suspend fun waitForDeviceToComeOnline() {
        viewModelScope.launch(Dispatchers.IO) {
            wifiPairingService.waitForDeviceToComeOnline { connectionResult ->
                connectionResult
                    .onSuccess {
                        logPairingEvent("${HomeViewModel::class.java.simpleName}: Grill is online. DSN: $it")
                        _newGrillDSN.value = it
                    }
                    .onFailure { error ->
                        logPairingEvent("${HomeViewModel::class.java.simpleName}: Grill is offline. Error: ${error.stackTraceToString()}")
                        wifiErrorCount++
                        logPairingEvent("onConnectGrillToWifiFailure: failure count...$wifiErrorCount")

//                        onConnectGrillToWifiFailure()
                    }
            }
        }
    }

    suspend fun renameGrill(dsn: String, name: String = grillName.value): Flow<Unit> {
        Log.d(
            HomeViewModel::class.java.simpleName,
            "Rename grill function called - dsn: $dsn, name: $name"
        )
        return wifiPairingService.renameIoTDevice(dsn = dsn, newName = name)
    }

    fun cancelPairing() {
        viewModelScope.launch {
            logPairingEvent("User Action:  canceled pairing")
            wifiPairingService.cancelPairing()
        }
    }

    fun setPermissionStatus(isPermissionGranted: PermissionStatus) {
        viewModelScope.launch {
            logPairingEvent("setPermissionStatus: ${isPermissionGranted.name}")
            _isPermissionGranted.emit(isPermissionGranted)
        }
    }

    fun setGrillName(newGrillName: String) {
        _grillName.value = newGrillName
    }

    fun onConnectToGrillFailure() {
        hotspotErrorCount++
        logPairingEvent("onConnectToGrillFailure: failure count...$hotspotErrorCount")
    }

    fun onConnectGrillToWifiFailure() {
        wifiErrorCount++
        logPairingEvent("onConnectGrillToWifiFailure: failure count...$wifiErrorCount")
    }

    fun onConnectWifi(password: String) {
        _selectedUserWifiNetwork.value?.let { wifiNetwork ->
            wifiNetwork.password = password

            _selectedUserWifiNetwork.value = wifiNetwork
        } ?: run {
            logPairingEvent("onConnectWifi: Selected wifi network is null. Cannot connect to it.")
        }
    }

    fun logPairingEvent(event: String) {
        viewModelScope.launch {
            wifiPairingService.logPairingEvent(event).collect()
        }
    }

    fun setLocationPermissionsErrorState(
        checkLocationPermissionOnResume: Boolean,
        actionGoToSettings: Boolean
    ) {
        _locationPermissionErrorState.value =
            _locationPermissionErrorState.value.copy(
                isCheckPermissionsOnResume = checkLocationPermissionOnResume,
                isActionGoToSettings = actionGoToSettings
            )
    }

    fun setNearbyPermissionsErrorState(
        checkNearbyPermissionOnResume: Boolean,
        actionGoToSettings: Boolean
    ) {
        _nearbyPermissionErrorState.value =
            _nearbyPermissionErrorState.value.copy(
                isCheckPermissionsOnResume = checkNearbyPermissionOnResume,
                isActionGoToSettings = actionGoToSettings
            )
    }


    //Bluetooth
    fun scanForBleDevices() {
//        bluetoothService.scanForBleDevices()
    }

    fun getBTAdapter(): BluetoothAdapter? {
        return bluetoothService.btAdapter
    }

    fun enableWifiResultNotification() {
        bluetoothService.enableWifiResultNotification()
    }

    fun enableWifiResultDescriptor() {
        bluetoothService.enableWifiResultDescriptor()
    }

    fun sendScanRequestCommand() {
        bluetoothService.sendScanRequestCommand()
    }

    fun sendScanResultCommand() {
        bluetoothService.sendScanResultCommand()
    }

    fun readWifiScanResult() {
        bluetoothService.readWifiScanResult()
    }

    fun sendWifiJoinRequest(wifiNetwork: WifiNetwork) {
        bluetoothService.sendWifiJoinRequest(wifiNetwork)
    }

    fun connectToDevice(address: String): Boolean {
        return bluetoothService.connectToDevice(address)
    }

    fun disconnectDevice() {
        bluetoothService.disconnectDevice()
    }

    fun eventFlow(): SharedFlow<GattFlowTransmission> {
        return bluetoothService.flowEvents
    }

    fun fetchGrillCoreWifiNetworks(): StateFlow<List<WifiNetwork>> {
        return bluetoothService.grillCoreWifiNetworks
    }

    fun fetchFoundDevice(): SharedFlow<List<BTManager.BTJoinableGrill>> {
        return bluetoothService.ninjaDeviceFound
    }

    fun checkForJoinableGrills() {
        bluetoothService.getBTJoinableGrills()
    }

    fun startPassiveScan() {
        bluetoothService.passiveBleScan()
    }

    fun stopPassiveScan() {
        bluetoothService.stopPassiveBleScan()
    }

    fun bluetoothWifiConnectedStatus(): SharedFlow<Int> {
        return bluetoothService.wifiConnectedStatus
    }

    fun storeBluetoothDeviceSerial() {
        viewModelScope.launch {
            bluetoothService.deviceDSN.collect {
                it?.let { _newGrillDSN.value = it }
            }
        }
    }

    fun setBTLEWifiOnlineCallback() {
        viewModelScope.launch {
            println("Networks: CALLING DEVICE ONLINE CALLBACK")
            wifiPairingService.waitForDeviceToComeOnline { result ->
                result
                    .onSuccess {
                        println("Networks: AYLA REGISTRATION SUCCESS")
                        _deviceRegistrationSuccess.tryEmit(true)
                    }
                    .onFailure {
                        println("Ayla registration has failed...${it.message}")
                    }
            }
        }
    }

    suspend fun listenForDeviceToken() {
        bluetoothService.deviceToken.collect {
            it?.let {
                println("Networks: FIRING REGISTER DEVICE!!!")
                registerDevice(it)
            }
        }
    }

    // Bluetooth Command & Encryption Integration

    fun setBtAvailable(isAvailable: Boolean) {
        bluetoothService.setBTAvailable(isAvailable)
    }

    fun checkBluetoothAdapterOn() {
        val isBluetoothOn = getBTAdapter()?.isEnabled == true
        _isBluetoothAdapterOn.value = isBluetoothOn
    }

    fun setBTAvailableOnResume() {
        val isBluetoothOn = getBTAdapter()?.isEnabled == true
        _isBluetoothAdapterOn.value = isBluetoothOn
        setBtAvailable(isBluetoothOn)
    }

    fun updatePermissionsGranted(isGranted: Boolean) {
        _permissionAllGranted.value = isGranted
    }

    private fun observePermissionsEnabled() {
        viewModelScope.launch(Dispatchers.IO) {
            _permissionAllGranted.distinctUntilChangedBy { _permissionAllGranted.value }
                .collect {
                    setBtAvailable(it && getBTAdapter()?.isEnabled == true)
                }
        }
    }

    private fun observeBluetoothEnabled() {
        viewModelScope.launch(Dispatchers.IO) {
            _isBluetoothAdapterOn.distinctUntilChangedBy { _isBluetoothAdapterOn.value }
                .collect {
                    setBtAvailable(it && _permissionAllGranted.value)
                }
        }
    }

    fun listenForDeviceRegistration() {
        viewModelScope.launch(Dispatchers.IO) {
            deviceRegistrationSuccess.collect {
                it?.let {
                    println("Networks: CALLING RENAME GRILL")
                    if (it) renameGrill(newGrillDSN.value).collect {
                        if (it is Unit) {
                            println("Rename device SUCCESSFUL")
                        }
                    }
                }
            }
        }
    }

    suspend fun registerDevice(token: String) {
        wifiPairingService.registerIoTDevice(newGrillDSN.value, token)
    }

    fun subscribeToPushForDevices() {
        viewModelScope.launch(Dispatchers.IO) {
            var numOfFailures = 0
            wifiPairingService.getGrills().forEach { grill ->
                cookService.subscribeToPushForDevice(grill.dsn)
                    .onSuccess {
                        println("${grill.dsn} has been fully subscribed to notifications!")
                    }
                    .onFailure { error ->
                        Log.e(HomeViewModel::class.java.simpleName, error.message, error)
                        numOfFailures += 1
                    }
            }
            _isPushEnabled.emit(numOfFailures == 0)
            setPushEnabledUserCache(numOfFailures == 0)
        }
    }

    private fun setPushEnabledUserCache(isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.setPushEnabledUserCache(isEnabled)
                .onSuccess { Log.d(HomeViewModel::class.java.simpleName, "setPushEnabledUserCache: Success")}
                .onFailure { Log.d(HomeViewModel::class.java.simpleName, "setPushEnabledUserCache: Failed To Save Notification Enabled Status To Cache") }
        }
    }

    fun observeDevicePushEnabled() {
        viewModelScope.launch(Dispatchers.IO) {
            var isFalse = 0
            wifiPairingService.getGrills().forEach { grill ->
                if(cookService.isDevicePushEnabled(grill.dsn).not()) {
                    isFalse +=1
                }
            }
            _isPushEnabled.emit(isFalse == 0)
            getDevicePushEnabledUserCache(isFalse == 0)
        }
    }

    private fun getDevicePushEnabledUserCache(isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
           val isPushEnabled = cookService.isDevicePushEnabledUserCache().getOrNull()
            isPushEnabled?.let {
                if(it && isEnabled.not()) {
                    subscribeToPushForDevices()
                    _isPushEnabled.emit(true)
                }
            }
        }
    }

    suspend fun signOut(): Flow<Unit> {
        return wifiPairingService.signOut()
    }

    private fun incrementTotalCooks() {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.getTotalCooks().catch { error ->
                Log.e(HomeViewModel::class.java.simpleName, error.message, error) }
                .collect {
                    val currentTotal = it + 1
                    Log.d("totalCooks", "incrementTotalCooks: $currentTotal")
                    cookService.setTotalCooks(currentTotal).catch {
                            error -> Log.e(HomeViewModel::class.java.simpleName, error.message, error)
                    }.collect()
                }
        }
    }

    fun getShouldShowAppRatingPrompt() {
        viewModelScope.launch(Dispatchers.IO) {
            val totalCooksFlow = cookService.getTotalCooks()
            val hasAppRatingShown = cookService.getAppRatingPromptHasShown()
            totalCooksFlow.combine(hasAppRatingShown) { numCooks, hasShownPrompt ->
                numCooks >= NUM_COOKS_TO_SHOW_APP_RATING && hasShownPrompt.not()
            }.collect {
                if(it) {
                    _surveyAction.emit(SurveyAction.ShowAppRatingSurvey)
                }
            }
        }
    }

    fun updateSurveyAction(surveyAction: SurveyAction) {
        _surveyAction.value = surveyAction
    }

    fun setAppRatingPromptHasShown() {
        viewModelScope.launch(Dispatchers.IO) {
            cookService.setAppRatingPromptHasShown(true).catch { error ->
                Log.e(HomeViewModel::class.java.simpleName, error.message.toString())
            }.collect()
        }
    }

    fun coldSmokeTemp(): String {
        return if(userTemperatureUnit.value == DegreeType.Fahrenheit) {
            COLD_SMOKE_TEMP_FAREN
        } else {
            COLD_SMOKE_TEMP_CELSIUS
        }
    }
    companion object {
        private const val GRILL_NETWORKS_RETRY_DELAY = 30000L
        private const val GRILL_NETWORKS_RETRY_ATTEMPTS = 3L
        const val PROBE_NOT_SET = "--"
        private const val PROBE_DEFAULT_US = "165"
        private const val PROBE_DEFAULT_EU = "75"
        private const val NUM_COOKS_TO_SHOW_APP_RATING = 5
    }
}