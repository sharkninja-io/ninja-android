package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.datasource

import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.Address
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.Customer
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopChangeEmail
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.ProductsRegistration
import kotlinx.coroutines.flow.Flow

interface IntershopDatasource {
    suspend fun getProfile(countryCode: String): Flow<Customer?>

    suspend fun createProfile(email: String, countryCode: String, useDev: Boolean): Flow<Unit>

    suspend fun updateProfile(customer: Customer, countryCode: String): Flow<Unit>

    suspend fun changeProfileEmailAddress(updateEmail: IntershopChangeEmail, countryCode: String): Flow<Unit>

    suspend fun updateProfileAddress(newAddress: Address, countryCode: String): Flow<Unit>

    suspend fun getProductRegistration(
        countryCode: String,
        registrationId: String?,
        customerNo: String?
    ): Flow<ProductsRegistration?>

    suspend fun registerProduct(countryCode: String, productRegistration: EcommerceProductRegistration): Flow<Unit>
}