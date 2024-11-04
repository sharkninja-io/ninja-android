package com.sharkninja.ninja.connected.kitchen.data.models


sealed class ProfileAccountChangeEvents {
    object UpdateAccountSuccess : ProfileAccountChangeEvents()
    object UpdateAccountError : ProfileAccountChangeEvents()
    object UpdateAccountLoading : ProfileAccountChangeEvents()
    object DeleteAccountSuccess : ProfileAccountChangeEvents()
    object DeleteAccountError: ProfileAccountChangeEvents()
    object DeleteAccountLoading: ProfileAccountChangeEvents()
    object ActionProcessed : ProfileAccountChangeEvents()
}
