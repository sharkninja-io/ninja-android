package com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces

import kotlinx.coroutines.flow.Flow

interface IoTDeviceWifiScanningInterface {
    suspend fun getIoTDeviceWifiNetworks(ssidPattern: String = "Ninja_CG"): Flow<List<String>>
}