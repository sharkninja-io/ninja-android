package com.sharkninja.ninja.connected.kitchen.data.models

import com.sharkninja.grillcore.Grill
import com.sharkninja.grillcore.GrillDetails

data class Appliance(
    val name: String = "",
    val details: GrillDetails = GrillDetails(
        serialNumber = "",
        modelNumber = "",
        dsn = "",
        mac = "",
        firmwareVersion = "",
        otaFirmwareVersion = ""
    ),
    val macAddress: String = "",
    val grillObject: Grill? = null
)