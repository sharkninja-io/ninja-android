package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment

abstract class BasePermissionRequester(
    private val fragment: Fragment,
    private val onGranted: () -> Unit,
    private val onDenied: () -> Unit
//    , private val onDisplayInfo:() -> Unit
) {
    protected abstract val permissions: Array<String>

    private val permissionRequest: ActivityResultLauncher<Array<String>> =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { grants ->
            onPermissionsResult(grants)
        }

    fun checkPermissions(context: Context) {
        val allGranted = permissions.all { p ->
            PermissionChecker.checkSelfPermission(
                context,
                p
            ) == PermissionChecker.PERMISSION_GRANTED
        }

//        val shouldDisplayExplanation = permissions.any { p -> fragment.shouldShowRequestPermissionRationale(p) }

//        if (shouldDisplayExplanation) {
        //do something
//            onDisplayInfo()
//        } else
        if (!allGranted) {
            permissionRequest.launch(permissions)
        } else {
            onGranted()
        }
    }

    private fun onPermissionsResult(grants: Map<String, Boolean>) {
        if (grants.all { grant -> grant.value }) {
            onGranted()
        } else {
            onDenied()
//            val userHadPreviouslyDenied = grants.keys.none { p -> fragment.shouldShowRequestPermissionRationale(p) }
//            if (userHadPreviouslyDenied) {
            // onDisplayInfo()
//                permissionRequest.launch(permissions)
//            } else {
//                // User clicked deny one time, show the error screen
//                onDenied()
//            }
        }
    }
}