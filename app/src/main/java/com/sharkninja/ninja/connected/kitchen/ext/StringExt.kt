package com.sharkninja.ninja.connected.kitchen.ext

import okhttp3.internal.trimSubstring

fun String?.getOrEmpty(): String {
    return this ?: ""
}

fun String?.isDevEnv(): Boolean {
    return this?.startsWith("dev@") ?: false
}

fun String.getEmailForIntershop(): String {
   return if(this.isDevEnv()) {
        trimSubstring(startIndex = 4).trim()
    } else {
        this.trim()
    }
}