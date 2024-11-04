package com.sharkninja.ninja.connected.kitchen.data.models.pairing

data class BluetoothPermissionErrorState(
    val isCheckPermissionsOnResume: Boolean = false,
    val isActionGoToSettings: Boolean = false
)