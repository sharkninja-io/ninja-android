package com.sharkninja.ninja.connected.kitchen.data.models.cookitems

data class TimerItem(
    val displayTimeString: String,
    val time: Int,
    var isSelected: Boolean
)