package com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce

import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceChangeEmail
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProfile
import kotlinx.coroutines.flow.Flow


interface EcommerceDatasource {

    suspend fun getProfile(countryCode: String, email: String = ""): Flow<EcommerceProfile?>

    suspend fun createProfile(email: String, countryCode: String, useDev: Boolean): Flow<Unit>

    suspend fun updateProfile(updatedProfile: EcommerceProfile, countryCode: String): Flow<Unit>

    suspend fun changeProfileEmailAddress(changeEmail: EcommerceChangeEmail, countryCode: String): Flow<Unit>

    suspend fun getProductRegistration(
        countryCode: String,
        email: String,
        serialNumber: String? = null,
        registrationId: String? = null,
        customerNo: String? = null
    ): Flow<EcommerceProductRegistration?>

    suspend fun registerProduct(countryCode: String, productRegistration: EcommerceProductRegistration): Flow<Unit>
}