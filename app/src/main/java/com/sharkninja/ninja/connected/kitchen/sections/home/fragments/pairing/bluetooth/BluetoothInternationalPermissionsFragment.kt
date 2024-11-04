package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentBluetoothInternationalPermissionsBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters.NearbyDevicesPermissionRequester
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.permissionrequesters.LocationPermissionRequester
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription

class BluetoothInternationalPermissionsFragment : Fragment() {

    private lateinit var binding: FragmentBluetoothInternationalPermissionsBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val nearbyDevicesPermissionRequester = NearbyDevicesPermissionRequester(this, ::onNearbyDevicesGranted, ::onNearbyDevicesDenied)
    private val locationPermissionRequester = LocationPermissionRequester(this, ::onLocationGranted, ::onLocationDenied)

    private var isGrillsEmpty: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBluetoothInternationalPermissionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        overrideBackPressed()
        observeViewModel()
        initBindings()
        if (homeViewModel.getBTAdapter()?.isEnabled == true) {
            homeViewModel.updateBluetoothEnabled(bluetoothEnabled = true)
        }
        setErrorCounts()
    }

    private fun setErrorCounts() {
        homeViewModel.hotspotErrorCount = 0
        homeViewModel.wifiErrorCount = 0
    }

    private fun checkBluetoothEnabled() {
        if (homeViewModel.getBTAdapter()?.isEnabled == false) {
            homeViewModel.updateBluetoothEnabled(bluetoothEnabled = false)
            showBluetoothDisabledDialog()
        } else if (homeViewModel.getBTAdapter()?.isEnabled == true) {
            homeViewModel.updateBluetoothEnabled(bluetoothEnabled = true)
        }
    }

    private fun initBindings() {
        binding.progressIndicator.stepTwo.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_grey
            )
        )
        binding.progressIndicator.stepThree.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_grey
            )
        )
        binding.progressIndicator.stepFour.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.light_grey
            )
        )

        binding.enableBluetoothButton.setOnClickListener {
            checkBluetoothEnabled()
        }
        binding.detectGrillButton.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                homeViewModel.updateBluetoothPermissionNearbyDevices(true)
            } else {
                checkNearbyPermissions()
            }
        }
        binding.enableLocationButton.setOnClickListener {
            checkLocationPermission()
        }

        binding.buttonNext.setOnClickListener {
            //navigate to pairing setup fragment
            val directions = BluetoothInternationalPermissionsFragmentDirections.actionBluetoothInternationalPermissionsFragmentToBluetoothInternationalPairingPreparationFragment()
            findNavController().safeNavigate(directions)
        }
    }

    private fun observeViewModel() {
        homeViewModel.isBluetoothPermissionGranted.onEach {
            binding.buttonNext.isEnabled = it.locationPermission && it.bluetoothEnabled && it.nearbyDevicesPermission

            if (binding.buttonNext.isEnabled) homeViewModel.startPassiveScan()

            binding.arrow1.setIcon(it.bluetoothEnabled)
            binding.arrow2.setIcon(it.nearbyDevicesPermission)
            binding.arrow3.setIcon(it.locationPermission)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)


        homeViewModel.grills.onSubscription {
            homeViewModel.fetchGrills()
        }.onEach {
            isGrillsEmpty = it.isEmpty()
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun overrideBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    if(isGrillsEmpty.not()) {
                        isEnabled = false
                        homeViewModel.isBackFromAddAppliance = true
                        findNavController().safeNavigate(BluetoothPermissionsFragmentDirections.globalToSettings())
                    }
                }
            })
    }

    fun ImageView.setIcon(isGranted: Boolean) {
        val backgroundResource =
            if (isGranted) R.drawable.green_circle_check else R.drawable.ic_arrow
        this.setBackgroundResource(backgroundResource)
    }

    private fun checkNearbyPermissions() {
        nearbyDevicesPermissionRequester.checkPermissions(requireContext())
    }

    private fun onNearbyDevicesGranted() {
        homeViewModel.updateBluetoothPermissionNearbyDevices(nearbyDevicePermission = true)
    }

    private fun onNearbyDevicesDenied() {
        val directions = BluetoothInternationalPermissionsFragmentDirections.actionBluetoothInternationalPermissionsFragmentToNearbyDevicesErrorFragment()
        findNavController().safeNavigate(directions)
    }

    private fun checkLocationPermission() {
        locationPermissionRequester.checkPermissions(requireContext())
    }

    private fun onLocationGranted() {
        homeViewModel.updateBluetoothPermissionLocation(locationPermission = true)
    }

    private fun onLocationDenied() {
        val directions = BluetoothInternationalPermissionsFragmentDirections.actionBluetoothInternationalPermissionsFragmentToLocationPermissionErrorFragment()
        findNavController().safeNavigate(directions)
    }

    private fun showBluetoothDisabledDialog() {
        val directions = BluetoothInternationalPermissionsFragmentDirections.actionBluetoothInternationalPermissionsFragmentToBluetoothDisabledDialog()
        findNavController().safeNavigate(directions)
    }
}