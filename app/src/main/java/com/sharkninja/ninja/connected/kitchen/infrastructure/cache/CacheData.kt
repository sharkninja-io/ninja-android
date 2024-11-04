package com.sharkninja.ninja.connected.kitchen.infrastructure.cache

import androidx.core.text.isDigitsOnly
import com.sharkninja.mantleutilities.CacheDataValue
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
data class CacheData<T>(val data: T) {

    var value: CacheDataValue = when (data) {
        is String -> CacheDataValue.String(data)
        is Boolean -> CacheDataValue.Boolean(data)
        is Int -> CacheDataValue.Int(data)
        is Double -> CacheDataValue.Double(data)
        is Long -> {
            // todo there should be a CacheDataValue.Long, we shouldn't need to do this
            CacheDataValue.String(data.toString())
        }
        is Object -> {  // JSON
            val jsonObject = Json.encodeToString(data) // needs a static type?
            CacheDataValue.Object(jsonObject)
        }
        else -> CacheDataValue.Null(data as Unit)
    }
}

fun <T> CacheDataValue.unwrap(defaultValue: T?): T? {
    return when (this) {
        is CacheDataValue.String -> {
            // todo there should be a CacheDataValue.Long, we shouldn't need to do this
            val (cachedString) = this

            if (cachedString.isNotEmpty() && cachedString.isDigitsOnly()) {
                cachedString.toLong() as T
            } else {
                cachedString as T
            }
        }
        is CacheDataValue.Boolean -> {
            val (cachedBoolean) = this
            cachedBoolean as T
        }
        is CacheDataValue.Int -> {
            val (cachedInt) = this
            cachedInt as T
        }
        is CacheDataValue.Double -> {
            val (cachedDouble) = this
            cachedDouble as T
        }
        is CacheDataValue.Object -> {
            val (cachedJSONObject) = this
            cachedJSONObject as T
        }
        is CacheDataValue.Null -> {
            defaultValue
        }
    }
}