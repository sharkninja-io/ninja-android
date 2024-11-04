package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.errors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentBluetoothPersistentErrorBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class BluetoothPersistentErrorFragment: Fragment() {
    private lateinit var binding: FragmentBluetoothPersistentErrorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBluetoothPersistentErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    private fun initBindings() {
        binding.buttonNext.setOnClickListener {
            findNavController().safeNavigate(BluetoothPersistentErrorFragmentDirections.actionBluetoothPersistentErrorFragmentToPairingPermissionsFragment())
        }
    }
}