package com.sharkninja.ninja.connected.kitchen.data.enums

import com.sharkninja.ninja.connected.kitchen.R

enum class TempMode(val id: Int, val displayTemp: Int) {
    LOW(1, R.string.low),
    MED(2, R.string.med),
    HI(3, R.string.high),
    BUGLOW(401, R.string.unknown),
    BUGMED(451, R.string.unknown),
    BUGHIGH(501, R.string.unknown),
    NotSet(0, R.string.not_set)
}

fun getDisplayTemp(id: String): Int {
    val tempMode = TempMode.values().firstOrNull { tempMode -> tempMode.id == id.toInt() }
    // temporary fix for displayTemp loading before default value
    //new cook mode selected
    return tempMode?.displayTemp ?: TempMode.HI.displayTemp
}