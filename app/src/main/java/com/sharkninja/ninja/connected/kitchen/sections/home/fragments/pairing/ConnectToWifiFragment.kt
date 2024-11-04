package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentConnectToWifiBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConnectToWifiFragment : Fragment() {

    private lateinit var binding: FragmentConnectToWifiBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConnectToWifiBinding.inflate(inflater, container, false)
        initBindings()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.logPairingEvent("onViewCreated: ${ConnectToWifiFragment::class.java.simpleName}")

        subscribeToVM()
    }

    private fun initBindings() {
        animateWaiting()
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

    private fun subscribeToVM() {
        lifecycleScope.launch {
            homeViewModel.sendWifiNetworkToIoTDevice()
            homeViewModel.logPairingEvent("${ConnectToWifiFragment::class.java.simpleName}: Waiting for grill to come online...")
            homeViewModel.newGrillDSN.collectLatest { if (it.isNotEmpty()) onConnectionSuccess() }
        }

//        lifecycleScope.launch {
//            homeViewModel.wifiErrorCount.collectLatest { onConnectionFailure() }
//        }
    }

    private fun mockConnection() {
        lifecycleScope.launch {
            delay(3000)

            binding.waitingTitle.animate().alpha(0f).setDuration(500)
                .withEndAction {
                    binding.connectedTitle.animate().alpha(1f).duration = 500
                }.start()

            binding.waitingSub.animate().alpha(0f).setDuration(500).start()

            binding.ellipseWaiting.animate().scaleX(0f).scaleY(0f).setDuration(500)
                .withEndAction {
                    binding.pairedCheck.animate().scaleX(1f).scaleY(1f).duration = 500
                }.start()

            delay(3000)
            findNavController().navigate(ConnectToWifiFragmentDirections.actionConnectToWifiFragmentToNameYourGrillFragment())
        }
    }

    private fun onConnectionSuccess() {
        mockConnection()
    }

    private fun onConnectionFailure() {
        when {
            homeViewModel.wifiErrorCount == 1 -> {
                findNavController().navigate(ConnectToWifiFragmentDirections.actionConnectToWifiFragmentToConnectToWifiErrorFirstFragment())
            }
            homeViewModel.wifiErrorCount >= 2 -> {
                findNavController().navigate(ConnectToWifiFragmentDirections.actionConnectToWifiFragmentToConnectToWifiErrorPersistentFragment())
            }
        }
    }
}