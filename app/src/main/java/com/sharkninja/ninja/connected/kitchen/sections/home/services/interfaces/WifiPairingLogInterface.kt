package com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces

import kotlinx.coroutines.flow.Flow

interface WifiPairingLogInterface {
    suspend fun getPairingLog(): Flow<String>
    suspend fun logPairingEvent(event: String): Flow<Unit>
    fun logPairingEventNoCallback(event: String)
}