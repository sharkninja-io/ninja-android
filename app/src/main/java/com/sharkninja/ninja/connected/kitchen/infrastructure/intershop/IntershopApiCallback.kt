package com.sharkninja.ninja.connected.kitchen.infrastructure.intershop

import android.util.Log
import com.sharkninja.ninja.connected.kitchen.infrastructure.intershop.IntershopRetrofitFactory.AUTH_TOKEN_HEADER
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// This class's purpose is to check every intershop response for a new token and save it.
open class IntershopApiCallback<T> : Callback<T> {

    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e(TAG, call.toString() + t.localizedMessage)
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val authToken = response.headers()[AUTH_TOKEN_HEADER]
        authToken?.let { token ->
            IntershopRetrofitFactory.setIntershopAuthToken(token)
        }
    }

    companion object {
        val TAG = IntershopApiCallback::class.simpleName
    }
}
