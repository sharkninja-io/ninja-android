package com.sharkninja.ninja.connected.kitchen.infrastructure.cache.interfaces

import kotlinx.coroutines.flow.Flow

interface CacheInterface {
    suspend fun <T: Any> setUserCacheValue(key: String, data: T): Flow<Unit>
    suspend fun <T: Any> setAppCacheValue(key: String, data: T): Flow<Unit>
    suspend fun <T: Any> setCacheValue(path: String, key: String, value: T): Flow<Unit>
    suspend fun <T: Any> setUserCacheValueResult(key: String, data: T): Result<Unit>

    suspend fun <T: Any> getUserCacheValue(key: String, defaultValue: T? = null): Flow<T>
    suspend fun <T: Any> getAppCacheValue(key: String, defaultValue: T? = null): Flow<T>
    suspend fun <T : Any> getUserCacheValueResult(key: String, defaultValue: T? = null): Result<T>
    suspend fun <T: Any> getCacheValue(path: String, key: String, defaultValue: T? = null): Flow<T>
}