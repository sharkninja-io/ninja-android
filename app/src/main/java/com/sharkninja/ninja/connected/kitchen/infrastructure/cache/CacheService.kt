package com.sharkninja.ninja.connected.kitchen.infrastructure.cache

import com.sharkninja.grillcore.Cache
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.interfaces.CacheInterface
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CacheService : CacheInterface {

    override suspend fun <T : Any> setUserCacheValue(key: String, data: T): Flow<Unit> =
        setCacheValue(
            path = CloudCoreConstants.USER_CACHE_PATH,
            key = key,
            value = data
        )

    override suspend fun <T : Any> setAppCacheValue(key: String, data: T): Flow<Unit> =
        setCacheValue(
            path = CloudCoreConstants.APP_CACHE_PATH,
            key = key,
            value = data
        )

    override suspend fun <T : Any> setCacheValue(path: String, key: String, value: T): Flow<Unit> =
        callbackFlow {
            Cache.setCachedData(
                path = path,
                key = key,
                data = CacheData(value).value
            ).onFailure { cause ->
                cancel(
                    message = "${CacheService::class.java.simpleName}: Failed to cache data($value).\n${cause.message}",
                    cause = cause
                )
            }.onSuccess(::trySend)

            awaitClose()
        }

    override suspend fun <T : Any> setUserCacheValueResult(key: String, data: T): Result<Unit> {
        val completableDeferred: CompletableDeferred<Result<Unit>> = CompletableDeferred()

        val result = Cache.setCachedData(
            path = CloudCoreConstants.USER_CACHE_PATH,
            key = key,
            data = CacheData(data).value
        )
        completableDeferred.complete(result)

        return completableDeferred.await()
    }

    override suspend fun <T : Any> getUserCacheValue(key: String, defaultValue: T?) = getCacheValue(
        path = CloudCoreConstants.USER_CACHE_PATH,
        key = key,
        defaultValue = defaultValue
    )

    override suspend fun <T : Any> getAppCacheValue(key: String, defaultValue: T?) = getCacheValue(
        path = CloudCoreConstants.APP_CACHE_PATH,
        key = key,
        defaultValue = defaultValue
    )


    override suspend fun <T : Any> getUserCacheValueResult(
        key: String,
        defaultValue: T?
    ): Result<T> {
        val completableDeferred: CompletableDeferred<Result<T>> = CompletableDeferred()

        Cache.getCachedData(
            path = CloudCoreConstants.USER_CACHE_PATH,
            key = key
        ).onSuccess { cacheDataValue ->
            completableDeferred.complete(
                cacheDataValue.unwrap(defaultValue)?.let { value ->
                    Result.success(value)
                } ?: Result.failure(
                    Throwable(
                        message = "${CacheService::class.java.simpleName}: Failed to get non-null value from cache for path(${CloudCoreConstants.USER_CACHE_PATH}), key($key), defaultValue($defaultValue)."
                    )
                )
            )
        }.onFailure { throwable ->
            completableDeferred.complete(
                Result.failure(
                    Throwable(
                        message = "${CacheService::class.java.simpleName}: Failed to get value from cache for path(${CloudCoreConstants.USER_CACHE_PATH}), key($key), defaultValue($defaultValue).\n${throwable.message}"
                    )
                )
            )
        }

        return completableDeferred.await()
    }

    override suspend fun <T : Any> getCacheValue(path: String, key: String, defaultValue: T?) =
        callbackFlow {
            Cache.getCachedData(path = path, key = key)
                    .onFailure { cause ->
                        defaultValue?.let {
                            trySend(defaultValue)
                        } ?: cancel(
                            message = "${CacheService::class.java.simpleName}: Error while getting $key from cache($path).\n${cause.message}",
                            cause = cause
                        )
                    }
                    .onSuccess { cacheDataValue ->
                        val unwrapped = cacheDataValue.unwrap(defaultValue)
                        if (unwrapped == null) {
                            cancel(
                                message = "${CacheService::class.java.simpleName}: key($key) was not found in cache($path) and the default value was null."
                            )
                        } else {
                            trySend(unwrapped)
                        }
                    }

            awaitClose()
        }

    object CloudCoreConstants {
        const val USER_CACHE_PATH = "user"
        const val APP_CACHE_PATH = "app"
        const val CACHE_KEY_COUNTRY_REGION = "countryRegionSelection"
        const val CACHE_KEY_TERMS_ACCEPTED = "didUserAcceptedTermsAndConditions"
        const val CACHE_KEY_TEMPERATURE_UNIT = "temperatureUnit"
        const val CACHE_KEY_WEIGHT_UNIT = "weightUnit"
        const val CACHE_KEY_NUM_OF_COOKS = "numCooks"
        const val CACHE_KEY_SHOW_APP_RATING_PROMPT = "showAppRatingPrompt"
        const val CACHE_KEY_PLACE_YOUR_THERMOMETER_DIALOG = "shouldShowPlaceYourThermometerDialog"
        const val CACHE_KEY_PUSH_NOTIFICATIONS_ENABLED = "pushNoficationsEnabled"
    }
}