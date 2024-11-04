package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment

class AllPermissionsChecker(
    fragment: Fragment
) {
    private val permissions: Array<String> = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
    }

    fun areAllPermissionsGranted(context: Context): Boolean {
        val allGranted = permissions.all { p ->
            Log.d(Fragment::class.java.simpleName, "permissionCheck: ")
            PermissionChecker.checkSelfPermission(
                context,
                p
            ) == PermissionChecker.PERMISSION_GRANTED
        }
        return allGranted
    }
}