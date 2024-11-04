package com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces

interface RegisterIoTDeviceInterface {
    suspend fun registerIoTDevice(dsn: String, token: String)
}