package com.sharkninja.ninja.connected.kitchen.data.enums

import com.sharkninja.grillcore.DegreeType
import com.sharkninja.grillcore.DegreeType.*
import com.sharkninja.ninja.connected.kitchen.R


fun DegreeType.getDisplayName(): Int {
    return when(this) {
        Fahrenheit,
        NotSet -> R.string.faren
        Celsius -> R.string.celsius
    }
}
