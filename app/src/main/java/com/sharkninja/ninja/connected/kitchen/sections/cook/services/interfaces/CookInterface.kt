package com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces

import com.sharkninja.grillcore.DegreeType
import com.sharkninja.ninja.connected.kitchen.data.enums.RegionCode
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.NinjaPushInterface
import kotlinx.coroutines.flow.Flow

interface CookInterface :
    GrillCookSettingsInterface,
    NinjaPushInterface {

    suspend fun getSelectedRegionCode(): Flow<RegionCode>
    suspend fun getUserTemperatureUnit(): Flow<DegreeType>
    suspend fun getSelectedWeightUnit(): Flow<String>
    suspend fun setShowPlaceYourThermometerDialog(shouldShow: Boolean): Flow<Unit>
    suspend fun getShouldShowPlaceYourThermometerDialog(): Flow<Boolean>

    suspend fun getTotalCooks(): Flow<Int>
    suspend fun setTotalCooks(currentTotal: Int): Flow<Unit>

    suspend fun getAppRatingPromptHasShown(): Flow<Boolean>
    suspend fun setAppRatingPromptHasShown(shouldShow: Boolean): Flow<Unit>
}