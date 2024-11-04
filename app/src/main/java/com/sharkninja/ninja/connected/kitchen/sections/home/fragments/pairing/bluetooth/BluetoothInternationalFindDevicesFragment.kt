package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentBluetoothInternationalFindDevicesBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.launch

class BluetoothInternationalFindDevicesFragment: Fragment() {
    private lateinit var binding: FragmentBluetoothInternationalFindDevicesBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBluetoothInternationalFindDevicesBinding.inflate(inflater, container, false)
//        homeViewModel.logPairingEvent("onViewCreated: ${BluetoothPairingFragment::class.java.simpleName}")
        initBindings()
        subcribeToEvents()
        return binding.root
    }

    private fun initBindings() {
        binding.progressIndicator.stepFour.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
    }

    private fun subcribeToEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.startPassiveScan()
                    homeViewModel.fetchFoundDevice().collect {
                        it?.let {
                            it.forEach { grill ->
                                homeViewModel.deviceMACAddresses.add(grill.uuid)
                            }
                            if (homeViewModel.deviceMACAddresses.isEmpty()) {
                                homeViewModel.onConnectToGrillFailure()
                                onConnectionFailure()
                            } else {
                                showConnectionSuccess()
                            }
                        }
                    }
                }

                launch {
                    searchForNinjaProducts()
                }
            }
        }
    }

    private fun searchForNinjaProducts() {
        homeViewModel.checkForJoinableGrills()
    }

    private fun showConnectionSuccess() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            findNavController().navigate(BluetoothInternationalFindDevicesFragmentDirections.actionBluetoothInternationalFindDevicesFragmentToBluetoothInternationalGrillDetectedFragment())
        }
    }

    private fun onConnectionFailure() {
        findNavController().safeNavigate(BluetoothInternationalFindDevicesFragmentDirections.actionBluetoothInternationalFindDevicesFragmentToConnectToBluetoothInternationalErrorFirstFragment())

//        NCMA-1233, removed the option for a persistent error and moving to wifi only pairing.  Bluetooth or bust now
//        when {
//            homeViewModel.hotspotErrorCount == 1 -> {
//                findNavController().safeNavigate(BluetoothFindDevicesFragmentDirections.actionBluetoothFindDevicesFragmentToConnectToBluetoothErrorFirstFragment())
//            }
//            homeViewModel.hotspotErrorCount >= 2 -> {
//                findNavController().safeNavigate(BluetoothFindDevicesFragmentDirections.actionBluetoothFindDevicesFragmentToBluetoothPersistentErrorFragment())
//            }
//        }
    }
}