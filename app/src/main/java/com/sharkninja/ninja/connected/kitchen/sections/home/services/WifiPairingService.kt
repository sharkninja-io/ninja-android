package com.sharkninja.ninja.connected.kitchen.sections.home.services

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.sharkninja.cloudcore.CloudCoreError
import com.sharkninja.cloudcore.CloudCoreErrorType
import com.sharkninja.cloudcore.WifiNetwork
import com.sharkninja.grillcore.*
import com.sharkninja.grillcore.Grill.Companion.setName
import com.sharkninja.grillcore.GrillManager.Companion.currentGrill
import com.sharkninja.grillcore.GrillManager.Companion.grills
import com.sharkninja.grillcore.WifiManager.Companion.registerDevice
import com.sharkninja.grillcore.WifiManager.Companion.resultCallback
import com.sharkninja.grillcore.WifiManager.Companion.setSelectedNetwork
import com.sharkninja.grillcore.WifiManager.Companion.startPairing
import com.sharkninja.grillcore.WifiManager.Companion.wifiNetworksCallback
import com.sharkninja.ninja.connected.kitchen.data.models.pairing.PhoneToDeviceConnectionStatus
import com.sharkninja.ninja.connected.kitchen.sections.authentication.activities.AuthenticationActivity
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.IoTDeviceWifiScanningInterface
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.WifiPairingInterface
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.WifiPairingLogInterface
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SignOutInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class WifiPairingService(
    context: Context,
    private val signOutService: SignOutInterface
) : WifiPairingInterface {
    private val pairingLogService: WifiPairingLogInterface = PairingLogService(context)

    val ctx = context

    private val iotDeviceConnectionService: IoTDeviceConnectionService =
        IoTDeviceConnectionService(
            connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager,
            wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager,
            pairLogService = pairingLogService
        )

    private val iotDeviceWifiScanningService: IoTDeviceWifiScanningInterface =
        WifiManagerIoTDeviceWifiScanningService(
            context = context,
            wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager,
            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager,
            pairLogService = pairingLogService
        )

    override suspend fun getIoTDeviceWifiNetworks(ssidPattern: String): Flow<List<String>> {
        return iotDeviceWifiScanningService.getIoTDeviceWifiNetworks(ssidPattern)
    }

    override suspend fun connect(
        ssid: String,
        password: String?
    ): Flow<PhoneToDeviceConnectionStatus> {
        return iotDeviceConnectionService.connect(ssid, password)
    }

    override suspend fun startPairingIoTDeviceProcess(ipAddress: String) {
        pairingLogService.logPairingEvent("${WifiPairingService::class.java.simpleName}: Start Pairing IoT Device").first()
        startPairing(deviceGatewayIP = ipAddress)
    }

    override suspend fun observeWifiNetworksSeenByDevice(): Flow<List<WifiNetwork>> {
        return callbackFlow {
                wifiNetworksCallback = { wifiNetworks ->
                    trySend(wifiNetworks)
                }

                awaitClose()
            }
    }

    override suspend fun setSelectedWifiNetwork(wifiNetwork: WifiNetwork) {
        setSelectedNetwork(wifiNetwork)
    }

    override suspend fun waitForDeviceToComeOnline(
        completionCallback: (Result<String>) -> Unit
    ) {
        resultCallback = { result ->
            completionCallback.invoke(result)
        }
    }

    override suspend fun cancelPairing() {
        withContext(Dispatchers.IO) {
            cancelPairing()
        }
    }

    override suspend fun renameIoTDevice(dsn: String, newName: String): Flow<Unit> {
        return callbackFlow {
            setName(
                dsn = dsn,
                newName = newName
            )?.onSuccess { trySend(Unit) }
                ?.onFailure { throwable ->
                    println("!!!FAILED TO RENAME DEVICE!!!")
//                    throw Throwable("${WifiPairingService::class.java.name}: Failed to rename IoT device($dsn) to $newName.\n${throwable.message}")
                }?: throw Throwable("${WifiPairingService::class.java.name}: CloudCoreSDK was null.")

            awaitClose()
        }
    }

    override suspend fun registerIoTDevice(dsn: String, token: String) {
        registerDevice(dsn, token)
    }

    override suspend fun fetchGrills(): Flow<List<Grill>> {
        return callbackFlow {
            GrillManager.fetchGrills()
                ?.onSuccess { trySend(it) }
                ?.onFailure {
                    val ccErr = it as? CloudCoreError
                    ccErr?.let {
                        if (it.errorType == CloudCoreErrorType.NotAuthorized) {
                            ctx.startActivity(
                                Intent(ctx, AuthenticationActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            )
                        }
                    }
                }

            awaitClose()
        }
    }

    override fun getGrills(): List<Grill> {
        return grills
    }

    override suspend fun getCurrentGrill(): Flow<Grill?> {
        return flow { emit(currentGrill) }
    }

    override suspend fun setSelectedGrill(grill: Grill): Flow<Grill?> {
        return flow {
            currentGrill = grill
            emit(currentGrill)
        }
    }

    override fun initializePairedDevice(
        dsn: String,
        robotName: String,
        callback: (Boolean) -> Unit
    ) {
//        RobotStateMachineService.start {
//            val newRobot = it?.filter { it.dsn == dsn }?.firstOrNull()
//            if (newRobot != null) {
//                RobotStateMachineService.setCurrentRobot(newRobot)
//                val renameResult = newRobot.setName(robotName)
//                callback(renameResult.isSuccess)
//            } else {
//                callback(false)
//            }
//        }
    }

    override suspend fun logPairingEvent(event: String): Flow<Unit> {
        return pairingLogService.logPairingEvent(event = event)
    }

    override suspend fun getPairingLog(): Flow<String> {
        return flow { }
    }

    override fun logPairingEventNoCallback(event: String) {

    }

    override suspend fun signOut(): Flow<Unit> {
        return signOutService.signOut()
    }

    companion object
}

fun WifiPairingService.Companion.isRealTek(ssid: String): Boolean {
//    if (ssid.contains("750X")) {
//        return true
//    }
//
//    if (ssid.contains("750P")) {
//        return true
//    }
//
//    if (ssid.contains("750G")) {
//        return true
//    }
//
//    if (ssid.contains("871P")) {
//        return true
//    }

    return true
}