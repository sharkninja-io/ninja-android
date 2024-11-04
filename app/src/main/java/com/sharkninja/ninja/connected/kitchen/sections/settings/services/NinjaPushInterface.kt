package com.sharkninja.ninja.connected.kitchen.sections.settings.services

interface NinjaPushInterface {

    suspend fun isDevicePushEnabled(dsn: String): Boolean

    suspend fun isDevicePushEnabledUserCache(): Result<Boolean>

    suspend fun subscribeToPushForDevice(dsn: String): Result<Unit>

    suspend fun unsubscribeToPushForDevice(dsn: String): Result<Unit>

    suspend fun setPushEnabledUserCache(isEnabled: Boolean): Result<Unit>
}