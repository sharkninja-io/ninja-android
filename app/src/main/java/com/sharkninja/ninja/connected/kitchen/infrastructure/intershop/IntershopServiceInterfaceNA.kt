package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IntershopServiceInterfaceNA {
    @POST("customers/basic")
    fun createCustomerProfile(@Body login: IntershopLogin): Call<Void>

    @GET("customers/-")
    fun getCustomerProfile(): Call<Customer>

    @PUT("customers/-")
    fun updateCustomerProfile(@Body customer: Customer): Call<Void>

    @PUT("customers/-")
    fun updateCustomerEmail(@Body updateEmail: IntershopChangeEmail): Call<Void>

    @PUT("customers/-/addresses/{addressId}")
    fun updateCustomerAddress(
        @Path("addressId") addressId: String?,
        @Body address: Address
    ): Call<Void>
}