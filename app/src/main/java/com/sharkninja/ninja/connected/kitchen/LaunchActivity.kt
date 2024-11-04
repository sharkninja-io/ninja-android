package com.sharkninja.ninja.connected.kitchen

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sharkninja.ninja.connected.kitchen.databinding.ActivityLaunchBinding
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.EcommerceProfileService
import com.sharkninja.ninja.connected.kitchen.sections.authentication.activities.AuthenticationActivity
import com.sharkninja.ninja.connected.kitchen.sections.authentication.activities.AuthenticationDeepLinkHelper
import com.sharkninja.ninja.connected.kitchen.sections.authentication.services.AuthenticationService
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModel
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModelFactory
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LaunchActivity : BaseActivity(), TextureView.SurfaceTextureListener,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private lateinit var authenticationViewModel: AuthenticationViewModel
    private lateinit var binding: ActivityLaunchBinding
    private lateinit var deepLinkHelper: AuthenticationDeepLinkHelper

    private val intentFlags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    private val authIntent by lazy {
        Intent(applicationContext, AuthenticationActivity::class.java)
            .apply {
                flags = intentFlags
            }
    }

    private val homeIntent by lazy {
        Intent(applicationContext, HomeActivity::class.java)
            .apply {
                flags = intentFlags
            }
    }

    private var isLoggedIn = false
    private val mp = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBar()

        authenticationViewModel =
            ViewModelProvider(
                this, AuthenticationViewModelFactory(
                    AuthenticationService(
                        CacheService(),
                        EcommerceProfileService.createInstance(applicationContext)
                    )
                )
            ).get(AuthenticationViewModel::class.java)

        observeViewModel()
        binding.splashVideo.surfaceTextureListener = this
        mp.setOnPreparedListener(this)
        mp.setOnCompletionListener(this)
    }

    private fun setStatusBar() {
        window.statusBarColor = resources.getColor(R.color.deep_black)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {

            authenticationViewModel.getRegionSelection().catch {
                deepLinkHelper = AuthenticationDeepLinkHelper(intent, applicationContext, "null")
            }.onEach {
                deepLinkHelper = AuthenticationDeepLinkHelper(intent, applicationContext, it.toString())
//                authenticationViewModel.setRegion(it.toString())

            }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

            launch {
                authenticationViewModel.logInReturningUserIfPossible()
                    .catch {
                        // failed to auto-sign in
                        // startActivity(authIntent)
                    }
                    .collect { isLoggedIn = it }
            }
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        val sf = Surface(binding.splashVideo.surfaceTexture)
        mp.apply {
            setDataSource(applicationContext,
                Uri.parse("android.resource://$packageName/raw/splash_smoke"))
            setSurface(sf)
            prepareAsync()
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) { }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean { return true }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) { }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        ValueAnimator.ofFloat(1f, 0f).apply {
            interpolator = LinearInterpolator()
            duration = 500
            addUpdateListener {
                val progress = it.animatedValue as Float
                binding.splashVideo.alpha = progress
                binding.activitySplashLogo.alpha = progress
            }
            doOnEnd {
                if(deepLinkHelper.isAppStartedFromAppLink()) {
                    navigateToDeepLink()
                } else {
                    if (isLoggedIn) startActivity(homeIntent) else startActivity(authIntent)
                }
            }
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mp.release()
        binding.splashVideo.surfaceTexture?.release()
    }

    private fun navigateToDeepLink() {
        when {
            deepLinkHelper.isConfirmAccountEmailAppLink() -> {
                Log.i("Deep Link", "launch activity: reset email ")
                startActivity(deepLinkHelper.getConfirmAccountEmailIntent())
            }
            deepLinkHelper.isPasswordResetAppLink() -> {
                Log.i("Deep Link", "launch activity: reset password ")
                startActivity(deepLinkHelper.getPasswordResetIntent())
            }
            else -> {}
        }
    }
}