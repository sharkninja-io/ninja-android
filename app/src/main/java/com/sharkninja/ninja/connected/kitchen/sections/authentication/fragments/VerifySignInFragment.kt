package com.sharkninja.ninja.connected.kitchen.sections.authentication.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentVerifySignInBinding
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModel
import kotlinx.coroutines.delay

class VerifySignInFragment : Fragment() {

    private lateinit var binding: FragmentVerifySignInBinding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifySignInBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        animateWaiting()
        attemptLogin()
    }

    private fun attemptLogin() {
        lifecycleScope.launchWhenStarted {
            authenticationViewModel.login()
                .onSuccess {
                    animateMessage()
                    delay(2000)
                    authenticationViewModel.checkEcommerceAccountCreation()
                }
                .onFailure {
                    println("NO CREDENTIALS - FAILED!")
                    findNavController().navigate(VerifySignInFragmentDirections.actionVerifySignInFragmentToSignInFragment())
                }
        }
    }

    private fun animateWaiting() {
        ValueAnimator.ofFloat(0f, 359f).apply {
            interpolator = LinearInterpolator()
            duration = 1000
            repeatCount = Animation.INFINITE
            addUpdateListener {
                val progress = it.animatedValue as Float
                binding.ellipseWaiting.rotation = progress
            }
            start()
        }
    }

    private fun animateMessage() {
        binding.verifyingHeader.animate().alpha(0f).setDuration(500).withEndAction {
            binding.verifiedHeader.animate().alpha(1f).duration = 500
        }.start()

        binding.ellipseWaiting.animate().scaleX(0f).scaleY(0f).setDuration(500)
            .withEndAction {
                binding.verifiedCheck.animate().scaleX(1f).scaleY(1f).duration = 500
            }.start()
    }
}