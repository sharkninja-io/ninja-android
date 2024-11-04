package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters

import android.Manifest
import androidx.fragment.app.Fragment
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters.BasePermissionRequester

class LocationPermissionRequester(
    fragment: Fragment,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) : BasePermissionRequester(fragment, onGranted, onDenied) {
    override val permissions: Array<String> = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
}