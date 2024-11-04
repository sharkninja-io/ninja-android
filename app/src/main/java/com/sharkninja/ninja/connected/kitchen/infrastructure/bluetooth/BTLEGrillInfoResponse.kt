package com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth

data class BTLEGrillInfoResponse(
    val type: UByte?,
    val size: UByte?,
    val dsn: String?,
    val isOnboardedToWifi: UByte?,
    val wifiConnectionStatus: UByte?,
    val wifiRssi: UShort?,
    val verWifi: UInt?,
    val verSTM32: UInt,
    val verBT: UInt?
)

fun ByteArray.toBTLEGrillInfoResponse(): BTLEGrillInfoResponse {
    return BTLEGrillInfoResponse(
        this[0].toUByte(),
        this[1].toUByte(),
        this.decodeToString(2, 22)?.replace("\u0000", ""),
        this[23].toUByte(),
        this[24].toUByte(),
        this[25].toUShort(),
        this[27].toUInt(),
        this[31].toUInt(),
        this[35].toUInt()
    )
}
