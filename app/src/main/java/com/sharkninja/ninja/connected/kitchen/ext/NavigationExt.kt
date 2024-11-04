package com.sharkninja.ninja.connected.kitchen.ext

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.safeNavigate(directions: NavDirections) {
    try {
        navigate(directions)
    } catch (error: Throwable) {
        Log.e("Navigation", "${error.message}")
    }
}