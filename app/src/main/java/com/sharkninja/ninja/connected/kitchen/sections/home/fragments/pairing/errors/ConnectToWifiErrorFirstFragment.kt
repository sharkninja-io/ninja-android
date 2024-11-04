package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.errors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentConnectToWifiErrorFirstBinding

class ConnectToWifiErrorFirstFragment : Fragment() {

    private lateinit var binding: FragmentConnectToWifiErrorFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnectToWifiErrorFirstBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.buttonTryAgain.setOnClickListener {
            findNavController().navigate(ConnectToWifiErrorFirstFragmentDirections
                .actionConnectToWifiErrorFirstFragmentToSelectWifiFragment())
        }
    }
}