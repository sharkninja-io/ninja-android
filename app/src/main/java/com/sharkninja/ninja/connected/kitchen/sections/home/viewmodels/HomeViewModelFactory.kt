package com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth.BluetoothService
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.CookInterface
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.WifiPairingInterface

class HomeViewModelFactory(private val wifiPairingInterface: WifiPairingInterface,
                           private val bluetoothService: BluetoothService,
                           private val cookService: CookInterface
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(WifiPairingInterface::class.java, BluetoothService::class.java, CookInterface::class.java)
            .newInstance(wifiPairingInterface, bluetoothService, cookService)
    }
}