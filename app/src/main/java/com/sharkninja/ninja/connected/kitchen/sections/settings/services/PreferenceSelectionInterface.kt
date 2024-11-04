package com.sharkninja.ninja.connected.kitchen.sections.settings.services

//import com.sharkninja.ninja.connected.kitchen.data.enums.TemperatureUnit
//import com.sharkninja.ninja.connected.kitchen.data.enums.WeightUnit
import kotlinx.coroutines.flow.Flow

interface PreferenceSelectionInterface {
//    suspend fun getSelectedTemperatureUnit(): Flow<String>
    suspend fun setSelectedTemperatureUnit(temperatureUnit: String): Flow<Unit>
    suspend fun getSelectedWeightUnit(): Flow<String>
    suspend fun setSelectedWeightUnit(weightUnit: String): Flow<Unit>
}