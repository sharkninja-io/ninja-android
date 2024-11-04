package com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sharkninja.grillcore.Grill
import com.sharkninja.grillcore.GrillDetails
import com.sharkninja.grillcore.User
import com.sharkninja.ninja.connected.kitchen.data.enums.RegionCode
import com.sharkninja.ninja.connected.kitchen.data.enums.SettingsEvent
import com.sharkninja.ninja.connected.kitchen.data.enums.toRegionServer
import com.sharkninja.ninja.connected.kitchen.data.models.*
import com.sharkninja.ninja.connected.kitchen.ext.getEmailForIntershop
import com.sharkninja.ninja.connected.kitchen.ext.getOrEmpty
import com.sharkninja.ninja.connected.kitchen.ext.isDevEnv
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService.CloudCoreConstants.CACHE_KEY_PUSH_NOTIFICATIONS_ENABLED
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.interfaces.CacheInterface
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceAddress
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProfile
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SettingsInterface
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.EmailAddressRepresentable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsService: SettingsInterface,
    private val cacheService: CacheInterface
) : ViewModel() {

    /* user/account */
    private val _currentUser = MutableStateFlow(UserProfile())
    val currentUser = _currentUser.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail = _userEmail.asStateFlow()

    private val _selectedRegion = MutableStateFlow(RegionCode.US.item.code)
    val selectedRegion = _selectedRegion.asStateFlow()

    private val _countrySelection = MutableStateFlow("")
    val countrySelection = _countrySelection.asStateFlow()

    private val _stateSelection = MutableStateFlow("")
    val stateSelection = _stateSelection.asStateFlow()

    private val _profileState = MutableStateFlow<ProfileAccountChangeEvents>(ProfileAccountChangeEvents.ActionProcessed)
    val profileState = _profileState.asStateFlow()

    private val _requiredFieldsValid = MutableStateFlow(false)
    val requiredFieldsValid = _requiredFieldsValid.asStateFlow()

    private val _emailChangeSuccessful = MutableStateFlow(false)
    val emailChangeSuccessful = _emailChangeSuccessful.asStateFlow()

    private val _passwordChangeSuccessful = MutableStateFlow(false)
    val passwordChangeSuccessful = _passwordChangeSuccessful.asStateFlow()

    private val _newEmailValid = MutableStateFlow(false)
    val newEmailValid = _newEmailValid.asStateFlow()

    /* appliance */
    private val _applianceList = MutableStateFlow<List<Appliance>>(listOf())
    val applianceList = _applianceList.asStateFlow()

    private val _currentAppliance = MutableStateFlow(Appliance())
    val currentAppliance = _currentAppliance.asStateFlow()

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog = _showErrorDialog.asStateFlow()

    private val _restoreFactorySettingsSuccessful = MutableStateFlow<Boolean?>(null)
    val restoreFactorySettingsSuccessful = _restoreFactorySettingsSuccessful.asStateFlow()


    /* preferences */
//    private val _temperatureSelection = MutableStateFlow<String?>(null)
//    val temperatureSelection = _temperatureSelection.asStateFlow()

    private val _weightSelection = MutableStateFlow<String?>(null)
    val weightSelection = _weightSelection.asStateFlow()

    private val _isCookingPushNotificationsEnabled = MutableStateFlow<Boolean>(false)
    val isCookingPushNotificationsEnabled = _isCookingPushNotificationsEnabled.asStateFlow()

    private val _deleteApplianceSuccess = MutableStateFlow<Boolean?>(null)
    val deleteApplianceSuccess = _deleteApplianceSuccess.asStateFlow()

    private val _settingsEvent = MutableStateFlow<SettingsEvent>(SettingsEvent.ActionProcessed)
    val settingsEvent = _settingsEvent.asStateFlow()

    fun getCurrentEmail() {
        _userEmail.value = settingsService.getCurrentEmail()
    }

    fun resetEmailChange() {
        _emailChangeSuccessful.value = false
    }

    fun resetPasswordChange() {
        _passwordChangeSuccessful.value = false
    }

    fun resetErrorDialog() {
        _showErrorDialog.value = false
    }

    fun checkValidEmail(email: String) {
        val representable = EmailAddressRepresentable(email)
        _newEmailValid.value = representable.isEmailValid()
    }

    fun updateRequiredFieldState(isValid: Boolean) {
        _requiredFieldsValid.value = isValid
    }

    fun saveStateSelection(state: String) {
        _stateSelection.value = state
    }

    /* user/account */
    fun fetchProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsService.getEcommerceProfile()
                .catch {
                    Log.e(SettingsViewModel::class.java.simpleName, it.message, it)
                }
                .collect {
                emitUserProfile(it)
            }
        }
    }

    private fun emitUserProfile(profile: EcommerceProfile?) {
        profile?.let {
            val isDev = User.getUsername().getOrNull().isDevEnv()
            val firstName = if(isDev) it.address?.firstName ?: "" else it.firstName
            val lastName = if(isDev) it.address?.lastName ?: "" else it.lastName
            _currentUser.update { user ->
                user.copy(
                    firstName = firstName,
                    lastName = lastName,
                    customerNo = it.customerNo,
                    address = it.address?.addressLine1 ?: "",
                    addressLine2 = it.address?.addressLine2 ?: "",
                    city = it.address?.city ?: "",
                    state = it.address?.state ?: "",
                    zipCode = it.address?.postalCode ?: "",
                    country = it.address?.countryCode ?: "",
                    phoneNumber = it.phoneNumber
                )
            }
            _stateSelection.value = it.address?.state.getOrEmpty()
        }
    }

    fun updateUserProfile(
        firstName: String = "",
        lastName: String = "",
        address: String = "",
        addressLine2: String = "",
        city: String = "",
        zipCode: String = "",
        phoneNumber: String = ""
    ) {
            _profileState.value = ProfileAccountChangeEvents.UpdateAccountLoading
            _currentUser.update {
                it.copy(
                    firstName = firstName,
                    lastName = lastName,
                    address = address,
                    addressLine2 = addressLine2,
                    city = city,
                    state = _stateSelection.value,
                    zipCode = zipCode,
                    country = _countrySelection.value,
                    phoneNumber = phoneNumber
                )
            }

            viewModelScope.launch(context = Dispatchers.IO) {
                val updatedAddress = EcommerceAddress(
                    id = null,
                    firstName = firstName,
                    lastName = lastName,
                    addressLine1 = address,
                    addressLine2 = addressLine2,
                    city = city,
                    state = _stateSelection.value,
                    postalCode = zipCode,
                    countryCode = _countrySelection.value
                )
                val updatedEcommerceProfile = EcommerceProfile(
                    email = _userEmail.value,
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber,
                    customerNo = _currentUser.value.customerNo,
                    address = updatedAddress,
                    receiveSharkEmails = false
                )
                settingsService.updateEcommerceProfile(updatedEcommerceProfile, selectedRegion.value)
                    .catch {
                        Log.e(SettingsViewModel::class.java.simpleName, it.message, it)
                        _profileState.emit(ProfileAccountChangeEvents.UpdateAccountError)
                    }.collect {
                        _profileState.emit(ProfileAccountChangeEvents.UpdateAccountSuccess)
                    }
            }
    }

    fun updateProfileState(action: ProfileAccountChangeEvents) {
        _profileState.value = action
    }

    fun changeEmail(newEmail: String) {
        viewModelScope.launch(context = Dispatchers.IO) {
            val isIntershopNorthAmerica = selectedRegion.value.toRegionServer() == CountryRegionSupport.CountryRegionServer.NorthAmerica
            if (isIntershopNorthAmerica) {
                changeEcommerceProfileEmailAddress(newEmail)
            } else {
                //Intershop EU path
                settingsService.changeEmail(newEmail).collect {
                    if (it.emailChangeSuccessful) {
                        changeEcommerceProfileEmailAddress(newEmail)
                    } else {
                        _showErrorDialog.value = true
                    }
                }
            }
        }
    }

    private suspend fun changeEcommerceProfileEmailAddress(newEmail: String) {
        val oldEmail = userEmail.value
        settingsService.changeEcommerceProfileEmailAddress(
            currentEmail = oldEmail.getEmailForIntershop(),
            newEmail = newEmail.getEmailForIntershop(),
            customerNo = _currentUser.value.customerNo,
            countryCode = selectedRegion.value,
            firstName = _currentUser.value.firstName,
            lastName = _currentUser.value.lastName,
            phoneNumber = _currentUser.value.phoneNumber
        ).catch {
            Log.e(SettingsViewModel::class.java.simpleName, it.message, it)
            _showErrorDialog.value = true
        }.collect {
            _emailChangeSuccessful.value = true
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch(context = Dispatchers.IO) {
            settingsService.changePassword(oldPassword, newPassword).collect {
                if (it) {
                    _passwordChangeSuccessful.value = true
                } else {
                    _showErrorDialog.value = true
                }
            }
        }
    }

    fun deleteAccount() {
        _profileState.value = ProfileAccountChangeEvents.DeleteAccountLoading
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            settingsService.deleteAccount()
                .catch {
                    Log.e(SettingsViewModel::class.java.simpleName, it.message, it)
                    _profileState.emit(ProfileAccountChangeEvents.DeleteAccountError)
                }
                .collect {
                _profileState.emit(ProfileAccountChangeEvents.DeleteAccountSuccess)
            }
        }
    }

    fun updateSettingsEvent(event: SettingsEvent) {
        _settingsEvent.value = event
    }

    fun signOut() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsService.signOut()
                .catch {
                    Log.e(SettingsViewModel::class.java.simpleName, it.message, it)
                    _settingsEvent.emit(SettingsEvent.UserSignOutError)
                }
                .collect {
                    _settingsEvent.emit(SettingsEvent.UserSignedOut)
                }
        }
    }

    fun fetchRegion() {
        viewModelScope.launch(Dispatchers.IO) {
            val region =
                cacheService.getUserCacheValueResult<String>(CacheService.CloudCoreConstants.CACHE_KEY_COUNTRY_REGION)
                    .getOrNull()
            if (region != null) {
                _selectedRegion.value = region
                _countrySelection.value = region
            }
        }
    }

    fun setPushEnabledUserCache(isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            cacheService.setUserCacheValueResult(CACHE_KEY_PUSH_NOTIFICATIONS_ENABLED, isEnabled)
                .onSuccess { Log.d(SettingsViewModel::class.java.simpleName, "setPushEnabledUserCache: Success")}
                .onFailure { Log.d(SettingsViewModel::class.java.simpleName, "setPushEnabledUserCache: Failed To Save Notification Enabled Status To Cache") }
        }
    }

    /* preferences */
    fun fetchSelectedWeightUnit() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsService.getSelectedWeightUnit().collectLatest {
                _weightSelection.value = it
            }
        }
    }

//    fun fetchSelectedTemperatureUnit() {
//        viewModelScope.launch(Dispatchers.IO) {
//            settingsService.getSelectedTemperatureUnit().collectLatest {
//                _temperatureSelection.value = it
//            }
//        }
//    }

//    fun saveTemperatureSelection(temperatureUnit: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            _temperatureSelection.emit(temperatureUnit)
//
//            settingsService.setSelectedTemperatureUnit(temperatureUnit)
//                .catch { error -> Log.e("SettingsViewModel", error.message, error) }
//                .collect()
//        }
//    }

    fun saveWeightSelection(weightUnit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weightSelection.emit(weightUnit)
            settingsService.setSelectedWeightUnit(weightUnit)
                .catch { error -> Log.e("SettingsViewModel", error.message, error) }
                .collect()
        }
    }

    fun fetchGrills() {
        viewModelScope.launch(Dispatchers.IO) {
            val grills = settingsService.getGrills().map { grill: Grill ->
                var details = GrillDetails("", "", "", "", "", "")


                grill.getDetails().onSuccess { value: GrillDetails -> details = value }
                    .onFailure { error -> Log.e("SettingsViewModel", error.message.toString()) }

                Appliance(
                    name = grill.getName(),
                    details = details,
                    grillObject = grill
                )

            }
            Log.d("deleteApplianceTest", "fetchGrills: $grills ")
            _applianceList.emit(grills)
        }
    }

    /* appliance */

    fun setCurrentAppliance(appliance: Appliance) {
        _currentAppliance.update { currentAppliance ->
            currentAppliance.copy(
                name = appliance.name,
                details = appliance.details,
                grillObject = appliance.grillObject
            )
        }
    }

    fun deleteAppliance() {
        Log.d("deleteApplianceTest", "currentAppliance: ${currentAppliance.value} ")
        viewModelScope.launch(Dispatchers.IO) {
            settingsService.unregisterDevice(currentAppliance.value).collect {
                if (it) {
                    _deleteApplianceSuccess.emit(true)
                } else {
                    _deleteApplianceSuccess.emit(false)
                    Log.e("deleteApplianceTest", "deleteAppliance: FAILURE", )
                }
            }
        }
    }

    fun resetDeleteApplianceSuccess() {
        _deleteApplianceSuccess.value = null
    }

    fun restoreFactorySettings() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsService.restoreDefaults(_currentAppliance.value)
                .catch {
                }
                .collect {
                    _restoreFactorySettingsSuccessful.emit(it)
                }
        }
    }

    fun resetRestoreFactorySettingsSuccessful() {
        _restoreFactorySettingsSuccessful.value = null
    }

    // Firebase Push
    fun observeDevicePushEnabled() {
        viewModelScope.launch(Dispatchers.IO) {
            var isFalse = 0
            settingsService.getGrills().forEach { grill ->
              if(settingsService.isDevicePushEnabled(grill.dsn).not()) {
                  isFalse +=1
              }
            }
            _isCookingPushNotificationsEnabled.emit(isFalse == 0)
        }
    }

    fun subscribeToPushForDevices() {
        viewModelScope.launch(Dispatchers.IO) {
            var numOfFailures = 0
            settingsService.getGrills().forEach { grill ->
                    settingsService.subscribeToPushForDevice(grill.dsn)
                        .onSuccess {
                            println("${grill.dsn} has been fully subscribed to notifications!")
                        }
                        .onFailure { error ->
                            Log.e(HomeViewModel::class.java.simpleName, error.message, error)
                            numOfFailures += 1
                        }
                }

            _isCookingPushNotificationsEnabled.emit(numOfFailures == 0)
            setPushEnabledUserCache(true)
        }
    }

    fun unSubscribeToPushForDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            var numOfFailures = 0
            settingsService.getGrills().forEach { grill ->
                    settingsService.unsubscribeToPushForDevice(grill.dsn)
                        .onSuccess {
                            println("${grill.dsn} has been fully subscribed to notifications!")
                        }
                        .onFailure { error ->
                            Log.e(HomeViewModel::class.java.simpleName, error.message, error)
                            numOfFailures += 1
                        }
            }
            _isCookingPushNotificationsEnabled.emit(numOfFailures != 0)
        }
    }
}