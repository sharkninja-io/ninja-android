package com.sharkninja.ninja.connected.kitchen.data.models.pairing

sealed class PhoneToDeviceConnectionStatus {
    object NotStarted: PhoneToDeviceConnectionStatus()
    class Connected(val gatewayIpAddress: String): PhoneToDeviceConnectionStatus()
    object UserCancelledConnection : PhoneToDeviceConnectionStatus()
    object FailedToConnect : PhoneToDeviceConnectionStatus()
}