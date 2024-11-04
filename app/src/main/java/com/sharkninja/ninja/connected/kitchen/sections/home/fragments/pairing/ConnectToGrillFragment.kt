package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.models.pairing.PhoneToDeviceConnectionStatus
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentConnectToGrillBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConnectToGrillFragment : Fragment() {

    private lateinit var binding: FragmentConnectToGrillBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private var phoneConnectionStatusJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnectToGrillBinding.inflate(inflater, container, false)
        initBindings()

//        mockConnection()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.logPairingEvent("onViewCreated: ${ConnectToGrillFragment::class.java.simpleName}")

        if (homeViewModel.phoneConnectionStatus.value !is PhoneToDeviceConnectionStatus.Connected) {
            homeViewModel.setConnectionStatus(connectionStatus = PhoneToDeviceConnectionStatus.NotStarted)
            connectToNearestGrill()

            lifecycleScope.launchWhenStarted {
                if (homeViewModel.phoneConnectionStatus.value is PhoneToDeviceConnectionStatus.NotStarted) {

                }
            }
        }

        subscribeToVM()
    }

    private fun connectToNearestGrill() {
        lifecycleScope.launch {
            homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: connectToNearestGrill - start")
            homeViewModel.getIoTDeviceWifiNetworks()
                .catch { error ->
                    Log.e(ConnectToGrillFragment::class.java.simpleName, error.message, error)
                    homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: Failed to Get Grill Wifi Network List. Error: ${error.message}")
                    homeViewModel.setConnectionStatus(PhoneToDeviceConnectionStatus.FailedToConnect)
                }
                .collect { iotDeviceNetworkList ->
                    homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: Found ${iotDeviceNetworkList.size}  Grill Networks.")

                    val firstDeviceNetwork = iotDeviceNetworkList.first()
                    homeViewModel.onIoTDeviceWifiNetworkSelected(selection = firstDeviceNetwork)
                    homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: Selected 1st Grill network with SSID: $firstDeviceNetwork")
                    homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: Attempting to connect to Grill network with SSID: $firstDeviceNetwork ...")

                    homeViewModel.connect(iotDeviceWifiNetworkSSID = firstDeviceNetwork)
                        .catch { connectError ->
                            Log.e(ConnectToGrillFragment::class.java.simpleName, connectError.message, connectError)
                            homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: FailedToConnect. Error connecting to Grill SSID: [$firstDeviceNetwork] : ${connectError.message}")
                            homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: connectToNearestGrill - End")
                            homeViewModel.setConnectionStatus(connectionStatus = PhoneToDeviceConnectionStatus.FailedToConnect)
                        }
                        .collect {
                            when (it) {
                                is PhoneToDeviceConnectionStatus.Connected -> {
                                    homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: Successfully Connected to SSID: $firstDeviceNetwork. Gateway IP: ${it.gatewayIpAddress}")
                                }
                                is PhoneToDeviceConnectionStatus.FailedToConnect -> {
                                    homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: Attempted & Failed to Connected to SSID: $firstDeviceNetwork")
                                }
                                is PhoneToDeviceConnectionStatus.NotStarted -> {
                                    homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: Haven't started connecting to SSID: $firstDeviceNetwork")
                                }
                                is PhoneToDeviceConnectionStatus.UserCancelledConnection -> {
                                    homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: User cancelled connecting to SSID: $firstDeviceNetwork")
                                }
                            }
                            homeViewModel.logPairingEvent("${ConnectToGrillFragment::class.java.simpleName}: connectToNearestGrill - End")
                            homeViewModel.setConnectionStatus(it)
                        }
                }
        }
    }

    private fun subscribeToVM() {
        lifecycleScope.launch {
            phoneConnectionStatusJob = launch {
                homeViewModel.phoneConnectionStatus.collectLatest { connectionStatus ->
                    when (connectionStatus) {
                        PhoneToDeviceConnectionStatus.NotStarted -> { }
                        is PhoneToDeviceConnectionStatus.Connected -> { mockConnection() }
                        PhoneToDeviceConnectionStatus.UserCancelledConnection -> {
                            findNavController().navigate(ConnectToGrillFragmentDirections.actionConnectToGrillFragmentToConnectToGrillErrorFragment())
                        }
                        PhoneToDeviceConnectionStatus.FailedToConnect -> showConnectionFailedToConnect()
                    }
                }
            }
        }
    }

    private fun initBindings() {
        animateWaiting()
        binding.progressIndicator.stepThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
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


    private fun showConnectionFailedToConnect() {
        if (homeViewModel.hotspotErrorCount > 0) {
            // needed cause this fires multiple times and we have to auto-navigate
            // according to the requirements. preferably it would be based on user actions
            if (this.isAdded) {
                homeViewModel.onConnectToGrillFailure()
                findNavController().navigate(ConnectToGrillFragmentDirections.actionConnectToGrillFragmentToConnectToGrillNetworkErrorPersistentFragment())
            }
        } else {
            homeViewModel.onConnectToGrillFailure()
            findNavController().navigate(ConnectToGrillFragmentDirections.actionConnectToGrillFragmentToConnectToGrillNetworkErrorFirstFragment())
        }
    }





    private fun mockConnection() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
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
            findNavController().navigate(ConnectToGrillFragmentDirections.actionConnectToGrillFragmentToConnectToWifiFragment())
        }
    }

    override fun onStop() {
        super.onStop()
        phoneConnectionStatusJob?.cancel()
    }
}