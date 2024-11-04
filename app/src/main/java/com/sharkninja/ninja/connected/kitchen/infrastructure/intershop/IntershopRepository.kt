package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop

import android.util.Log
import com.sharkninja.ninja.connected.kitchen.data.enums.toRegionCode
import com.sharkninja.ninja.connected.kitchen.data.models.CountryRegionSupport
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.datasource.IntershopDatasource
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.datasource.IntershopEuropeDataSource
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.datasource.IntershopNorthAmericaDataSource
import kotlinx.coroutines.flow.Flow

class IntershopRepository {

    suspend fun getProfile(countryCode: String): Flow<Customer?> {
        return getIntershopDataSource(countryCode).getProfile(countryCode)
    }

    suspend fun createProfile(email: String, countryCode: String, useDev: Boolean): Flow<Unit> {
        return getIntershopDataSource(countryCode).createProfile(email, countryCode, useDev)
    }

    suspend fun updateProfile(customer: Customer, countryCode: String): Flow<Unit> {
        return getIntershopDataSource(countryCode).updateProfile(customer, countryCode)
    }

    suspend fun changeProfileEmailAddress(
        email: String,
        customerNo: String?,
        countryCode: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Flow<Unit> {
        return getIntershopDataSource(countryCode).changeProfileEmailAddress(
            IntershopChangeEmail(
                email = email,
                customerNo = customerNo,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber
            ),
            countryCode
        )
    }

    suspend fun updateProfileAddress(
        newAddress: Address,
        countryCode: String
    ): Flow<Unit> {
        return getIntershopDataSource(countryCode).updateProfileAddress(newAddress, countryCode)
    }

    suspend fun getProductRegistration(
        countryCode: String,
        registrationId: String?,
        customerNo: String?
    ): Flow<ProductsRegistration?> {
        return getIntershopDataSource(countryCode).getProductRegistration(
            countryCode = countryCode,
            registrationId = registrationId,
            customerNo = customerNo
        )
    }

    suspend fun registerProduct(
        countryCode: String,
        productRegistration: EcommerceProductRegistration
    ): Flow<Unit> {
        return getIntershopDataSource(countryCode).registerProduct(countryCode, productRegistration)
    }

    private fun getIntershopDataSource(countryCode: String): IntershopDatasource {
        val countrySupport = countryCode.toRegionCode().item
        return if (countrySupport.server == CountryRegionSupport.CountryRegionServer.NorthAmerica) {
            Log.d(IntershopRepository::class.java.simpleName, "Intershop datasource: NA")
            IntershopNorthAmericaDataSource()
        } else {
            Log.d(IntershopRepository::class.java.simpleName, "Intershop datasource: EU")
            IntershopEuropeDataSource()
        }
    }
}