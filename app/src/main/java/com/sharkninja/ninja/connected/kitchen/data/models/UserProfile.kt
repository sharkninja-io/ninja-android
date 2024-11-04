package com.sharkninja.ninja.connected.kitchen.data.models

data class UserProfile(
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val country: String = "",
    val areaCode: String = "",
    val phoneNumber: String = "",
    val customerNo: String = ""
)