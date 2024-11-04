package com.sharkninja.ninja.connected.kitchen.sections.home.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.wifi.WifiManager
import androidx.core.location.LocationManagerCompat
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.IoTDeviceWifiScanningInterface
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.WifiPairingLogInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

internal class WifiManagerIoTDeviceWifiScanningService(
    private val context: Context,
    private val wifiManager: WifiManager,
    private val locationManager: LocationManager,
    private val pairLogService: WifiPairingLogInterface
) : IoTDeviceWifiScanningInterface {
    override suspend fun getIoTDeviceWifiNetworks(ssidPattern: String): Flow<List<String>> =
        withContext(Dispatchers.IO) {
            callbackFlow {
                val wifiScanReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        logReceiverPairingEvent("$TAG: Got Wifi Scanning Intent")
                        val results = wifiManager.scanResults
                            .filter { scanResult -> scanResult.SSID.contains(ssidPattern) }
                            .sortedByDescending { scanResult -> scanResult.level }
                            .map { scanResult -> scanResult.SSID }

                        if (results.isEmpty()) {
                            logReceiverPairingEvent("$TAG: No grill wifi networks found. Throwing NoGrillNetworksFoundThrowable error.")
                            close(NoGrillNetworksFoundThrowable("${WifiManagerIoTDeviceWifiScanningService::class.java.name}: No Grill networks with SSID pattern: $ssidPattern"))
                        } else {
                            logReceiverPairingEvent("$TAG: Found ${results.size} grill networks.")
                            trySend(results)
                        }
                    }

                    private fun logReceiverPairingEvent(event: String) {
                        this@WifiManagerIoTDeviceWifiScanningService.logPairingEvent(event)
                    }
                }

                val intentFilter = IntentFilter()
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                context.registerReceiver(wifiScanReceiver, intentFilter)

                val isGPSEnabled =
                    LocationManagerCompat.hasProvider(locationManager, LocationManager.GPS_PROVIDER)
                val isNetworkEnabled =
                    LocationManagerCompat.hasProvider(locationManager, LocationManager.NETWORK_PROVIDER)

                if (!isGPSEnabled && !isNetworkEnabled) {
                    logPairingEvent("$TAG: GPS & NETWORK not enabled. Throwing NetworkAndGPSNotEnabledThrowable error.")
                    throw NetworkAndGPSNotEnabledThrowable(
                        message = "${WifiManagerIoTDeviceWifiScanningService::class.java.name}: GPS and Network are not enabled. Can\'t scan for WiFi networks on M+ unless one of these is enabled."
                    )
                }

                val scanInitiated = wifiManager.startScan()
                if (!scanInitiated) {
                    logPairingEvent("$TAG: WIFI Scanning NOT Started. Throwing WifiNetworkScanNotStartedThrowable error.")
                    throw WifiNetworkScanNotStartedThrowable("${WifiManagerIoTDeviceWifiScanningService::class.java.name}: Failed to start scan for WiFi networks.")
                } else {
                    logPairingEvent("$TAG: WIFI Scanning Started.")
                }

                awaitClose {
                    context.unregisterReceiver(wifiScanReceiver)
                }
            }
        }

    private fun logPairingEvent(event: String) {
        pairLogService.logPairingEventNoCallback(event)
    }

    class NetworkAndGPSNotEnabledThrowable(message: String) : Throwable(message)
    class NoGrillNetworksFoundThrowable(message: String) : Throwable(message)
    class WifiNetworkScanNotStartedThrowable(message: String) : Throwable(message)

    companion object {
        private const val TAG = "IoTDeviceWifiScanningService"
    }
}