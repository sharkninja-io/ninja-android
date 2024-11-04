package com.sharkninja.ninja.connected.kitchen.ui.components.fields

class PasswordRepresentable(var string: String) {

    private val limitCriteria: Int = 8

    var isOverExpectedCriteria: Boolean = false
    var containsUppercase: Boolean = false
    var containsLowercase: Boolean = false
    var containsNumber: Boolean = false

    init {
        val trimmed = string.replace(" ", "")
        for (c in trimmed) {
            if (trimmed.count() >= limitCriteria) {
                isOverExpectedCriteria = true
            }

            if (c.isUpperCase()) {
                containsUppercase = true
            }

            if (c.isLowerCase()) {
                containsLowercase = true
            }

            if (c.isDigit()) {
                containsNumber = true
            }
        }
        this.string = trimmed
    }
}