package com.sharkninja.ninja.connected.kitchen.ext

import com.sharkninja.grillcore.Doneness
import com.sharkninja.grillcore.Doneness.*
import com.sharkninja.ninja.connected.kitchen.R

fun Doneness.displayName(): Int {
    return when(this) {
        Rare -> R.string.rare
        Rare1 -> R.string.rare1
        Rare2 -> R.string.rare2
        MedRare -> R.string.medrare
        MedRare3 -> R.string.medrare3
        MedRare4 -> R.string.medrare4
        Medium -> R.string.med
        Med5 -> R.string.med5
        Med6 -> R.string.med6
        MedWell -> R.string.medwell
        MedWell7 -> R.string.medwell7
        MedWell8 -> R.string.medwell8
        Well -> R.string.well
        Well9 -> R.string.well9
        NotSet -> R.string.not_set
    }
}