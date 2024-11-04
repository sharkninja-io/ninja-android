package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentBluetoothInternationalSecondPairingPreparationBinding

class BluetoothInternationalSecondPairingPreparationFragment: Fragment() {

    private lateinit var binding: FragmentBluetoothInternationalSecondPairingPreparationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBluetoothInternationalSecondPairingPreparationBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.progressIndicator.stepFour.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_grey))

        binding.buttonNext.setOnClickListener {
            findNavController().navigate(BluetoothInternationalSecondPairingPreparationFragmentDirections.actionBluetoothInternationalSecondPairingPreparationFragmentToBluetoothInternationalFindDevicesFragment())
        }
    }
}