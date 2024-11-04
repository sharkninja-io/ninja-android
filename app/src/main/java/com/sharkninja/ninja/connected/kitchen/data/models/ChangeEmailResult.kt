package com.sharkninja.ninja.connected.kitchen.data.models

data class ChangeEmailResult(
    val emailChangeSuccessful: Boolean,
    val authToken: String?
)