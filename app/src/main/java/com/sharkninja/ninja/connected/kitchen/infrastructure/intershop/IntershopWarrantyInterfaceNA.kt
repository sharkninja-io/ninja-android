package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IntershopWarrantyInterfaceNA {
    @POST("warranty/register")
    fun registerProduct(@Body productRegistration: CreateProductRegistration): Call<Void>

    @GET("warranty/search")
    fun getProductRegistrations(@Query("customerId") customerId: String): Call<ProductsRegistration>

    @GET("warranty/search")
    fun getProductRegistrationById(@Query("regId") regId: String): Call<ProductRegistration>
}