package com.sharkninja.ninja.connected.kitchen.data.enums

enum class DisplayTimeUnit(val shortName: String) {
    Minutes("Min"),
    Seconds("Sec")
}

fun String.getTimeUnitShortName(): String {
    val unit = DisplayTimeUnit.values().first { it.name == this }
    return unit.shortName
}