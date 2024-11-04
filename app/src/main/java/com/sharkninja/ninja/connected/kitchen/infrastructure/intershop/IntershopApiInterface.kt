package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop

import retrofit2.Call
import retrofit2.http.*

interface IntershopApiInterface {
    @POST("-/customers/basic")
    fun createCustomer(@Body login: IntershopLogin): Call<Void>

    @POST("-/productregistrations/-")
    fun registerProduct(@Body productRegistration: ProductToRegistration): Call<Void>

    @GET("-/productregistrations")
    fun getProductRegistration(): Call<ProductsRegistration>

    @GET("-/metadata/productregistration")
    fun getProductIdProductRegistration(): Call<SharkProductTypes>

    @GET("-/customers/-")
    fun getCustomerInfo(): Call<Customer>

    @PUT("-/customers/-")
    fun changeEmail(@Body emailParams: IntershopChangeEmail): Call<IntershopChangeEmail>

    @PUT("-/customers/-")
    fun updateCustomer(@Body customer: Customer): Call<Void>

    @PUT("-/customers/-/addresses/{addressId}")
    fun updateAddress(@Path("addressId") addressId: String?, @Body address: Address): Call<Void>
}

