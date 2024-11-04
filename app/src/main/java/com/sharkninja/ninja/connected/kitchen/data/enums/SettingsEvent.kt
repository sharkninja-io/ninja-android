package com.sharkninja.ninja.connected.kitchen.data.enums

sealed class SettingsEvent {

    object SignOut: SettingsEvent()

    object UserSignedOut: SettingsEvent()
    object UserSignOutError: SettingsEvent()
    object ActionProcessed: SettingsEvent()
}