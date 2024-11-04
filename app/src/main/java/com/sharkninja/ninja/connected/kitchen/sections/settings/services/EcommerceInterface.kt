package com.sharkninja.ninja.connected.kitchen.sections.settings.services

import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProfile
import kotlinx.coroutines.flow.Flow

interface EcommerceInterface {
    suspend fun getEcommerceProfile(): Flow<EcommerceProfile?>

    suspend fun updateEcommerceProfile(profile: EcommerceProfile, countryCode: String): Flow<Unit>
    suspend fun changeEcommerceProfileEmailAddress(
        currentEmail: String? = null,
        newEmail: String,
        customerNo: String? = null,
        countryCode: String,
        firstName: String = "",
        lastName: String = "",
        phoneNumber: String = ""
    ): Flow<Unit>

    suspend fun getEcommerceProductRegistration(
        email: String,
        customerNo: String?,
        countryCode: String,
        serialNumber: String? = null,
        registrationId: String? = null
    ): Flow<EcommerceProductRegistration?>

    suspend fun registerProductWithEcommerce(
        countryCode: String,
        eCommRegistration: EcommerceProductRegistration
    ): Flow<Unit>
}