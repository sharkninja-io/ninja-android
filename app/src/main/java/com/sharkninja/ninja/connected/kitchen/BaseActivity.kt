package com.sharkninja.ninja.connected.kitchen

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.sharkninja.ninja.connected.kitchen.infrastructure.logging.NinjaBreadCrumbLogger


open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val configuration: Configuration = newBase.resources.configuration
        val displayMetrics = newBase.resources.displayMetrics
        if (displayMetrics.densityDpi != DisplayMetrics.DENSITY_DEVICE_STABLE) {
            // Current density is different from Default Density. Override it
            configuration.densityDpi = DisplayMetrics.DENSITY_DEVICE_STABLE
        }
        configuration.fontScale = FONT_SCALE
        val newContext = newBase.createConfigurationContext(configuration)
        super.attachBaseContext(newContext)
    }

    override fun onStart() {
        super.onStart()
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            NinjaBreadCrumbLogger(),
            true
        )
    }

    companion object {
        private const val FONT_SCALE = 1.0f
    }
}