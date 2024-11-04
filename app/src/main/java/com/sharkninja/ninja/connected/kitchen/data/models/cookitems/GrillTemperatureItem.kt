package com.sharkninja.ninja.connected.kitchen.data.models.cookitems

data class GrillTemperatureItem(
    val tempString: String,
    val temp: Int,
    var isSelected: Boolean
)
