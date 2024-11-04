package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentBluetoothPairingPreparationBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.dialogs.MoreInfoDialog
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.dialogs.PressStartModal

class BluetoothPairingPreparationFragment: Fragment() {

    private lateinit var binding: FragmentBluetoothPairingPreparationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBluetoothPairingPreparationBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.progressIndicator.stepThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_grey))

        val plugInModalBottomSheet = MoreInfoDialog()
        val pressStartModal = PressStartModal()

        binding.stepOneMoreInfo.setOnClickListener {
            plugInModalBottomSheet.show(parentFragmentManager, MoreInfoDialog.TAG)
        }
        binding.stepTwoMoreInfo.setOnClickListener {
            pressStartModal.show(parentFragmentManager, PressStartModal.TAG)
        }
        binding.buttonNext.setOnClickListener {
            findNavController().navigate(BluetoothPairingPreparationFragmentDirections.actionBluetoothPreparationToBluetoothConnecting())
        }
    }
}