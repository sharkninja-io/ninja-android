package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop

import com.sharkninja.grillcore.User
import com.sharkninja.ninja.connected.kitchen.BuildConfig.DEBUG
import com.sharkninja.ninja.connected.kitchen.ext.isDevEnv
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object IntershopRetrofitFactory {

    private var authenticationToken: String? = null

    const val AUTH_TOKEN_HEADER = "authentication-token"
    private const val ACCESS_TOKEN_HEADER = "access-token"
    private const val SUBSCRIPTION_KEY_HEADER = "Ocp-Apim-Subscription-Key"

    fun setIntershopAuthToken(token: String) {
        authenticationToken = token
    }

    fun getRetrofitWithIntershopToken(countryCode: String? = null, currentAccessToken: String? = null, warrantyNA: Boolean = false): Retrofit {
        val endpoint = getInterShopEndpointRegion(countryCode = countryCode, warrantyNA = warrantyNA).endPoint
        val client = OkHttpClient().newBuilder().addInterceptor { chain ->
            val originalRequest = chain.request()
            val accessToken = User.getAccessToken().getOrNull() ?: currentAccessToken ?: ""

            val request: Request.Builder = if (authenticationToken?.isNotEmpty() == true) {
                originalRequest.newBuilder()
                    .header(AUTH_TOKEN_HEADER, authenticationToken!!)
                    .header(ACCESS_TOKEN_HEADER, accessToken)
                    .header(SUBSCRIPTION_KEY_HEADER, endpoint.subscriptionKey)
            } else {
                originalRequest.newBuilder()
                    .header(ACCESS_TOKEN_HEADER, accessToken)
                    .header(SUBSCRIPTION_KEY_HEADER, endpoint.subscriptionKey)
            }
            request.method(originalRequest.method, originalRequest.body)
            chain.proceed(request.build())
        }

        applyHttpLogging(client)

        return Retrofit.Builder()
            .baseUrl(endpoint.baseUrl)
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofitWithAccessToken(countryCode: String): Retrofit {
        val endpoint = getInterShopEndpointRegion(countryCode = countryCode).endPoint
        return getRetrofitWithAccessTokenForBaseUrl(
            baseUrl = endpoint.baseUrl,
            subscriptionKey = endpoint.subscriptionKey
        )
    }

    private fun getRetrofitWithAccessTokenForBaseUrl(
        baseUrl: String,
        subscriptionKey: String,
        requiresAccessToken: Boolean = true
    ): Retrofit {
        val client = OkHttpClient().newBuilder().addInterceptor { chain ->
            val originalRequest = chain.request()

            val request = originalRequest.newBuilder()
                .addHeader(SUBSCRIPTION_KEY_HEADER, subscriptionKey)

            if (requiresAccessToken) {
                val accessToken = User.getAccessToken().getOrNull() ?: ""
                request.addHeader(ACCESS_TOKEN_HEADER, accessToken)
            }

            chain.proceed(request.build())
        }

        applyHttpLogging(client)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getRetrofit(countryCode: String, useDev: Boolean): Retrofit {
        val endpoint = getInterShopEndpointRegion(countryCode = countryCode, useDev = useDev).endPoint
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient().newBuilder().addInterceptor { chain ->
            val originalRequest = chain.request()
            val request = originalRequest.newBuilder().addHeader(
                SUBSCRIPTION_KEY_HEADER, endpoint.subscriptionKey
            )
            chain.proceed(request.build())
        }

        applyHttpLogging(client)

        return Retrofit.Builder()
            .baseUrl(endpoint.baseUrl)
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun applyHttpLogging(requestBuilder: OkHttpClient.Builder) {
        if(DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            requestBuilder.addInterceptor(logging).build()
        }
    }

    private fun getInterShopEndpointRegion(
        countryCode: String? = null,
        useDev: Boolean? = null,
        warrantyNA: Boolean = false
    ): InterShopEndpointRegion {
        val isDev = useDev ?: User.getUsername().getOrNull().isDevEnv()
        return if (warrantyNA) {
            if (isDev) InterShopEndpointRegion.NA_WARRANTY_DEV
            else InterShopEndpointRegion.NA_WARRANTY
        } else {
            when (countryCode) {
                "GB" -> InterShopEndpointRegion.GB
                "US" -> if (isDev) InterShopEndpointRegion.US_DEV
                else InterShopEndpointRegion.US
                "CA" -> if (isDev) InterShopEndpointRegion.CA_DEV
                else InterShopEndpointRegion.CA
                else -> InterShopEndpointRegion.EU
            }
        }
    }
}