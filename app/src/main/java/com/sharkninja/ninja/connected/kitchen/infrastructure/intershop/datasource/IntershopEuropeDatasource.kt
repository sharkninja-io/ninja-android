package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.datasource

import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProductRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.Address
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.Customer
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopApiCallback
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopChangeEmail
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopLogin
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopRetrofitFactory
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopServiceInterfaceEU
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.ProductToRegistration
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.ProductsRegistration
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Response

class IntershopEuropeDataSource: IntershopDatasource {

    override suspend fun getProfile(countryCode: String): Flow<Customer?> = callbackFlow {
        IntershopRetrofitFactory.getRetrofitWithAccessToken(countryCode)
            .create(IntershopServiceInterfaceEU::class.java)
            .getCustomerInfo().enqueue(object : IntershopApiCallback<Customer>() {
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
        IntershopRetrofitFactory.getRetrofit(countryCode, useDev)
            .create(IntershopServiceInterfaceEU::class.java)
            .createCustomer(IntershopLogin(email))
            .enqueue(object : IntershopApiCallback<Void>() {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    super.onResponse(call, response)
                    trySend(Unit)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    super.onFailure(call, t)
                    cancel(
                        message = "Intershop EU sign up failed",
                        cause = t
                    )
                }
            })

        awaitClose()
    }

    override suspend fun updateProfile(customer: Customer, countryCode: String): Flow<Unit> = callbackFlow {
        IntershopRetrofitFactory.getRetrofitWithAccessToken(countryCode)
            .create(IntershopServiceInterfaceEU::class.java)
            .updateCustomer(customer)
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
            .create(IntershopServiceInterfaceEU::class.java)
            .changeEmail(updateEmail)
            .enqueue(
                object : IntershopApiCallback<IntershopChangeEmail>() {
                    override fun onResponse(call: Call<IntershopChangeEmail>, response: Response<IntershopChangeEmail>) {
                        super.onResponse(call, response)
                        if (response.isSuccessful) {
                            trySend(Unit)
                        } else {
                            cancel(
                                message = "Failed to change profile email address for EU.",
                                cause = Throwable("Email address change failed: ${response.message()}")
                            )
                        }
                    }

                    override fun onFailure(call: Call<IntershopChangeEmail>, t: Throwable) {
                        super.onFailure(call, t)
                        cancel(
                            message = "Failed to change profile email address for EU.",
                            cause = t
                        )
                    }
                }
            )

        awaitClose()
    }

    override suspend fun updateProfileAddress(
        newAddress: Address,
        countryCode: String
    ): Flow<Unit> = callbackFlow {
        IntershopRetrofitFactory.getRetrofitWithAccessToken(countryCode)
            .create(IntershopServiceInterfaceEU::class.java)
            .updateAddress(newAddress.id, newAddress)
            .enqueue(object : IntershopApiCallback<Void>() {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    super.onResponse(call, response)
                    trySend(Unit)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    super.onFailure(call, t)
                    cancel(
                        message = "Error updating the profile address in Intershop EU",
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
        IntershopRetrofitFactory.getRetrofitWithAccessToken(countryCode)
            .create(IntershopServiceInterfaceEU::class.java)
            .getProductRegistration()
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
        IntershopRetrofitFactory.getRetrofitWithAccessToken(countryCode)
            .create(IntershopServiceInterfaceEU::class.java)
            .registerProduct(createProductRegistration(productRegistration))
            .enqueue(object : IntershopApiCallback<Void>() {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    super.onResponse(call, response)
                    if (response.isSuccessful) {
                        trySend(Unit)
                    } else {
                        cancel(
                            message = "Failed to register product on Intershop EU",
                            cause = Throwable(response.message())
                        )
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    super.onFailure(call, t)
                    cancel(
                        message = "Failed to register product on Intershop EU",
                        cause = t
                    )
                }
            })
        awaitClose()
    }

    private fun createProductRegistration(productRegistration: EcommerceProductRegistration): ProductToRegistration {
        return ProductToRegistration(
            firstName = productRegistration.firstName,
            lastName = productRegistration.lastName,
            postalCode = productRegistration.postalCode,
            email = productRegistration.email,
            productName = productRegistration.productName,
            purchaseDate = productRegistration.purchaseDate?.time.toString(),
            sellingLocationName = productRegistration.purchaseLocationName,
            sellingLocation = productRegistration.purchaseLocation,
            serialNumber = productRegistration.serialNumber,
            competitions = null,
            receiveOffers = productRegistration.offers,
            customerId = productRegistration.customerId,
            productId = productRegistration.productSku
        )
    }
}