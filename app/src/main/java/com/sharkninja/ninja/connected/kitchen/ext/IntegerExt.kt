package com.sharkninja.ninja.connected.kitchen.ext

import android.content.res.Resources
import com.sharkninja.grillcore.CookMode
import com.sharkninja.ninja.connected.kitchen.data.models.Duration
import com.sharkninja.ninja.connected.kitchen.data.models.cookitems.TimerItem
import java.util.concurrent.TimeUnit

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.fromSecondsToHHmm(): String {
    val minutes = TimeUnit.SECONDS.toMinutes(this.toLong())
    val hours = TimeUnit.MINUTES.toHours(minutes)
    val remainMinutes = minutes - TimeUnit.HOURS.toMinutes(hours)
    return String.format("%d:%02d", hours, remainMinutes)
}

fun Int.fromSecondsToMMss(): String {
    val minutes = this / 60
    val remainSeconds = this % 60
    return String.format("%d:%02d", minutes, remainSeconds)
}

fun Int.fromSecondsToHHMM(): String {
    val minutes = TimeUnit.SECONDS.toMinutes(this.toLong())
    val hours = TimeUnit.MINUTES.toHours(minutes)
    val remainMinutes = minutes - TimeUnit.HOURS.toMinutes(hours)
    return String.format("%d:%02d", hours, remainMinutes)
}

fun Int.toDoubleDigits(): String {
    return String.format("%02d", this)
}

fun Int.fromSecondsToProgressDialFormat(): String {
    return if (this > 3599) {
        this.fromSecondsToHHMM()
    } else {
        this.fromSecondsToMMss()
    }
}

fun Int.fromMinutesToHHmm(): String {
    val hours = TimeUnit.MINUTES.toHours(this.toLong())
    val remainMinutes = this - TimeUnit.HOURS.toMinutes(hours)
    return String.format("%d:%02d", hours, remainMinutes)
}

fun Int.toTimeDisplay(cookMode: CookMode, isDialValue: Boolean): String {
    return when (cookMode) {
        CookMode.Broil -> {
            this.fromSecondsToMMss()
        }

        else -> {
            if (isDialValue) {
                this.fromMinutesToHHmm()
            } else {
                this.fromSecondsToHHmm()
            }
        }
    }
}

fun Int.toTimeDisplayProgressDial(cookMode: CookMode): String {
    return when (cookMode) {
        CookMode.Broil -> {
            this.fromSecondsToMMss()
        }

        else -> {
            this.fromSecondsToProgressDialFormat()
        }
    }
}


fun timeListToMap(list: List<Int>): Map<TimerItem, List<TimerItem>> {
    // this returns the correct output
    val map = mutableMapOf<TimerItem, List<TimerItem>>()
    val keyList = mutableListOf<Int>()

    keyList.add(0)
    list.forEach { timerValue ->
        if (timerValue % 60 == 0) {
            keyList.add(timerValue)
        }
    }

    keyList.forEach { key ->
        val valuesList = list.filter { num -> num >= key && num < key + 60 }.map {
            val timeValue = if (key == 0) it else it - key
            TimerItem(
                displayTimeString = timeValue.toDoubleDigits(),
                time = timeValue,
                isSelected = false
            )
        }

        map[TimerItem(
            displayTimeString = (key / 60).toString(),
            time = key,
            isSelected = false
        )] = valuesList
    }

    println("$map")
    return map
}

fun Int.toSplitDuration(): Duration {
    val defaultLeftValue = this/60
    val defaultRightValue = this - (defaultLeftValue * 60)
    return Duration(
        defaultLeft = defaultLeftValue,
        defaultRight = defaultRightValue
    )
}

fun Int.toSplitDurationMC(cookMode: CookMode): Duration {
   return if(cookMode == CookMode.Broil) {
        this.toSplitDuration()
    } else {
        val defaultLeft = this/3600
        val defaultRight = (this - (defaultLeft * 3600))/60
        Duration(
            defaultLeft = defaultLeft,
            defaultRight = defaultRight
        )
    }
}

