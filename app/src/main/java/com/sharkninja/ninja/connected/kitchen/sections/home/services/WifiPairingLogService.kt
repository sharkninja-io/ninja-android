package com.sharkninja.ninja.connected.kitchen.sections.home.services

import android.content.Context
import android.util.Log
import com.sharkninja.grillcore.GrillCoreSDK
import com.sharkninja.grillcore.WifiManager.Companion.readPairingLog
import com.sharkninja.grillcore.WifiManager.Companion.writeToPairingLog
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.WifiPairingLogInterface
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

const val PAIRING_LOG_TAG = "DevicePairingLog"

class PairingLogService(
    context: Context
) : WifiPairingLogInterface {
    private val cloudCore by lazy { GrillCoreSDK ?: GrillCoreSDK.initialize(context) }

    override suspend fun getPairingLog() = callbackFlow {
        try {
            val content = readPairingLog().getOrThrow()
            trySend(content)
        } catch (error: Exception) {
            cancel(
                message = "${PairingLogService::class.java.simpleName}: Error while getting pairing logs.\n${error.message}",
                cause = error
            )
        }
        awaitClose()
    }

    override suspend fun logPairingEvent(event: String) =  callbackFlow {
        try {
            Log.d(PAIRING_LOG_TAG, event)
            writeToPairingLog(event).getOrThrow()
            trySend(Unit)
        } catch (error: Throwable) {
            cancel(
                message = "${PairingLogService::class.java.simpleName}: Error writing to pairing logs.\n${error.message}",
                cause = error
            )
        }
        awaitClose()
    }

    override fun logPairingEventNoCallback(event: String) {
        Log.d(PAIRING_LOG_TAG, event)
        writeToPairingLog(event).getOrNull()
    }
}