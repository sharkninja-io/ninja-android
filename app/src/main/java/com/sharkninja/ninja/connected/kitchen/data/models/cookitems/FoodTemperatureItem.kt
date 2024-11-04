package com.sharkninja.ninja.connected.kitchen.data.models.cookitems

import com.sharkninja.grillcore.Doneness


data class FoodTemperatureItem(
    val tempString: String,
    var isSelected: Boolean,
    val isIncrementNumber: Boolean,
    val donenessLevel: Doneness,
    val presetIndex: Int = -1
)