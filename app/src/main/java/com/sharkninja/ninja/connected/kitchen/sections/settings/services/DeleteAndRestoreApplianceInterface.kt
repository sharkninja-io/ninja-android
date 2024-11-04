package com.sharkninja.ninja.connected.kitchen.sections.settings.services

import com.sharkninja.ninja.connected.kitchen.data.models.Appliance
import kotlinx.coroutines.flow.Flow

interface DeleteAndRestoreApplianceInterface {
    fun restoreDefaults(appliance: Appliance): Flow<Boolean>
    fun unregisterDevice(appliance: Appliance): Flow<Boolean>
}