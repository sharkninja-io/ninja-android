package com.sharkninja.ninja.connected.kitchen.data.models

import com.sharkninja.ninja.connected.kitchen.data.models.ConnectionStatusToolbar.*


enum class ConnectionStatusToolbar {
    WifiOnly,
    BluetoothOnly,
    Online,
    Offline
}

fun getConnectionStatus(isBtConnected: Boolean, isWifiConnected: Boolean): ConnectionStatusToolbar {
   return if(isBtConnected && isWifiConnected) {
        Online
    } else if(isBtConnected) {
        BluetoothOnly
   } else if(isWifiConnected) {
       WifiOnly
   } else {
       Offline
   }
}
