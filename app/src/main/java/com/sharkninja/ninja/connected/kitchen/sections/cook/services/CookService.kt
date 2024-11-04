package com.sharkninja.ninja.connected.kitchen.sections.cook.services

import android.util.Log
import com.sharkninja.grillcore.*
import com.sharkninja.grillcore.Cook.Companion.getAvailableTemps
import com.sharkninja.grillcore.Cook.Companion.getAvailableTempsUnit
import com.sharkninja.grillcore.Cook.Companion.getAvailableTimes
import com.sharkninja.grillcore.Cook.Companion.getAvailableTimesUnit
import com.sharkninja.grillcore.Cook.Companion.getDefaultTemp
import com.sharkninja.grillcore.Cook.Companion.getDefaultTime
import com.sharkninja.grillcore.Cook.Companion.getFoodPresets
import com.sharkninja.grillcore.GrillManager.Companion.currentGrill
import com.sharkninja.grillcore.GrillManager.Companion.selectedGrillState
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardTabs
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService.CloudCoreConstants.CACHE_KEY_PLACE_YOUR_THERMOMETER_DIALOG
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.CookInterface
import com.sharkninja.grillcore.NotificationManager.Companion.isSubscribedCookingNotifications
import com.sharkninja.grillcore.NotificationManager.Companion.subscribeCookingNotifications
import com.sharkninja.grillcore.NotificationManager.Companion.unsubscribeCookingNotifications
import com.sharkninja.ninja.connected.kitchen.data.enums.RegionCode
import com.sharkninja.ninja.connected.kitchen.data.enums.toRegionCode
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService.CloudCoreConstants.CACHE_KEY_NUM_OF_COOKS
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService.CloudCoreConstants.CACHE_KEY_PUSH_NOTIFICATIONS_ENABLED
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService.CloudCoreConstants.CACHE_KEY_SHOW_APP_RATING_PROMPT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class CookService(
    private val cacheService: CacheService
) : CookInterface {


    override suspend fun getSelectedRegionCode(): Flow<RegionCode> {
        return withContext(Dispatchers.IO) {
            flow {
                cacheService.getUserCacheValue<String>(key = CacheService.CloudCoreConstants.CACHE_KEY_COUNTRY_REGION)
                    .collect { regionCodeString ->
                        emit(regionCodeString.toRegionCode())
                    }
            }
        }
    }
    override suspend fun getSelectedWeightUnit(): Flow<String> {
        TODO("Not yet implemented")
    }

    override suspend fun setShowPlaceYourThermometerDialog(shouldShow: Boolean): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            cacheService.setUserCacheValue(
                key = CACHE_KEY_PLACE_YOUR_THERMOMETER_DIALOG,
                data = shouldShow
            )
        }
    }

    override suspend fun getShouldShowPlaceYourThermometerDialog(): Flow<Boolean> {
        return withContext(Dispatchers.IO) {
            flow {
                cacheService.getUserCacheValue(key = CACHE_KEY_PLACE_YOUR_THERMOMETER_DIALOG, true)
                    .collect { shouldShow ->
                        emit(shouldShow)
                    }
            }
        }
    }


    // GrillCore Integration
//    override suspend fun getGrillCookModesUSA(): Flow<List<CookMode>> {
//        return flow { emit(getCookModes()) }
//    }
//
//    override suspend fun getGrillCookModesINT(): Flow<List<CookMode>> {
//        return flow { emit(getCookModes()) }
//    }

    override suspend fun getGrillTempsForCookMode(cookMode: CookMode): Flow<List<Int>> {
        return flow { emit(getAvailableTemps(cookMode)) }
    }

    override suspend fun getGrillTempUnitsForCookMode(cookMode: CookMode): Flow<String> {
        return flow { emit(getAvailableTempsUnit(cookMode)) }
    }

    override suspend fun getGrillTimesForCookMode(cookMode: CookMode): Flow<List<Int>> {
        return flow { emit(getAvailableTimes(cookMode)) }
    }

    override suspend fun getGrillTimeUnitsForCookMode(cookMode: CookMode): Flow<String> {
        return flow { emit(getAvailableTimesUnit(cookMode)) }
    }

    override suspend fun getGrillDefaultTempForCookMode(cookMode: CookMode): Flow<Int> {
        return flow { emit(getDefaultTemp(cookMode)) }
    }

    override suspend fun getGrillDefaultTimeForCookMode(cookMode: CookMode): Flow<Int> {
        return flow { emit(getDefaultTime(cookMode)) }
    }

    override suspend fun getGrillPresetsForFoodType(foodType: Food): Flow<List<FoodPreset>> {
        return flow { emit(getFoodPresets(foodType)) }
    }

    override suspend fun setGrillCookMode(cookMode: CookMode): Flow<Cook> {
        return flow { emit(Cook.new().setCookMode(cookMode)) }
    }

    override suspend fun getUserTemperatureUnit(): Flow<DegreeType> {
        return flow { emit( User.userDegreeType())}
    }

    override suspend fun setGrillTemperature(temperature: Int): Flow<Cook> {
        TODO("Not yet implemented")
    }

    override suspend fun setGrillTime(duration: Int): Flow<Cook> {
        TODO("Not yet implemented")
    }

    override suspend fun setGrillWoodSmokeFeature(toggle: Boolean): Flow<Cook> {
        TODO("Not yet implemented")
    }

    override suspend fun setFirstProbeManual(temperature: Int): Flow<Cook> {
        TODO("Not yet implemented")
    }

    override suspend fun setFirstProbePreset(foodType: Food, presetIndex: Int): Flow<Cook> {
        TODO("Not yet implemented")
    }

    override suspend fun setSecondProbeManual(temperature: Int): Flow<Cook> {
        TODO("Not yet implemented")
    }

    override suspend fun setSecondProbePreset(foodType: Food, presetIndex: Int): Flow<Cook> {
        TODO("Not yet implemented")
    }

    override suspend fun setSelectedGrill(grill: Grill): Flow<Grill?> {
        return flow {
            currentGrill = grill
            emit(currentGrill)
        }
    }

    override suspend fun sendGrillCookCommand(
        cookMode: CookMode,
        cookType: CookDashboardTabs,
        temperatureThermCook: Int,
        temperatureTimeCook: Int,
        duration: Int,
        smokeFeature: Boolean,
        isProbe0Active: Boolean,
        isProbe1Active: Boolean,
        firstProbeTemperature: Int,
        firstProbePresetFood: Food,
        firstProbePresetIndex: Int,
        secondProbeTemperature: Int,
        secondProbePresetFood: Food,
        secondProbePresetIndex: Int
    ): Flow<Unit> {
        return callbackFlow {

            val cook = Cook.new()

            if (cookType == CookDashboardTabs.TimeCook) {
                cook
                    .setCookMode(cookMode)
                    .setTemp(temperatureTimeCook)
                    .setDuration(duration)
                    .setSmoke(smokeFeature)
                    .sync()
                    .onSuccess { trySend(it) }
                    .onFailure { println("!!!FAILED TO SEND COOK COMMAND!!!") }
            } else {
                val probe1CookObject = if (isProbe0Active.not()) {
                    cook
                } else {
                    if (firstProbePresetFood == Food.Manual || firstProbePresetFood == Food.NotSet) {
                        cook.setProbe0Manual(firstProbeTemperature)
                    } else {
                        if (firstProbePresetIndex != -1) {
                            cook.setProbe0Preset(firstProbePresetFood, firstProbePresetIndex)
                        } else {
                            cook
                        }
                    }
                }
                val probe2CookObject = if (isProbe1Active.not()) {
                    probe1CookObject
                } else {
                    if (secondProbePresetFood == Food.Manual || secondProbePresetFood == Food.NotSet) {
                        probe1CookObject.setProbe1Manual(secondProbeTemperature)
                    } else {
                        if (secondProbePresetIndex != -1) {
                            probe1CookObject.setProbe1Preset(
                                secondProbePresetFood,
                                secondProbePresetIndex
                            )
                        } else {
                            probe1CookObject
                        }
                    }
                }

                probe2CookObject
                    .setCookMode(cookMode)
                    .setTemp(temperatureThermCook)
                    .setSmoke(smokeFeature)
                    .sync()
                    .onSuccess { trySend(it) }
                    .onFailure { println("!!!FAILED TO SEND COOK COMMAND!!!") }
            }


            awaitClose()
        }
    }

    override suspend fun editTimedCook(
        cookMode: CookMode,
        temperature: Int,
        duration: Int
    ): Flow<Unit> {
        return callbackFlow {
            val cook = Grill.getLastCook()
            val addCookMode = if (cook?.mode == cookMode) cook else cook?.setCookMode(cookMode)
            val addTemp = if(cook?.temp == temperature) addCookMode else addCookMode?.setTemp(temperature)
            val addDurationFinalObject = if(cook?.duration == duration) addTemp else addTemp?.setDuration(duration)

            addDurationFinalObject
                ?.sync()
                ?.onSuccess { trySend(it) }
                ?.onFailure {
                    Log.e("EditTimedCookCookService", "editTimedCook: ${it.message} ")
                    println("FAILED TO UPDATE TIME COOK SETTINGS")
                }

            awaitClose()
        }
    }

    override suspend fun editFirstThermometerSettings(
        internalTemperature: Int,
        presetFood: Food,
        presetIndex: Int
    ): Flow<Unit> {
        return callbackFlow {
            val cook = Grill.getLastCook()
            val thermCook = if (presetFood == Food.Manual || presetFood == Food.NotSet) {
                cook?.setProbe0Manual(internalTemperature)
            } else {
                cook?.setProbe0Preset(presetFood, presetIndex)
            }

            if (thermCook != null) {
                thermCook.sync()
                    .onSuccess { trySend(it) }
                    .onFailure { println("FAILED TO UPDATE TIME COOK SETTINGS") }
            }

            awaitClose()
        }
    }

    override suspend fun editSecondThermometerSettings(
        internalTemperature: Int,
        presetFood: Food,
        presetIndex: Int
    ): Flow<Unit> {
        return callbackFlow {
            val cook = Grill.getLastCook()
            val thermCook = if (presetFood == Food.Manual || presetFood == Food.NotSet) {
                cook?.setProbe1Manual(internalTemperature)
            } else {
                cook?.setProbe1Preset(presetFood, presetIndex)
            }

            if (thermCook != null) {
                thermCook.sync()
                    .onSuccess { trySend(it) }
                    .onFailure { println("FAILED TO UPDATE TIME COOK SETTINGS") }
            }

            awaitClose()
        }
    }

    override suspend fun editThermometerCookSettings(
        cookMode: CookMode,
        temperature: Int
    ): Flow<Unit> {
        return callbackFlow {
            val cook = Grill.getLastCook()
            val addCookMode = if (cook?.mode == cookMode) cook else cook?.setCookMode(cookMode)
            val addTempFinalObject = if (cook?.temp == temperature) addCookMode else addCookMode?.setTemp(temperature)

            addTempFinalObject
                ?.sync()
                ?.onSuccess { trySend(it) }
                ?.onFailure { println("FAILED TO UPDATE TIME COOK SETTINGS") }

            awaitClose()
        }
    }

    override suspend fun editWoodfireSetting(smokeFeature: Boolean): Flow<Unit> {
        return flow {
            Grill
                .getLastCook()
                ?.setSmoke(smokeFeature)
                ?.sync()?.getOrThrow()?.let {
                    emit(
                        it
                    )
                }
        }
    }

    override suspend fun skipPreheat(): Flow<Unit> {
        return flow {
            emit(Grill.skipPreheat().getOrThrow())
        }
    }

    override suspend fun stopCook(): Flow<Unit> {
        return flow {
            emit(Grill.stopCook().getOrThrow())
        }
    }

    override suspend fun getCurrentGrillStatus(): Flow<GrillState> {
        return selectedGrillState
    }

    override suspend fun isDevicePushEnabled(dsn: String): Boolean {
        return isSubscribedCookingNotifications(dsn)
    }

    override suspend fun isDevicePushEnabledUserCache(): Result<Boolean> {
       return cacheService.getUserCacheValueResult(CACHE_KEY_PUSH_NOTIFICATIONS_ENABLED, false)
    }

    override suspend fun subscribeToPushForDevice(dsn: String): Result<Unit> {
        return subscribeCookingNotifications(dsn)
    }

    override suspend fun setPushEnabledUserCache(isEnabled: Boolean): Result<Unit> {
          return cacheService.setUserCacheValueResult(CACHE_KEY_PUSH_NOTIFICATIONS_ENABLED, isEnabled)
    }

    override suspend fun unsubscribeToPushForDevice(dsn: String): Result<Unit> {
        return unsubscribeCookingNotifications(dsn)
    }

    override suspend fun getTotalCooks(): Flow<Int> {
        return withContext(Dispatchers.IO) {
            flow {
                cacheService.getUserCacheValue(
                    key = CACHE_KEY_NUM_OF_COOKS,
                    defaultValue = 0
                )
                    .collect { numOfCooks ->
                        emit(numOfCooks)
                    }
            }
        }
    }

    override suspend fun setTotalCooks(currentTotal: Int): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            cacheService.setUserCacheValue(
                key = CACHE_KEY_NUM_OF_COOKS,
                data = currentTotal
            )
        }
    }

    override suspend fun getAppRatingPromptHasShown(): Flow<Boolean> {
        return withContext(Dispatchers.IO) {
            flow {
                cacheService.getUserCacheValue(
                    key = CACHE_KEY_SHOW_APP_RATING_PROMPT,
                    defaultValue = false
                )
                    .collect { shouldShowAppRatingPrompt ->
                        emit(shouldShowAppRatingPrompt)
                    }
            }
        }
    }

    override suspend fun setAppRatingPromptHasShown(shouldShow: Boolean): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            cacheService.setUserCacheValue(
                key = CACHE_KEY_SHOW_APP_RATING_PROMPT,
                data = shouldShow
            )
        }
    }
}