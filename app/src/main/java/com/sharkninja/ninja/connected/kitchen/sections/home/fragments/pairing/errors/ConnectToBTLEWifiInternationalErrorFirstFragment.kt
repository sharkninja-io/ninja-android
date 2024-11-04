package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.errors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentConnectToBtleWifiErrorFirstBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate

class ConnectToBTLEWifiInternationalErrorFirstFragment : Fragment() {

    private lateinit var binding: FragmentConnectToBtleWifiErrorFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnectToBtleWifiErrorFirstBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.buttonTryAgain.setOnClickListener {
            findNavController().safeNavigate(ConnectToBTLEWifiInternationalErrorFirstFragmentDirections
                .actionConnectToBTLEWifiInternationalErrorFirstFragmentToSelectBTLEWifiInternationalFragment())
        }
    }
}