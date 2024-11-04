package com.sharkninja.ninja.connected.kitchen.sections.home.services

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.util.Log
import com.sharkninja.ninja.connected.kitchen.data.models.pairing.PhoneToDeviceConnectionStatus
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.IoTDeviceConnectionInterface
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.WifiPairingLogInterface
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.toIpAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

private const val TIMEOUT_MS = 30_000

internal class IoTDeviceConnectionService(
    private val connectivityManager: ConnectivityManager,
    private val wifiManager: WifiManager,
    private val pairLogService: WifiPairingLogInterface
) : IoTDeviceConnectionInterface {
    override suspend fun connect(
        ssid: String,
        password: String?
    ): Flow<PhoneToDeviceConnectionStatus> =
        withContext(Dispatchers.IO) {
            callbackFlow {
                connectivityManager.requestNetwork(
                    getNetworkRequest(ssid, password),
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            super.onAvailable(network)
                            logPairingEvent("$TAG: NetworkCallback - onAvailable()")

                            // this is hit after connect is hit on system dialog

                            connectivityManager.bindProcessToNetwork(network)

                            val gatewayIpAddress = getGatewayIpAddress(wifiManager)
                            val ipAddress = getIpAddress(wifiManager)

                            when {
                                gatewayIpAddress != null -> {
                                    logPairingEvent("$TAG: NetworkCallback - onAvailable() - Got Gateway IP Address. Sending connection status: Connected")
                                    trySend(PhoneToDeviceConnectionStatus.Connected(gatewayIpAddress = gatewayIpAddress))
                                }
                                ipAddress != null && WifiPairingService.isRealTek(ssid) -> {
                                    logPairingEvent("$TAG: NetworkCallback - onAvailable() - Got IP Address. Sending connection status: Connected")
                                    trySend(
                                        PhoneToDeviceConnectionStatus.Connected(
                                            gatewayIpAddress = ipAddress.replace(ipAddress.substring(ipAddress.lastIndexOf('.')), ".1")
                                        )
                                    )
                                }
                                else -> {
                                    logPairingEvent("$TAG: NetworkCallback - onAvailable() - No Gateway IP Address OR IP Address. Sending connection status: FailedToConnect")
                                    trySend(PhoneToDeviceConnectionStatus.FailedToConnect)
                                    disconnect(this)
                                }
                            }
                        }

                        override fun onUnavailable() {
                            super.onUnavailable()
                            Log.d(IoTDeviceConnectionService::class.simpleName, "onUnavailable")
                            logPairingEvent("$TAG: NetworkCallback - onUnavailable() - sending connection status: FailedToConnect")

                            trySend(PhoneToDeviceConnectionStatus.FailedToConnect)
                            disconnect(this)
                        }

                        override fun onLost(network: Network) {
                            super.onLost(network)
                            Log.d(IoTDeviceConnectionService::class.simpleName, "onLost")
                            logPairingEvent("$TAG: NetworkCallback - onLost() - sending connection status: FailedToConnect")

                            trySend(PhoneToDeviceConnectionStatus.FailedToConnect)
                            disconnect(this)
                        }

                        fun disconnect(networkCallback: ConnectivityManager.NetworkCallback?) {
                            networkCallback?.let {
                                connectivityManager.unregisterNetworkCallback(networkCallback)
                                connectivityManager.bindProcessToNetwork(null)
                            }
                        }

                        fun logPairingEvent(event: String) {
                            this@IoTDeviceConnectionService.logPairingEvent(event)
                        }
                    },
                    TIMEOUT_MS
                )

                awaitClose()
            }
        }

    private fun getNetworkRequest(
        ssid: String,
        password: String?
    ): NetworkRequest {
        val wifiSpecifierBuilder = WifiNetworkSpecifier.Builder()
        wifiSpecifierBuilder.setSsid(ssid)
        if (password != null) wifiSpecifierBuilder.setWpa2Passphrase(password)

        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiSpecifierBuilder.build())
            .build()
    }

    private fun getIpAddress(wifiManager: WifiManager): String? {
        val address = wifiManager.dhcpInfo.ipAddress

        return if (address == 0) {
            null
        } else {
            address.toIpAddress()
        }
    }

    private fun getGatewayIpAddress(wifiManager: WifiManager): String? {
        val address = wifiManager.dhcpInfo.gateway

        return if (address == 0) {
            null
        } else {
            address.toIpAddress()
        }
    }

    private fun logPairingEvent(event: String) {
        pairLogService.logPairingEventNoCallback(event)
    }

    companion object {
        private const val TAG = "DeviceConnectionService"
    }
}