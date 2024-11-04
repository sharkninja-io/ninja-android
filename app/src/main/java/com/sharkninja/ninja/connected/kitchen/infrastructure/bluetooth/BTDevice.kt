package com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth

class BTDevice {

}

enum class BTLEDataType {
    AUTH_REQUEST,
    AUTH_RESPONSE,
    AUTH_OK,
    WIFI_SCAN_REQUEST,
    WIFI_SCAN_REQUEST_DONE,
    WIFI_SCAN_RESULT,
    WIFI_JOIN_REQUEST,
    WIFI_JOIN_STATUS,
    GRILL_STATUS_REQUEST,
    GRILL_STATUS_RESPONSE,
    GRILL_COMMAND,
    GRILL_INFO_REQUEST,
    GRILL_INFO_RESPONSE
}

enum class BTLEWifiConnectionStatus {
    NONE,
    AP,
    CONNECTING,
    CONNECTED,
    ERROR,
    COUNT
}

fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }