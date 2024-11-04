package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentWifiOptionBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate

class WiFiInternationalOptionFragment: Fragment() {
    private lateinit var binding: FragmentWifiOptionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWifiOptionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.buttonStartWifiPairing.setOnClickListener {
            val directions = WiFiInternationalOptionFragmentDirections.actionWifiInternationalOptionFragmentToSelectBTLEWifiInternationalFragment()
            findNavController().safeNavigate(directions)
        }
    }
}