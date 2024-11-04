package com.sharkninja.ninja.connected.kitchen.sections.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentPairingBinding

class PairingFragment : Fragment() {

    private lateinit var binding: FragmentPairingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPairingBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.bluetoothPairingButton.setOnClickListener {
            findNavController().navigate(PairingFragmentDirections.actionPairingFragmentToBluetoothFragment())
        }
    }
}