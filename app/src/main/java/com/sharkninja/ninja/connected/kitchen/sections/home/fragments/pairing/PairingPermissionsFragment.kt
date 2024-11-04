package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentPairingPermissionsBinding

class PairingPermissionsFragment : Fragment() {

    private lateinit var binding: FragmentPairingPermissionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPairingPermissionsBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.progressIndicator.stepTwo.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
        binding.progressIndicator.stepThree.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_grey))

        binding.buttonNext.setOnClickListener {
            findNavController().navigate(PairingPermissionsFragmentDirections.actionPairingPermissionsFragmentToPairingPreparationFragment())
        }
    }
}