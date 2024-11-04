package com.sharkninja.ninja.connected.kitchen.sections.authentication.services.interfaces

import com.sharkninja.ninja.connected.kitchen.data.enums.RegionCode
import kotlinx.coroutines.flow.Flow

interface RegionCodeSelectionInterface {
    suspend fun getSelectedRegionCode(): Flow<RegionCode>
    suspend fun setSelectedRegionCode(regionCode: RegionCode): Flow<Unit>
    suspend fun isTermsAccepted(): Flow<Boolean>
    suspend fun setTermsAccepted(): Flow<Unit>
}