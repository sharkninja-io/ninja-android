package com.sharkninja.ninja.connected.kitchen.sections.home.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sharkninja.cloudcore.WifiNetwork
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentBluetoothBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BluetoothFragment : Fragment() {

    private lateinit var binding: FragmentBluetoothBinding

    private val homeViewModel: HomeViewModel by activityViewModels()

    private val btRequestForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        println("Goodie!")
    }

    private val requestScanPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
        if (isGranted) homeViewModel.startPassiveScan() else println("User said NO!")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBluetoothBinding.inflate(inflater, container, false)
        initBt()
        initBindings()
        return binding.root
    }

    private fun initBt() {
        if (homeViewModel.getBTAdapter()?.isEnabled == false) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            btRequestForResult.launch(enableIntent)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                println("BEGIN SUBSCRIPTION")
                homeViewModel.eventFlow().collect {
//                    println("stuff - subscription #2: $it")
                }
            }
        }
    }

    private fun initBindings() {
        binding.scanDevicesButton.setOnClickListener {
            when {
                context?.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PERMISSION_GRANTED &&
                        context?.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) ==
                        PERMISSION_GRANTED -> homeViewModel.startPassiveScan()
                shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN) -> {
                    println("what do we say?")
                    requestScanPermission.launch(Manifest.permission.BLUETOOTH_SCAN)
                    requestScanPermission.launch(Manifest.permission.BLUETOOTH_CONNECT)
                }
                else -> {
                    requestScanPermission.launch(Manifest.permission.BLUETOOTH_SCAN)
                    requestScanPermission.launch(Manifest.permission.BLUETOOTH_CONNECT)
                }
            }
        }

        binding.enableNotifyCharacteristicButton.setOnClickListener { homeViewModel.enableWifiResultNotification() }
        binding.enableNotifyDescriptorButton.setOnClickListener { homeViewModel.enableWifiResultDescriptor() }
        binding.sendWifiScanRequestButton.setOnClickListener { homeViewModel.sendScanRequestCommand() }
        binding.sendWifiScanResultButton.setOnClickListener { homeViewModel.sendScanResultCommand() }
        binding.readWifiScanResultButton.setOnClickListener { homeViewModel.readWifiScanResult() }
        binding.sendWifiJoinRequestButton.setOnClickListener {
            val wifiNetwork = WifiNetwork(5, "", 0, "Y", 30, "Test", "Unknown", "")
            homeViewModel.sendWifiJoinRequest(wifiNetwork)
        }
        binding.disconnectDeviceButton.setOnClickListener { homeViewModel.disconnectDevice() }
        binding.toWifiSelectionButton.setOnClickListener { findNavController().safeNavigate(BluetoothFragmentDirections.actionBluetoothFragmentToSelectBTLEWifiFragment2()) }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
