package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters

import android.Manifest
import androidx.fragment.app.Fragment

class NearbyDevicesPermissionRequester(
    fragment: Fragment,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) : BasePermissionRequester(fragment, onGranted, onDenied) {
    override val permissions: Array<String> = arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
}