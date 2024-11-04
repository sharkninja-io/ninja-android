package com.sharkninja.ninja.connected.kitchen.data.enums

import android.content.Context
import androidx.annotation.StringRes
import com.sharkninja.grillcore.CookMode
import com.sharkninja.grillcore.CookMode.*
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.ui.views.DialState

data class CookUiState(
    val levelListBackground: Int = R.drawable.level_list_preheat,
    val selectedCookTypeTabBackground: Int = R.drawable.selected_cook_type_tab_preheat_background,
    val selectedThermometerTabDrawableBackground: Int = R.drawable.preheat_thermometer_selector_background_plugged_in,
    val unselectedThermometerTabDrawableBackground: Int = R.drawable.thermometer_selector_background_un_plugged,
    val progress: Int = 0,
    val progressDisplayValue: ProgressDialFormattedString? = null,
    val isTimedCook: Boolean = true,
    val isThermometer2Selected: Boolean = false,
    val skipPreheatButtonVisible: Boolean = false,
    val dialState: DialState = DialState.Preheating,
    val progressSubVisibility: Boolean = false,
    val progressSubText: Int = R.string.empty,
    val cookComplete: Boolean = false,
    val probeOneCardEnabled: Boolean = true,
    val probeTwoCardEnabled: Boolean = true,
    val showDialState: Boolean = true,
    val isOnline: Boolean = true
)

sealed class ProgressDialFormattedString {
    data class SimpleStringProgressDial(@StringRes val stringId:Int): ProgressDialFormattedString()
    data class TimerStringProgressDial(val string: String = "0:00"): ProgressDialFormattedString()
    data class NumberPercent(@StringRes val stringId:Int = R.string.percent_progress, val number:String): ProgressDialFormattedString()
    data class NumberTempUnit(@StringRes val stringId:Int = R.string.current_temp_and_temp_unit, val number:String, @StringRes val tempUnit: Int = R.string.faren): ProgressDialFormattedString()

}

fun ProgressDialFormattedString.getFormattedString(context: Context):String {
    return when(this) {
        is ProgressDialFormattedString.SimpleStringProgressDial -> context.getString(stringId)
        is ProgressDialFormattedString.TimerStringProgressDial -> string
        is ProgressDialFormattedString.NumberPercent -> context.getString(stringId, number)
        is ProgressDialFormattedString.NumberTempUnit -> context.getString(stringId, number, context.getString(tempUnit))
    }
}

data class CookModeCard(
    val displayName: Int,
    val drawable: Int
)

fun CookMode.getCookCardItem(): CookModeCard {
   return when(this) {
        Grill -> CookModeCard(
            displayName = R.string.grill,
            drawable =R.drawable.ic_grill
        )
        Roast -> CookModeCard(
            displayName = R.string.roast,
            drawable = R.drawable.ic_roast
        )
        Bake -> CookModeCard(
            displayName = R.string.bake,
            drawable =R.drawable.bake
        )
        AirCrisp -> CookModeCard(
            displayName = R.string.aircrisp,
            drawable =R.drawable.ic_aircrisp
        )
        Smoke -> CookModeCard(
            displayName = R.string.smoke,
            drawable = R.drawable.ic_smoke
        )
        Dehydrate -> CookModeCard(
            displayName = R.string.dehydrate,
            drawable = R.drawable.ic_dehydrate
        )
        Broil -> CookModeCard(
            displayName = R.string.broil,
            drawable = R.drawable.ic_broil
        )
        Reheat -> CookModeCard(
            displayName = R.string.reheat,
            drawable = R.drawable.ic_reheat
        )
        else -> CookModeCard(
            displayName = R.string.empty,
            drawable = R.drawable.ic_grill
        )
    }
}