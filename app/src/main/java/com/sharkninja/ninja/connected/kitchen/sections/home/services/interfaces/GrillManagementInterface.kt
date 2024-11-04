package com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces

import com.sharkninja.grillcore.Grill
import kotlinx.coroutines.flow.Flow

interface GrillManagementInterface {
    suspend fun fetchGrills(): Flow<List<Grill>>
    fun getGrills(): List<Grill>
    suspend fun getCurrentGrill(): Flow<Grill?>
    suspend fun setSelectedGrill(grill: Grill): Flow<Grill?>
}