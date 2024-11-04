package com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces

import com.sharkninja.ninja.connected.kitchen.data.models.pairing.PhoneToDeviceConnectionStatus
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder

interface IoTDeviceConnectionInterface {
    suspend fun connect(ssid: String, password: String? = null): Flow<PhoneToDeviceConnectionStatus>
}

fun Int.toIpAddress(): String? {
    // Convert endian-ness if needed
    val ip = if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
        Integer.reverseBytes(this)
    } else {
        this
    }

    val ipBytes = BigInteger.valueOf(ip.toLong()).toByteArray()

    return try {
        InetAddress.getByAddress(ipBytes).hostAddress
    } catch (e: UnknownHostException) {
        null
    }
}