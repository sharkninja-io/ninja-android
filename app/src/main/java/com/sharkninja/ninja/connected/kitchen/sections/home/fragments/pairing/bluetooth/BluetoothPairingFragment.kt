package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.app.Dialog
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.BluetoothGatt.STATE_DISCONNECTED
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.DialogBluetoothConnectionFailureBinding
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentBluetoothPairingBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth.ConnectionChanged
import com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth.ServicesDiscovered
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BluetoothPairingFragment : Fragment() {

    private lateinit var binding: FragmentBluetoothPairingBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBluetoothPairingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToEvents()
    }

    private fun subscribeToEvents() {
        homeViewModel.storeBluetoothDeviceSerial()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (homeViewModel.deviceMACAddresses.isNotEmpty()) {
                    homeViewModel.selectedDevice?.let { homeViewModel.connectToDevice(it) }
                }

                homeViewModel.eventFlow().collect {
                    when (it) {
                        is ConnectionChanged -> {
                            if (it.newState == STATE_DISCONNECTED) {
                                Log.d(BluetoothInternationalPairingFragment::class.java.simpleName, "STATE DISCONNECTED ERROR")
//                                showError()
                            }
                        }
                        is ServicesDiscovered -> {
                            when (it.status) {
                                GATT_SUCCESS -> {
                                    Log.d(BluetoothInternationalPairingFragment::class.java.simpleName, "SUCCESS: ${it.status}")
                                    showConnectionSuccess()
                                }
                                else -> {
                                    Log.d(BluetoothInternationalPairingFragment::class.java.simpleName, "STATUS ERROR: ${it.status}")
                                    showError()
                                }
                            }
                        }
                        else -> { }
                    }
                }
            }
        }
    }

    private fun showError() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            delay(3000)

            binding.waitingTitle.animate().alpha(0f).setDuration(500)
                .withEndAction {
                    binding.connectedTitle.animate().alpha(1f).duration = 500
                }.start()

            binding.waitingSub.animate().alpha(0f).setDuration(500).start()

            binding.ellipseWaiting.animate().scaleX(0f).scaleY(0f).setDuration(500)
                .withEndAction {
                    showErrorDialog()
                }.start()
        }
    }

    private fun showConnectionSuccess() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            delay(3000)

            binding.waitingTitle.animate().alpha(0f).setDuration(500)
                .withEndAction {
                    binding.connectedTitle.animate().alpha(1f).duration = 500
                }.start()

            binding.waitingSub.animate().alpha(0f).setDuration(500).start()

            binding.ellipseWaiting.animate().scaleX(0f).scaleY(0f).setDuration(500)
                .withEndAction {
                    binding.connectedTitle.text =
                        getString(R.string.bluetooth_paired_success_header)
                    binding.waitingSub.visibility = View.GONE
                    binding.pairedCheck.animate().scaleX(1f).scaleY(1f).duration = 500
                }.start()

            delay(3000)
            findNavController().navigate(BluetoothPairingFragmentDirections.actionBluetoothPairingFragmentToBluetoothNameYourGrillFragment())
        }
    }

    private fun showErrorDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogBluetoothConnectionFailureBinding.inflate(layoutInflater)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.buttonGetStarted.setOnClickListener {
            dialog.dismiss()
            findNavController().safeNavigate(BluetoothPairingFragmentDirections.actionBluetoothPairingFragmentToPairingPreparationsFraagment())
        }
        dialog.show()
    }
}