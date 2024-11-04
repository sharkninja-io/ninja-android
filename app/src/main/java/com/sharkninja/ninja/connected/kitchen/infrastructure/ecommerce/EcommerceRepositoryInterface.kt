package com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce

import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceChangeEmail
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProfile
import kotlinx.coroutines.flow.Flow

interface EcommerceRepositoryInterface {

    suspend fun createProfile(profile: EcommerceProfile, countryCode: String, useDev: Boolean): Flow<Unit>
    suspend fun updateProfile(profile: EcommerceProfile, countryCode: String): Flow<Unit>
    suspend fun getProfileFromServer(countryCode: String): Flow<EcommerceProfile?>

    suspend fun getProfileFromCache(countryCode: String): Flow<EcommerceProfile?>

    suspend fun registerProduct(productRegistration: EcommerceProductRegistration, countryCode: String): Flow<Unit>
    suspend fun getProductRegistration(
        email: String,
        customerNo: String?,
        countryCode: String,
        serialNumber: String? = null,
        registrationId: String? = null
    ): Flow<EcommerceProductRegistration?>
    suspend fun changeProfileEmailAddress(changeEmail: EcommerceChangeEmail, countryCode: String): Flow<Unit>
}