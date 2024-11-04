package com.sharkninja.ninja.connected.kitchen.sections.authentication.services

import com.sharkninja.grillcore.User
import com.sharkninja.ninja.connected.kitchen.data.constants.AylaConstants.Companion.AYLA_EMAIL_TEMPLATE_DEV
import com.sharkninja.ninja.connected.kitchen.data.constants.AylaConstants.Companion.AYLA_EMAIL_TEMPLATE_PROD
import com.sharkninja.ninja.connected.kitchen.data.constants.AylaConstants.Companion.AYLA_EMAIL_TEMPLATE_PROD_EU
import com.sharkninja.ninja.connected.kitchen.data.constants.AylaConstants.Companion.AYLA_PASSWORD_TEMPLATE_DEV
import com.sharkninja.ninja.connected.kitchen.data.constants.AylaConstants.Companion.AYLA_PASSWORD_TEMPLATE_PROD
import com.sharkninja.ninja.connected.kitchen.data.constants.AylaConstants.Companion.AYLA_PASSWORD_TEMPLATE_PROD_EU
import com.sharkninja.ninja.connected.kitchen.data.enums.RegionCode
import com.sharkninja.ninja.connected.kitchen.data.enums.toRegionCode
import com.sharkninja.ninja.connected.kitchen.ext.isDevEnv
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService.CloudCoreConstants.CACHE_KEY_COUNTRY_REGION
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService.CloudCoreConstants.CACHE_KEY_TERMS_ACCEPTED
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.EcommerceRepositoryInterface
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProfile
import com.sharkninja.ninja.connected.kitchen.sections.authentication.services.interfaces.AuthenticationInterface
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.internal.trimSubstring

class AuthenticationService(
    private val cacheService: CacheService,
    private val ecommerceRepositoryInterface: EcommerceRepositoryInterface
) : AuthenticationInterface {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val completableDeferred = CompletableDeferred<Result<Unit>>()

            val result = User.login(email, password)
            completableDeferred.complete(result)
            completableDeferred.await()
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return User.getUsername().getOrNull() != null
    }

    override suspend fun hasUserSession(): Boolean {
        return User.getAccessToken().getOrNull() != null
    }

    override suspend fun createAccount(
        email: String, password: String,
        emailTemplateId: String?, emailSubject: String?, emailBodyHTML: String?,
        regionCode: String
    ) = callbackFlow {
        val isDev = email.isDevEnv()
        val emailTemplate = if (isDev) {
            AYLA_EMAIL_TEMPLATE_DEV
        } else {
            when (regionCode) {
                "GB", "DE" -> AYLA_EMAIL_TEMPLATE_PROD_EU
                else -> AYLA_EMAIL_TEMPLATE_PROD
            }
        }
        User.create(
            password,
            email,
            emailTemplate
        )
            .onSuccess { result -> trySend(result) }
            .onFailure { error -> trySend(error) }
        awaitClose()
    }

    override suspend fun confirmAccount(token: String) = callbackFlow {
        User.confirmAccount(token)
            .onSuccess { result -> trySend(result) }
            .onFailure { error -> trySend(error) }
        awaitClose()
    }

    override suspend fun sendConfirmation(
        email: String, emailTemplateId: String?,
        emailSubject: String?, emailBodyHTML: String?,
        regionCode: String
    ) = callbackFlow {
        val isDev = email.isDevEnv()
        val emailTemplate = if (isDev) {
            AYLA_EMAIL_TEMPLATE_DEV
        } else {
            when (regionCode) {
                "GB", "DE" -> AYLA_EMAIL_TEMPLATE_PROD_EU
                else -> AYLA_EMAIL_TEMPLATE_PROD
            }
        }
        User.confirmAccountRequest(
            email,
            emailTemplate
        ).onSuccess { result -> trySend(result) }
            .onFailure { error -> trySend(error) }
        awaitClose()
    }

    override suspend fun requestPasswordReset(
        email: String,
        emailTemplateId: String?,
        emailSubject: String?,
        emailBodyHTML: String?,
        regionCode: String
    ) = callbackFlow {
        val isDev = email.isDevEnv()
        val template = if (isDev) {
            AYLA_PASSWORD_TEMPLATE_DEV
        } else {
            when (regionCode) {
                "GB", "DE" -> AYLA_PASSWORD_TEMPLATE_PROD_EU
                else -> AYLA_PASSWORD_TEMPLATE_PROD
            }
        }
        User.resetPasswordRequest(email, template)
            .onFailure { error -> trySend(error) }
            .onSuccess { result -> trySend(result) }
        awaitClose()
    }

    override suspend fun resetPassword(
        token: String,
        password: String,
        passwordConfirmation: String
    ) = callbackFlow {
        User.resetPassword(token, password, passwordConfirmation)
            .onFailure { error -> trySend(error) }
            .onSuccess { result -> trySend(result) }
        awaitClose()
    }

    override suspend fun getSelectedRegionCode(): Flow<RegionCode> {
        return withContext(Dispatchers.IO) {
            flow {
                cacheService.getUserCacheValue<String>(key = CACHE_KEY_COUNTRY_REGION)
                    .collect { regionCodeString ->
                        emit(regionCodeString.toRegionCode())
                    }
            }
        }
    }

    override suspend fun setSelectedRegionCode(regionCode: RegionCode): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            cacheService.setUserCacheValue(
                key = CACHE_KEY_COUNTRY_REGION,
                data = regionCode.item.code
            )
        }
    }

    override suspend fun isTermsAccepted(): Flow<Boolean> {
        return withContext(Dispatchers.IO) {
            flow {
                cacheService.getAppCacheValue<Boolean>(key = CACHE_KEY_TERMS_ACCEPTED, defaultValue = false)
                    .collect { isAccepted ->
                        emit(isAccepted)
                    }
            }
        }
    }

    override suspend fun setTermsAccepted(): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            cacheService.setAppCacheValue(key = CACHE_KEY_TERMS_ACCEPTED, data = true)
        }
    }

    override suspend fun createEcommerceUserProfile(email: String, countryCode: String): Flow<Unit> {
        val hasDevPrefix = email.trim().startsWith("dev@", true)
        val profileEmail = (if (hasDevPrefix) email.trimSubstring(startIndex = 4) else email).trim()
        return ecommerceRepositoryInterface.createProfile(
            EcommerceProfile(email = profileEmail),
            countryCode,
            hasDevPrefix
        )
    }

    override suspend fun getEcommerceUserProfile(countryCode: String): Flow<EcommerceProfile?> {
        return ecommerceRepositoryInterface.getProfileFromServer(countryCode)
    }
}