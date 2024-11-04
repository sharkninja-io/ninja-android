package com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces

interface RequestLocationPermissionsInterface {
    fun getLocationPermissionStatus(): PermissionStatus
    fun requestLocationPermissions()
}

enum class PermissionStatus {
    HaventAsked,
    Granted,
    NotGranted
}