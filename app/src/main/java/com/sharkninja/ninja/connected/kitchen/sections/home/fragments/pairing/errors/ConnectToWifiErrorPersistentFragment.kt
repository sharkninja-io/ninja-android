package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.errors

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.DialogCustomerSupportBinding
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentConnectToWifiErrorPersistentBinding

class ConnectToWifiErrorPersistentFragment : Fragment() {

    private lateinit var binding: FragmentConnectToWifiErrorPersistentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnectToWifiErrorPersistentBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.buttonTryAgain.setOnClickListener {
            findNavController().navigate(ConnectToWifiErrorPersistentFragmentDirections
                .actionConnectToWifiErrorPersistentFragmentToSelectWifiFragment())
        }

        binding.buttonSupport.setOnClickListener {
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogCustomerSupportBinding.inflate(layoutInflater)

            dialogBinding.buttonCallUs.setOnClickListener {
                startActivity(Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:${resources.getString(R.string.customer_support_number)}")
                })
            }

            dialogBinding.buttonNevermind.setOnClickListener { dialog.dismiss() }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
            dialog.setContentView(dialogBinding.root)
            dialog.show()
        }
    }
}