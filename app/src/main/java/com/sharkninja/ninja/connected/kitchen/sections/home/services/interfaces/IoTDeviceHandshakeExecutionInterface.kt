package com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces

import com.sharkninja.cloudcore.WifiNetwork
import kotlinx.coroutines.flow.Flow

interface IoTDeviceHandshakeExecutionInterface {
    suspend fun startPairingIoTDeviceProcess(ipAddress: String)

    suspend fun observeWifiNetworksSeenByDevice(): Flow<List<WifiNetwork>>

    suspend fun setSelectedWifiNetwork(wifiNetwork: WifiNetwork)

    suspend fun waitForDeviceToComeOnline(completionCallback: (Result<String>) -> Unit)

    suspend fun cancelPairing()
}