package com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce

import android.content.Context
import android.util.Log
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceChangeEmail
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProfile
import com.sharkninja.ninja.connected.kitchen.infrastructure.ext.fromJsonString
import com.sharkninja.ninja.connected.kitchen.infrastructure.ext.toJsonString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class EcommerceProfileService(
    private val intershopEcommerceDatasource: IntershopEcommerceDatasource,
    private val cacheService: CacheService
) : EcommerceRepositoryInterface {

    override suspend fun createProfile(profile: EcommerceProfile, countryCode: String, useDev: Boolean): Flow<Unit> {
        return intershopEcommerceDatasource.createProfile(
            email = profile.email,
            countryCode = countryCode,
            useDev = useDev
        )
    }

    override suspend fun updateProfile(profile: EcommerceProfile, countryCode: String): Flow<Unit> {
        return intershopEcommerceDatasource.updateProfile(profile, countryCode)
    }

    override suspend fun getProfileFromServer(countryCode: String): Flow<EcommerceProfile?> {
        return intershopEcommerceDatasource
            .getProfile(countryCode)
            .onEach { serverProfile ->
                Log.d(EcommerceProfileService::class.java.simpleName, "Got eComm Profile from server.")
                saveProfileToCache(serverProfile)
            }
    }

    override suspend fun getProfileFromCache(countryCode: String): Flow<EcommerceProfile?> = flow {
        val profileFromCache =
            cacheService.getUserCacheValueResult<String>(E_COMMERCE_PROFILE_CACHE_KEY)
                .getOrNull()?.fromJsonString<EcommerceProfile>()

        emit(
            profileFromCache?.let {
                if (it.customerNo.isEmpty()) {
                    null
                } else {
                    it
                }
            } ?: run {
                null
            }
        )
    }

    override suspend fun registerProduct(
        productRegistration: EcommerceProductRegistration,
        countryCode: String
    ): Flow<Unit> {
        return intershopEcommerceDatasource.registerProduct(
            countryCode,
            productRegistration
        )
    }

    override suspend fun getProductRegistration(
        email: String,
        customerNo: String?,
        countryCode: String,
        serialNumber: String?,
        registrationId: String?
    ): Flow<EcommerceProductRegistration?> {
        return intershopEcommerceDatasource.getProductRegistration(
            countryCode = countryCode,
            email = email,
            serialNumber = serialNumber,
            registrationId = registrationId,
            customerNo = customerNo
        )
    }

    override suspend fun changeProfileEmailAddress(
        changeEmail: EcommerceChangeEmail,
        countryCode: String
    ): Flow<Unit> {
        return intershopEcommerceDatasource.changeProfileEmailAddress(
            changeEmail,
            countryCode
        )
    }

    private suspend fun saveProfileToCache(profile: EcommerceProfile?) {
        profile?.let {
            Log.d(
                EcommerceProfileService::class.java.simpleName,
                "Saving eComm profile to cache."
            )
            cacheService.setUserCacheValueResult(E_COMMERCE_PROFILE_CACHE_KEY, it.toJsonString()).getOrNull()
        } ?: run {
            Log.d(
                EcommerceProfileService::class.java.simpleName,
                "eComm profile is null. Nothing to save."
            )
        }
    }

    companion object {
        const val E_COMMERCE_PROFILE_CACHE_KEY = "E_COMMERCE_PROFILE_CACHE_KEY"

        fun createInstance(context: Context): EcommerceRepositoryInterface {
            return EcommerceProfileService(
                intershopEcommerceDatasource = IntershopEcommerceDatasource(),
                cacheService = CacheService()
            )
        }
    }
}