package com.sharkninja.ninja.connected.kitchen.data.models.pairing

data class BluetoothPermissionsState(
    val nearbyDevicesPermission: Boolean = false,
    val locationPermission: Boolean = false,
    val bluetoothEnabled: Boolean = false
)