package com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces

import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SignOutInterface

interface WifiPairingInterface :
    IoTDeviceConnectionInterface,
    IoTDeviceWifiScanningInterface,
    IoTDeviceHandshakeExecutionInterface,
    RenameIoTDeviceInterface,
    RegisterIoTDeviceInterface,
    GrillManagementInterface,
    WifiPairingLogInterface,
    SignOutInterface {
    fun initializePairedDevice(dsn: String, grillName: String, callback: (Boolean) -> Unit)
}