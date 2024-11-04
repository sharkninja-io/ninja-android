package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.models.Appliance
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentBluetoothInternationalConnectedBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.adapters.ApplianceListAdapter

class BluetoothInternationalGrillDetectedFragment : Fragment() {
    private lateinit var binding: FragmentBluetoothInternationalConnectedBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private val appliances = mutableListOf<Appliance>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBluetoothInternationalConnectedBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.buttonNext.isEnabled = false
        initDeviceList()

        binding.buttonNext.setOnClickListener {
            homeViewModel.stopPassiveScan()
            val directions =
                BluetoothInternationalGrillDetectedFragmentDirections.actionBluetoothInternationalGrillDetectedFragmentToBluetoothInternationalPairingFragment()
            findNavController().safeNavigate(directions)
        }
    }

    private fun initDeviceList() {
        homeViewModel.deviceMACAddresses.distinct().forEach {
            it?.let {
                appliances.add(
                    Appliance(
                        name = resources.getString(R.string.appliance_name),
                        macAddress = it)
                )
            }
        }
        val adapter = ApplianceListAdapter(appliances, ::onListItemClick)
        binding.foundDevicesView.layoutManager = LinearLayoutManager(requireContext())
        binding.foundDevicesView.adapter = adapter
    }

    private fun onListItemClick(appliance: Appliance) {
        homeViewModel.selectedDevice = appliance.macAddress
        binding.buttonNext.isEnabled = true
    }
}