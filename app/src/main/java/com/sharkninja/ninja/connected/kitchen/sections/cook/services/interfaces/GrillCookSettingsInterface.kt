package com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces

import com.sharkninja.grillcore.*
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardTabs
import kotlinx.coroutines.flow.Flow

interface GrillCookSettingsInterface {

//    suspend fun getGrillCookModesUSA(): Flow<List<CookMode>>
//
//    suspend fun getGrillCookModesINT(): Flow<List<CookMode>>

    suspend fun getGrillTempsForCookMode(cookMode: CookMode): Flow<List<Int>>

    suspend fun getGrillTempUnitsForCookMode(cookMode: CookMode): Flow<String>

    suspend fun getGrillTimesForCookMode(cookMode: CookMode): Flow<List<Int>>

    suspend fun getGrillTimeUnitsForCookMode(cookMode: CookMode): Flow<String>

    suspend fun getGrillDefaultTempForCookMode(cookMode: CookMode): Flow<Int>

    suspend fun getGrillDefaultTimeForCookMode(cookMode: CookMode): Flow<Int>

    suspend fun getGrillPresetsForFoodType(foodType: Food): Flow<List<FoodPreset>>

    suspend fun setGrillCookMode(cookMode: CookMode): Flow<Cook>

    suspend fun setGrillTemperature(temperature: Int): Flow<Cook>

    suspend fun setGrillTime(duration: Int): Flow<Cook>

    suspend fun setGrillWoodSmokeFeature(toggle: Boolean): Flow<Cook>

    suspend fun setFirstProbeManual(temperature: Int): Flow<Cook>

    suspend fun setFirstProbePreset(foodType: Food, presetIndex: Int): Flow<Cook>

    suspend fun setSecondProbeManual(temperature: Int): Flow<Cook>

    suspend fun setSecondProbePreset(foodType: Food, presetIndex: Int): Flow<Cook>

    suspend fun setSelectedGrill(grill: Grill): Flow<Grill?>

    suspend fun sendGrillCookCommand(cookMode: CookMode,
                                     cookType: CookDashboardTabs,
                                     temperatureThermCook: Int,
                                     temperatureTimeCook: Int,
                                     duration: Int,
                                     smokeFeature: Boolean = false,
                                     isProbe0Active: Boolean = false,
                                     isProbe1Active: Boolean = false,
                                     firstProbeTemperature: Int = 0,
                                     firstProbePresetFood: Food = Food.NotSet,
                                     firstProbePresetIndex: Int = 0,
                                     secondProbeTemperature: Int = 0,
                                     secondProbePresetFood: Food = Food.NotSet,
                                     secondProbePresetIndex: Int = 0): Flow<Unit>

    suspend fun getCurrentGrillStatus(): Flow<GrillState>

    suspend fun editTimedCook(cookMode: CookMode,
                              temperature: Int,
                              duration: Int
    ) : Flow<Unit>
    suspend fun editFirstThermometerSettings(
        internalTemperature: Int,
        presetFood: Food,
        presetIndex: Int
    ): Flow<Unit>

    suspend fun editSecondThermometerSettings(
        internalTemperature: Int,
        presetFood: Food,
        presetIndex: Int
    ): Flow<Unit>

    suspend fun editThermometerCookSettings(
        cookMode: CookMode,
        temperature: Int
    ): Flow<Unit>

    suspend fun editWoodfireSetting(
        smokeFeature: Boolean
    ): Flow<Unit>

    suspend fun skipPreheat(): Flow<Unit>

    suspend fun stopCook(): Flow<Unit>
}