package com.sharkninja.ninja.connected.kitchen.ui.components.fields

import android.util.Patterns

class EmailAddressRepresentable(var string: String) {

    fun isEmailValid(): Boolean {
        var trimmed = string.replace(" ", "")
        var devPrefix: String? = null

        if (trimmed.startsWith("dev@")) {
            devPrefix = trimmed.take(4)
            trimmed = trimmed.drop(4)
        }

        return if (Patterns.EMAIL_ADDRESS.toRegex().matches(trimmed)) {
            string = if (devPrefix != null) {
                "$devPrefix$trimmed"
            } else {
                trimmed
            }
            true
        } else {
            false
        }
    }

    fun isDevDomain(): Boolean {
        return string.startsWith("dev@")
    }

    fun email(): String {
        return if (isDevDomain()) string.drop(4) else string
    }

    fun possibleDomains(): String? {
        val gmails = arrayOf("gamil.com", "gmai.com", "gmail.co",
            "gmail.con", "gmail.org", "gmailc.om",
            "gmaill.com", "gmal.com", "gmall.com",
            "gmil.com", "gnail.com", "gmsil.com")
        val hotmails = arrayOf("hitmail.com", "hormail.com", "hotail.com",
            "hotmai.com", "hotmail.co", "hotmail.con",
            "hotmaill.com", "hotmial.com")
        val yahoos = arrayOf("tahoo.com", "yaho.com", "yahoo.co",
            "yahoo.con", "yanoo.com", "yaoo.com",
            "yhoo.com", "yshoo.com")
        val iclouds = arrayOf("icloid.com", "icoud.com", "iloud.con")
        val aols = arrayOf("ail.com")

        val emailAfterAt = email().split("@")[1]

        return when {
            gmails.contains(emailAfterAt) -> {
                "gmail.com"
            }
            hotmails.contains(emailAfterAt) -> {
                "hotmail.com"
            }
            iclouds.contains(emailAfterAt) -> {
                "icloud.com"
            }
            yahoos.contains(emailAfterAt) -> {
                "yahoo.com"
            }
            aols.contains(emailAfterAt) -> {
                "aol.com"
            }
            else -> {
                null
            }
        }
    }

}