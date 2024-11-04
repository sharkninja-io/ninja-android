package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.errors

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentConnectToGrillErrorBinding

class ConnectToGrillErrorFragment : Fragment() {

    private lateinit var binding: FragmentConnectToGrillErrorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConnectToGrillErrorBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.buttonTryAgain.setOnClickListener {
            findNavController().navigate(ConnectToGrillErrorFragmentDirections
                .actionConnectToGrillErrorFragmentToPairingPreparationFragment())
        }
    }
}