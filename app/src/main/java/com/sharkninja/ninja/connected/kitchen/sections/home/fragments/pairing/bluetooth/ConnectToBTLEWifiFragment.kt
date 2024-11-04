package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentConnectToBtleWifiBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth.BTLEWifiConnectionStatus
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val COUNT_DOWN_LENGTH = 30000L
const val COUNT_DOWN_INTERVAL = 1000L

class ConnectToBTLEWifiFragment : Fragment() {

    private lateinit var binding: FragmentConnectToBtleWifiBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private val countDown = object : CountDownTimer(COUNT_DOWN_LENGTH, COUNT_DOWN_INTERVAL) {
        override fun onTick(millisUntilFinished: Long) { }
        override fun onFinish() {
            findNavController().safeNavigate(
                ConnectToBTLEWifiFragmentDirections
                    .actionConnectToBTLEWifiFragmentToConnectToBTLEWifiErrorPersistentFragment()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConnectToBtleWifiBinding.inflate(inflater, container, false)
        initBindings()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.logPairingEvent("onViewCreated: ${ConnectToBTLEWifiFragment::class.java.simpleName}")

        connectAndSubscribe()
        countDown.start()
    }

    override fun onPause() {
        super.onPause()
        homeViewModel.startPassiveScan()
        countDown.cancel()
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

    private fun connectAndSubscribe() {
        (requireActivity() as HomeActivity).hideCloseButton()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            homeViewModel.listenForDeviceToken()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.selectedUserWifiNetwork.value?.let {
                    homeViewModel.sendWifiJoinRequest(it)
                }

                homeViewModel.bluetoothWifiConnectedStatus().collect {
                    when (it) {
                        BTLEWifiConnectionStatus.CONNECTED.ordinal -> onConnectionSuccess()
                        BTLEWifiConnectionStatus.ERROR.ordinal -> {
                            homeViewModel.onConnectGrillToWifiFailure()
                            onConnectionFailure()
                        }
                    }
                }
            }
        }

//        lifecycleScope.launch {
//            homeViewModel.wifiErrorCount.collectLatest { onConnectionFailure() }
//        }
    }

    private fun onConnectionSuccess() {
        homeViewModel.askForNotifications = true
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            homeViewModel.deviceRegistrationSuccess.collect {
                println("Networks: FIRING AYLA SUCCESS FLOW")
                it?.let {
                    if (it) {
                        countDown.cancel()
                        binding.waitingTitle.animate().alpha(0f).setDuration(500)
                            .withEndAction {
                                binding.connectedTitle.animate().alpha(1f).duration = 500
                            }.start()

                        binding.waitingSub.animate().alpha(0f).setDuration(500).start()

                        binding.ellipseWaiting.animate().scaleX(0f).scaleY(0f).setDuration(500)
                            .withEndAction {
                                binding.pairedCheck.animate().scaleX(1f).scaleY(1f).duration = 500
                                binding.goToDashboardButton.animate().alpha(1f).setDuration(500).start()
                                binding.goToDashboardButton.setOnClickListener {
                                    homeViewModel.disconnectDevice()
                                    homeViewModel.wasRedirectedAtLaunch = false
                                    findNavController().safeNavigate(ConnectToBTLEWifiFragmentDirections.globalToHomeFragment())
                                }
                            }.start()
                    }
                }
            }
        }
    }

    private fun onConnectionFailure() {
        when {
            homeViewModel.wifiErrorCount == 1 -> {
                findNavController().safeNavigate(ConnectToBTLEWifiFragmentDirections.actionConnectToBTLEWifiFragmentToConnectToBTLEWifiErrorFirstFragment())
            }
            homeViewModel.wifiErrorCount >= 2 -> {
                findNavController().safeNavigate(ConnectToBTLEWifiFragmentDirections.actionConnectToBTLEWifiFragmentToConnectToBTLEWifiErrorPersistentFragment())
            }
        }
    }
}