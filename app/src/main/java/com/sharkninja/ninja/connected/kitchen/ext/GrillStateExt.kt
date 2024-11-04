package com.sharkninja.ninja.connected.kitchen.ext

import android.content.Context
import com.sharkninja.grillcore.CalculatedState
import com.sharkninja.grillcore.CookMode
import com.sharkninja.grillcore.GrillState
import com.sharkninja.ninja.connected.kitchen.data.constants.AppConstants
import com.sharkninja.ninja.connected.kitchen.data.enums.getDisplayTemp

fun CalculatedState.shouldNavigateFromCookToPrecook(isAlreadyNavigating: Boolean): Boolean {
   return isAlreadyNavigating.not() && this !== CalculatedState.Calculating && this !== CalculatedState.Done && isGrillActive().not()
}

fun CalculatedState.shouldNavigateFromPreCookToCook(isAlreadyNavigating: Boolean): Boolean {
    return isAlreadyNavigating.not() && this !== CalculatedState.Calculating && isGrillActive()
}

fun CalculatedState.canGoBackFromCookScreen(): Boolean {
    return this !== CalculatedState.Calculating && isGrillActive().not()
}

fun CalculatedState.hasStartedCook(): Boolean {
    return this !== CalculatedState.Calculating && isGrillActive()
}

fun CalculatedState.hasStoppedCook(): Boolean {
    return this !== CalculatedState.Calculating && isGrillActive().not()
}
fun CalculatedState.isGrillActive(): Boolean {
    return when (this) {
        // removed done from pre-cook dashboard to avoid getting stuck in Cook
        CalculatedState.Preheating,
        CalculatedState.Cooking,
        CalculatedState.Resting,
        CalculatedState.Igniting,
        CalculatedState.AddFood,
        CalculatedState.FlipFood,
        CalculatedState.LidOpenBeforeCook,
        CalculatedState.LidOpenDuringCook,
        CalculatedState.PlugInProbe1,
        CalculatedState.PlugInProbe2,
        CalculatedState.GetFood,
        CalculatedState.Done-> true

        else -> false
    }
}

fun GrillState.getDesiredTemp(context: Context, coldSmokeTemp: String): String {
    return if (cookMode == CookMode.Grill) {
        context.getString(getDisplayTemp(oven.desiredTemp.toString())).uppercase()

    } else if(cookMode == CookMode.Smoke && oven.desiredTemp.toString() == coldSmokeTemp) {
        AppConstants.COLD_SMOKE_DISPLAY
    } else {
        oven.desiredTemp.toString()
    }
}