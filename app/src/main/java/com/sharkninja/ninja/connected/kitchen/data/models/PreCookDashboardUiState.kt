package com.sharkninja.ninja.connected.kitchen.data.models

import com.sharkninja.grillcore.CookMode
import com.sharkninja.grillcore.Doneness
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel.Companion.PROBE_NOT_SET

data class PreCookDashboardUiState(
    val duration: Int = 0,
    val grillTemp: String = "3",
    val probe0FoodTempValue: String = PROBE_NOT_SET,
    val probe1FoodTempValue: String = PROBE_NOT_SET,
    val probe0Doneness: Doneness = Doneness.NotSet,
    val probe1Doneness: Doneness = Doneness.NotSet,
    val probe0PluggedIn: Boolean = false,
    val probe1PluggedIn: Boolean = false
)

data class PreCookDashboardButtonState(
    val isOnline: Boolean = false,
    val isTimedCook: Boolean = true,
    val isProbeOneToggleOn: Boolean = false,
    val isProbeTwoToggleOn: Boolean = false,
    val cookMode: CookMode = CookMode.Grill,
    val isSmokeFeatureOn: Boolean = false
) {
    fun getDashboardButtonEnabled(): Boolean {
        return isOnline && (isTimedCook || (isProbeOneToggleOn || isProbeTwoToggleOn))
    }
    fun getDashboardButtonText(): Int {
        return if(isOnline) {
            getOnlineButtonTextFromCookMode()
        } else {
            R.string.start_cooking_button_offline
        }
    }

    private fun getOnlineButtonTextFromCookMode(): Int {
      return if (isSmokeFeatureOn || cookMode == CookMode.Smoke) {
            R.string.start_ignition_button_online
        } else if(cookMode == CookMode.Dehydrate || cookMode == CookMode.Broil || cookMode == CookMode.Reheat) {
            R.string.start_cooking_button_online
        } else {
            R.string.start_preheating_button_online
        }
    }
}
