package com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth

data class BTLEWifiJoinStatus(
    val type: Int?,
    val size: Int?,
    val progress: Int?,
    val result: Int?,
    val error: Int?)

fun ByteArray.toBTLEWifiJoinStatus(): BTLEWifiJoinStatus {
    return BTLEWifiJoinStatus(
        this[0].toInt(),
        this[1].toInt(),
        this[2].toInt(),
        this[3].toInt(),
        this[4].toInt()
    )
}