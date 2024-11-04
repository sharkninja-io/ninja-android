package com.sharkninja.ninja.connected.kitchen.data.models

data class DeleteAccountState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean? = null,
    val isFailure: Boolean? = null
)
