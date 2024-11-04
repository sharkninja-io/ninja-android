package com.sharkninja.ninja.connected.kitchen

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.bugsnag.android.Bugsnag
import com.bugsnag.android.Configuration
import com.sharkninja.grillcore.GrillCoreSDK
import com.sharkninja.ninja.connected.kitchen.BuildConfig.DEBUG
import com.sharkninja.ninja.connected.kitchen.infrastructure.mixpanel.Mixpanel
import com.testfairy.TestFairy

const val TEST_FAIRY_APP_TOKEN = "SDK-BwSYgGjs"

class NinjaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initBugSnag()
        initGrillCore()
        Mixpanel.init(applicationContext)
        if(DEBUG) {
            TestFairy.begin(this, TEST_FAIRY_APP_TOKEN)
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initGrillCore() {
        GrillCoreSDK.initialize(applicationContext)
    }

    private fun initBugSnag() {
        val bugsnagConfig = Configuration.load(this)
        bugsnagConfig.releaseStage = BuildConfig.BUG_SNAG_RELEASE_STAGE
        Bugsnag.start( this, bugsnagConfig)
    }
}