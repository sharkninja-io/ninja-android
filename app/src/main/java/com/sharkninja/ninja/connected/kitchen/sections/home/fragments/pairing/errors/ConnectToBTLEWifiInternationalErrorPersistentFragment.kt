package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.errors

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.DialogCustomerSupportBinding
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentConnectToBtleWifiErrorPersistentBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate

class ConnectToBTLEWifiInternationalErrorPersistentFragment : Fragment() {

    private lateinit var binding: FragmentConnectToBtleWifiErrorPersistentBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) dialSupportNumber()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConnectToBtleWifiErrorPersistentBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.buttonTryAgain.setOnClickListener {
            findNavController().safeNavigate(ConnectToBTLEWifiInternationalErrorPersistentFragmentDirections
                .actionConnectToBTLEWifiInternationalErrorPersistentFragmentToSelectBTLEWifiInternationalFragment())
        }

        binding.buttonSupport.setOnClickListener {
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogCustomerSupportBinding.inflate(layoutInflater)

            dialogBinding.buttonCallUs.setOnClickListener {
                askForPhonePermission()
            }

            dialogBinding.buttonNevermind.setOnClickListener { dialog.dismiss() }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
            dialog.setContentView(dialogBinding.root)
            dialog.show()
        }
    }

    private fun askForPhonePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            dialSupportNumber()
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {
            // Need UI for this???
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        }
    }

    private fun dialSupportNumber() {
        startActivity(Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:${resources.getString(R.string.customer_support_number)}")
        })
    }
}