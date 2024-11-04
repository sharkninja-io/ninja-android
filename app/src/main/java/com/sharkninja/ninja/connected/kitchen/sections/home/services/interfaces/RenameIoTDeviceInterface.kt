package com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces

import kotlinx.coroutines.flow.Flow

interface RenameIoTDeviceInterface {
    suspend fun renameIoTDevice(dsn: String, newName: String): Flow<Unit>
}