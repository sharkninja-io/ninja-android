package com.sharkninja.ninja.connected.kitchen.sections.settings.services

import android.content.Context
import android.util.Log
import com.sharkninja.grillcore.Grill
import com.sharkninja.grillcore.GrillManager
import com.sharkninja.grillcore.GrillManager.Companion.grills
import com.sharkninja.grillcore.NotificationManager.Companion.isSubscribedCookingNotifications
import com.sharkninja.grillcore.NotificationManager.Companion.subscribeCookingNotifications
import com.sharkninja.grillcore.NotificationManager.Companion.unsubscribeCookingNotifications
import com.sharkninja.grillcore.User
import com.sharkninja.ninja.connected.kitchen.data.enums.*
import com.sharkninja.ninja.connected.kitchen.data.models.Appliance
import com.sharkninja.ninja.connected.kitchen.data.models.ChangeEmailResult
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService.CloudCoreConstants.CACHE_KEY_COUNTRY_REGION
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.EcommerceRepositoryInterface
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceChangeEmail
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

class SettingsService(
    private val context: Context,
    private val cacheService: CacheService,
    private val signOutService: SignOutInterface,
    private val eCommerceService: EcommerceRepositoryInterface
) : SettingsInterface {

    override fun getShopUriPath(region: String): String {
        TODO("Not yet implemented")
    }

    override fun getCurrentEmail(): String {
        return User.getUsername().getOrDefault("")
    }

    override fun changeEmail(newEmail: String) = callbackFlow {
        val authToken = User.getAccessToken().getOrNull()
        User.updateUserEmail(newEmail)
            .onFailure { error ->
                Log.e(SettingsService::class.java.simpleName, error.message, error)
                trySend(ChangeEmailResult(false, null))

            }
            .onSuccess {
                trySend(ChangeEmailResult(true, authToken))
            }
        awaitClose()
    }

    override fun changePassword(currentPassword: String, newPassword: String) =
        callbackFlow {
            User.resetUserPassword(currentPassword, newPassword)
                .onFailure { error ->
                    Log.e(SettingsService::class.java.simpleName, error.message, error)
                    trySend(false)
                }
                .onSuccess { trySend(true) }
            awaitClose()
        }

    override fun deleteAccount() = callbackFlow {
        User.delete()
            .onFailure { trySend(false) }
            .onSuccess { trySend(true) }
        awaitClose()
    }

    @OptIn(kotlinx.coroutines.FlowPreview::class)
    override suspend fun  getEcommerceProfile(): Flow<EcommerceProfile?> {
        return cacheService.getUserCacheValue<String>(key = CACHE_KEY_COUNTRY_REGION)
            .map { countryCodeString ->
                countryCodeString.toRegionCode()
            }.flatMapConcat { regionCode ->
                eCommerceService.getProfileFromServer(regionCode.item.code)
            }
    }

    override suspend fun updateEcommerceProfile(profile: EcommerceProfile, countryCode: String): Flow<Unit> {
        return eCommerceService.updateProfile(profile, countryCode)
            .onEach {
                eCommerceService.getProfileFromServer(countryCode)
            }
    }

    override suspend fun changeEcommerceProfileEmailAddress(
        currentEmail: String?,
        newEmail: String,
        customerNo: String?,
        countryCode: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Flow<Unit> {
        return eCommerceService.changeProfileEmailAddress(
            countryCode = countryCode,
            changeEmail = EcommerceChangeEmail(
                currentEmail = currentEmail,
                newEmail = newEmail,
                customerNo = customerNo,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber
            )
        )
    }

    override suspend fun getEcommerceProductRegistration(
        email: String,
        customerNo: String?,
        countryCode: String,
        serialNumber: String?,
        registrationId: String?
    ): Flow<EcommerceProductRegistration?> {
        return eCommerceService.getProductRegistration(
            countryCode = countryCode,
            email = email,
            serialNumber = serialNumber,
            customerNo = customerNo,
            registrationId = registrationId
        )
    }

    override suspend fun registerProductWithEcommerce(
        countryCode: String,
        eCommRegistration: EcommerceProductRegistration
    ): Flow<Unit> {
        return eCommerceService.registerProduct(eCommRegistration, countryCode)
    }

    override suspend fun signOut(): Flow<Unit> {
        return signOutService.signOut()
    }

    override suspend fun setSelectedTemperatureUnit(temperatureUnit: String): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            cacheService.setUserCacheValue(
                key = CacheService.CloudCoreConstants.CACHE_KEY_TEMPERATURE_UNIT,
                data = temperatureUnit
            )
        }
    }

    override suspend fun getSelectedWeightUnit(): Flow<String> {
        return withContext(Dispatchers.IO) {
            flow {
                cacheService.getUserCacheValue(
                    key = CacheService.CloudCoreConstants.CACHE_KEY_WEIGHT_UNIT,
                    defaultValue = WeightUnit.LB.selection
                )
                    .collect { weightUnitString ->
                        emit(weightUnitString)
                    }
            }
        }
    }

    override suspend fun setSelectedWeightUnit(weightUnit: String): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            cacheService.setUserCacheValue(
                key = CacheService.CloudCoreConstants.CACHE_KEY_WEIGHT_UNIT,
                data = weightUnit
            )
        }

    }

    override fun restoreDefaults(appliance: Appliance): Flow<Boolean> {
        return callbackFlow {
            appliance.grillObject?.let {
                it.factoryReset()
                    .onFailure { error ->
                        Log.e(
                            SettingsService::class.java.simpleName,
                            "Error restoring defaults: + ${error.message}",
                            error
                        )
                        trySend(false)
                    }
                    .onSuccess { trySend(true) }
            }
            awaitClose()
        }
    }

    override fun unregisterDevice(appliance: Appliance) = callbackFlow {
        appliance.grillObject?.let {
            it.delete()
                .onFailure { trySend(false) }
                .onSuccess { trySend(true) }
            awaitClose()
        }
    }

    override suspend fun fetchGrills(): Flow<List<Grill>> {
        return callbackFlow {
            GrillManager.fetchGrills()
                ?.onSuccess { trySend(it) }
                ?.onFailure { trySend(listOf()) }

            awaitClose()
        }
    }

    override fun getGrills(): List<Grill> {
        return grills
    }

    override suspend fun getCurrentGrill(): Flow<Grill?> {
        TODO("Not yet implemented")
    }

    override suspend fun setSelectedGrill(grill: Grill): Flow<Grill?> {
        TODO("Not yet implemented")
    }

    override suspend fun isDevicePushEnabled(dsn: String): Boolean {
        return isSubscribedCookingNotifications(dsn)
    }

    override suspend fun isDevicePushEnabledUserCache(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun subscribeToPushForDevice(dsn: String): Result<Unit> {
        return subscribeCookingNotifications(dsn)
    }

    override suspend fun unsubscribeToPushForDevice(dsn: String): Result<Unit> {
        return unsubscribeCookingNotifications(dsn)
    }

    override suspend fun setPushEnabledUserCache(isEnabled: Boolean): Result<Unit> {
        TODO("Not yet implemented")
    }
}