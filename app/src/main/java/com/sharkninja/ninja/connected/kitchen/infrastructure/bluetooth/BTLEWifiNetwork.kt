package com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth

data class BTLEWifiNetwork(
    val type: Int?,
    val size: Int?,
    val index: Int?,
    val totalCount: Int?,
    val ssid: String?,
    val networkType: Int?,
    val channel: Int?,
    val rssi: Int?,
    val bars: Int?,
    val security: Int?,
    val bssid: UByteArray?)

fun ByteArray.toBTLEWifiNetwork(): BTLEWifiNetwork {
    return BTLEWifiNetwork(
        this[0].toInt(),
        this[1].toInt(),
        this[2].toInt(),
        this[4].toInt(),
        this.decodeToString(6, 39)?.replace("\u0000", ""),
        this[40].toInt(),
        this[41].toInt(),
        this[42].toInt(),
        this[43].toInt(),
        this[44].toInt(),
        ubyteArrayOf(
            this[48].toUByte(),
            this[49].toUByte(),
            this[50].toUByte(),
            this[51].toUByte(),
            this[52].toUByte(),
            this[53].toUByte()
        )
    )
}