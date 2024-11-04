package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.datasource

import com.sharkninja.ninja.connected.kitchen.ext.getOrEmpty
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.Address
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.CreateProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.Customer
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopApiCallback
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopChangeEmail
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopLogin
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopRetrofitFactory
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopServiceInterfaceNA
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopWarrantyInterfaceNA
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.ProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.ProductsRegistration
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Response

private const val MILLIS_PER_SECOND = 1000

class IntershopNorthAmericaDataSource:
    IntershopDatasource {

    override suspend fun getProfile(countryCode: String): Flow<Customer?> = callbackFlow {
        IntershopRetrofitFactory.getRetrofitWithAccessToken(countryCode)
            .create(IntershopServiceInterfaceNA::class.java)
            .getCustomerProfile().enqueue(object : IntershopApiCallback<Customer>() {
                override fun onResponse(call: Call<Customer>, response: Response<Customer>) {
                    super.onResponse(call, response)
                    trySend(response.body())
                }

                override fun onFailure(call: Call<Customer>, t: Throwable) {
                    super.onFailure(call, t)
                    trySend(null)
                }
            })
        awaitClose()
    }

    override suspend fun createProfile(email: String, countryCode: String, useDev: Boolean): Flow<Unit> = callbackFlow {
        IntershopRetrofitFactory.getRetrofit(countryCode, useDev = useDev).create(IntershopServiceInterfaceNA::class.java)
            .createCustomerProfile(IntershopLogin(email))
            .enqueue(object : IntershopApiCallback<Void>() {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    super.onResponse(call, response)
                    trySend(Unit)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    super.onFailure(call, t)
                    cancel(
                        message = "Intershop NA sign up failed",
                        cause = t
                    )
                }
            })
        awaitClose()
    }

    override suspend fun updateProfile(customer: Customer, countryCode: String): Flow<Unit> = callbackFlow {
        IntershopRetrofitFactory.getRetrofitWithAccessToken(countryCode)
            .create(IntershopServiceInterfaceNA::class.java)
            .updateCustomerProfile(customer)
            .enqueue(object : IntershopApiCallback<Void>() {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    super.onResponse(call, response)
                    trySend(Unit)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    super.onFailure(call, t)
                    trySend(Unit)
                }
            })


        awaitClose()
    }

    override suspend fun changeProfileEmailAddress(updateEmail: IntershopChangeEmail, countryCode: String): Flow<Unit> = callbackFlow {
        IntershopRetrofitFactory.getRetrofitWithIntershopToken(countryCode)
            .create(IntershopServiceInterfaceNA::class.java)
            .updateCustomerEmail(updateEmail)
            .enqueue(object : IntershopApiCallback<Void>() {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    super.onResponse(call, response)
                    if (response.isSuccessful) {
                        trySend(Unit)
                    } else {
                        cancel(
                            message = "Failed to change profile email address for NA.",
                            cause = Throwable("Email address change failed: ${response.message()}")
                        )
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    super.onFailure(call, t)
                    cancel(
                        message = "Failed to change profile email address for NA.",
                        cause = t
                    )
                }
            })

        awaitClose()
    }

    override suspend fun updateProfileAddress(
        newAddress: Address,
        countryCode: String
    ): Flow<Unit> = callbackFlow {
        IntershopRetrofitFactory.getRetrofitWithAccessToken(countryCode)
            .create(IntershopServiceInterfaceNA::class.java)
            .updateCustomerAddress(newAddress.id, newAddress)
            .enqueue(object : IntershopApiCallback<Void>() {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    super.onResponse(call, response)
                    trySend(Unit)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    super.onFailure(call, t)
                    cancel(
                        message = "Error updating the profile address in Intershop NA",
                        cause = t
                    )
                }
            })

        awaitClose()
    }

    override suspend fun getProductRegistration(
        countryCode: String,
        registrationId: String?,
        customerNo: String?
    ): Flow<ProductsRegistration?> = callbackFlow {
        getWarrantyServiceApi()
            .getProductRegistrations(customerNo.getOrEmpty())
            .enqueue(object : IntershopApiCallback<ProductsRegistration>() {
                override fun onResponse(call: Call<ProductsRegistration>, response: Response<ProductsRegistration>) {
                    super.onResponse(call, response)
                    trySend(response.body())
                }

                override fun onFailure(call: Call<ProductsRegistration>, t: Throwable) {
                    super.onFailure(call, t)
                    trySend(null)
                }
            })

        awaitClose()
    }

    override suspend fun registerProduct(
        countryCode: String,
        productRegistration: EcommerceProductRegistration
    ): Flow<Unit> = callbackFlow {

        getWarrantyServiceApi()
            .registerProduct(createProductRegistration(productRegistration))
            .enqueue(object : IntershopApiCallback<Void>() {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    super.onResponse(call, response)
                    if(response.isSuccessful) {
                        trySend(Unit)
                    } else {
                        cancel(
                            message = "Failed to register product on Intershop NA",
                            cause = Throwable("Product Registration Failed: ${response.message()}")
                        )
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    super.onFailure(call, t)
                    cancel(
                        message = "Failed to register product on Intershop NA",
                        cause = t
                    )
                }
            })

        awaitClose()
    }

    private fun getProductRegistrationById(regId: String): Flow<ProductRegistration?> = callbackFlow {
        getWarrantyServiceApi().getProductRegistrationById(regId)
            .enqueue(object : IntershopApiCallback<ProductRegistration>() {
                override fun onResponse(call: Call<ProductRegistration>, response: Response<ProductRegistration>) {
                    super.onResponse(call, response)
                    trySend(response.body())
                }

                override fun onFailure(call: Call<ProductRegistration>, t: Throwable) {
                    super.onFailure(call, t)
                    cancel(
                        message = "Failed to get product registration for regId: $regId on Intershop NA",
                        cause = t
                    )
                }
            })
        awaitClose()
    }

    private fun getWarrantyServiceApi(): IntershopWarrantyInterfaceNA {
        return IntershopRetrofitFactory.getRetrofitWithIntershopToken(warrantyNA = true)
            .create(IntershopWarrantyInterfaceNA::class.java)
    }

    private fun createProductRegistration(eCommRegistration: EcommerceProductRegistration): CreateProductRegistration {
        return CreateProductRegistration(
            firstName = eCommRegistration.firstName,
            lastName = eCommRegistration.lastName,
            email = eCommRegistration.email,
            purchaseSourceName = eCommRegistration.purchaseLocationName,
            serialNumber = eCommRegistration.serialNumber,
            customerId = eCommRegistration.customerId,
            customerLocale = eCommRegistration.customerLocale,
            recordSource = "ICM",
            regCountry = eCommRegistration.country,
            warrantyEffectiveDate = eCommRegistration.purchaseDate?.time?.div(MILLIS_PER_SECOND),
            productSku = eCommRegistration.productSku
        )
    }
}