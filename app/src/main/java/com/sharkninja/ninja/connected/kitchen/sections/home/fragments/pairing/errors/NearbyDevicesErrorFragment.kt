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
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentNearbyDevicesPermissionsErrorBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters.NearbyDevicesPermissionRequester
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel


class NearbyDevicesErrorFragment : Fragment() {

    private lateinit var binding: FragmentNearbyDevicesPermissionsErrorBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val nearbyDevicesPermissionRequester: NearbyDevicesPermissionRequester = NearbyDevicesPermissionRequester(this, ::onNearbyDevicesGranted, ::onNearbyDevicesDenied)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNearbyDevicesPermissionsErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    private fun initBindings() {
        if(homeViewModel.nearbyPermissionErrorState.value.isActionGoToSettings) setClickListenerGoToSettings()
        else setClickListenerTryAgain()
    }

    override fun onResume() {
        super.onResume()
        if (homeViewModel.nearbyPermissionErrorState.value.isCheckPermissionsOnResume) {
            nearbyDevicesPermissionRequester.checkPermissions(requireContext())
            homeViewModel.setNearbyPermissionsErrorState(
                checkNearbyPermissionOnResume = false,
                actionGoToSettings = true
            )
        }
    }

    private fun onNearbyDevicesGranted() {
        homeViewModel.updateBluetoothPermissionNearbyDevices(true)
        findNavController().popBackStack()
    }

    private fun onNearbyDevicesDenied() {
        setClickListenerGoToSettings()
    }

    private fun setClickListenerTryAgain() {
        binding.errorButton.text = getString(R.string.try_again)
        binding.errorButton.setOnClickListener {
            nearbyDevicesPermissionRequester.checkPermissions(requireContext())
        }
    }

    private fun setClickListenerGoToSettings() {
        binding.errorButton.text = getString(R.string.bluetooth_permissions_error_button_go_to_settings)
        binding.errorButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", requireActivity().packageName, null)
            })
            homeViewModel.setNearbyPermissionsErrorState(checkNearbyPermissionOnResume = true, actionGoToSettings = true)
        }
    }

}