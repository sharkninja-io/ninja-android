package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentPairingPreparationBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.PermissionStatus
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.RequestLocationPermissionsInterface
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

private const val LOCATION_REQUEST_CODE = 1337

class PairingPreparationFragment : Fragment(), RequestLocationPermissionsInterface {

    private lateinit var binding: FragmentPairingPreparationBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPairingPreparationBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.progressIndicator.stepThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_grey))

        binding.buttonNext.setOnClickListener {
            findNavController().navigate(PairingPreparationFragmentDirections.actionPairingPreparationFragmentToConnectToGrillFragment())
        }

        binding.moreInfo1.setOnClickListener {
            findNavController().navigate(PairingPreparationFragmentDirections.actionPairingPreparationFragmentToPlugDialogFragment())
        }

        binding.moreInfo2.setOnClickListener {
            findNavController().navigate(PairingPreparationFragmentDirections.actionPairingPreparationFragmentToStartDialogFragment())
        }
    }


    override fun getLocationPermissionStatus(): PermissionStatus {
        return when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> {
                PermissionStatus.Granted
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                PermissionStatus.NotGranted
            }
            else -> {
                PermissionStatus.HaventAsked
            }
        }
    }

    override fun requestLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> {
                homeViewModel.setPermissionStatus(isPermissionGranted = PermissionStatus.Granted)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                homeViewModel.setPermissionStatus(isPermissionGranted = PermissionStatus.NotGranted)
            }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                // if accepted go to next step
                // else show rationale page
                homeViewModel.setPermissionStatus(
                    isPermissionGranted = if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        PermissionStatus.Granted
                    } else {
                        PermissionStatus.NotGranted
                    }
                )
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        when (val permissionStatus = getLocationPermissionStatus()) {
            PermissionStatus.HaventAsked -> requestLocationPermissions()
            PermissionStatus.Granted -> setNextButtonOnClickListener(permissionStatus)
            PermissionStatus.NotGranted -> findNavController().navigate(PairingPreparationFragmentDirections.actionPairingPreparationFragmentToPermissionsErrorFragment())
        }
    }

    private fun setNextButtonOnClickListener(permissionStatus: PermissionStatus) {
        binding.buttonNext.setOnClickListener {
            when (permissionStatus) {
                PermissionStatus.HaventAsked -> requestLocationPermissions()
                PermissionStatus.Granted -> findNavController().navigate(PairingPreparationFragmentDirections.actionPairingPreparationFragmentToConnectToGrillFragment())
                PermissionStatus.NotGranted -> findNavController().navigate(PairingPreparationFragmentDirections.actionPairingPreparationFragmentToPermissionsErrorFragment())
            }
        }

        binding.buttonNext.isEnabled = true
    }
}