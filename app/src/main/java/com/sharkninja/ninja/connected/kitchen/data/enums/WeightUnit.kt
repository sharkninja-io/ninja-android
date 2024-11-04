package com.sharkninja.ninja.connected.kitchen.data.enums

enum class WeightUnit(val listDisplayName: String, val selection: String) {

    LB("Pounds (lb)", "Pounds (lb)"),
    G("Grams (g)", "Grams (g)"),
    KG("Kilograms (kg)", "Kilograms (kg)"),
    OZ("Ounces (oz)", "Ounces (oz)");

    companion object {
        fun getEnumByName(name: String): State? {
            for (i in State.values()) {
                if (i.name == name) return i
            }
            return null
        }
    }
}

fun String.toWeightUnit(): WeightUnit {
    return WeightUnit.valueOf(this)
}