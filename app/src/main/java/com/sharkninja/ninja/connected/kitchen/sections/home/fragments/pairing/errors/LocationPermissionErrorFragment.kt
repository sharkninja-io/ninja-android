package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.errors

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentLocationPermissionErrorBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters.LocationPermissionRequester
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class LocationPermissionErrorFragment : Fragment() {

    private lateinit var binding: FragmentLocationPermissionErrorBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val locationPermissionRequester: LocationPermissionRequester =
        LocationPermissionRequester(this, ::onLocationGranted, ::onLocationDenied)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationPermissionErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
    }

    private fun initClickListeners() {
        if (homeViewModel.locationPermissionErrorState.value.isActionGoToSettings) setClickListenerGoToSettings()
        else setClickListenerTryAgain()
    }

    override fun onResume() {
        super.onResume()
        if (homeViewModel.locationPermissionErrorState.value.isCheckPermissionsOnResume) {
            locationPermissionRequester.checkPermissions(requireContext())
            homeViewModel.setLocationPermissionsErrorState(checkLocationPermissionOnResume = false, actionGoToSettings = true)
        }
    }

    private fun onLocationGranted() {
        homeViewModel.updateBluetoothPermissionLocation(true)
        findNavController().popBackStack()
    }

    private fun onLocationDenied() {
        setClickListenerGoToSettings()
    }

    private fun setClickListenerTryAgain() {
        binding.errorButton.text = getString(R.string.try_again)
        binding.errorButton.setOnClickListener {
            locationPermissionRequester.checkPermissions(requireContext())
        }
    }

    private fun setClickListenerGoToSettings() {
        binding.errorButton.text = getString(R.string.bluetooth_permissions_error_button_go_to_settings)
        binding.errorButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", requireActivity().packageName, null)
            })

            homeViewModel.setLocationPermissionsErrorState(checkLocationPermissionOnResume = true, actionGoToSettings = true)
        }
    }
}