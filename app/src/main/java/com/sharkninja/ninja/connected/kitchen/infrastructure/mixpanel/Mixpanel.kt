package com.sharkninja.ninja.connected.kitchen.infrastructure.mixpanel

import android.content.Context
import com.mixpanel.android.mpmetrics.MixpanelAPI
import org.json.JSONObject

object Mixpanel {

    lateinit var instance: MixpanelAPI

    fun init(context: Context) {
        instance = MixpanelAPI.getInstance(context, "70d842f308737d8163375def35ff5da4", JSONObject())
    }
}