package com.sharkninja.ninja.connected.kitchen.data.models

import com.sharkninja.grillcore.Food

data class ProbePreset(
    var food: Food = Food.NotSet,
    var preset: Int = -1
)
