package com.sharkninja.ninja.connected.kitchen.extensions

import androidx.annotation.DrawableRes
import com.sharkninja.cloudcore.WifiNetwork
import com.sharkninja.ninja.connected.kitchen.R

@DrawableRes
fun WifiNetwork.getWifiSignalStrengthDrawable(): Int {
    return when (this.bars) {
        1,
        2 -> R.drawable.icon_wifi_signal_weak
        3 -> R.drawable.icon_wifi_signal_medium
        else -> R.drawable.icon_wifi_signal_strong
    }
}