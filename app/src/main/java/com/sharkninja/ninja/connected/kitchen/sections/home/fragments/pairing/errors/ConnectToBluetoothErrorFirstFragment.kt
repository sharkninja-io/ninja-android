package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.errors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentConnectToBluetoothErrorFirstBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate

class ConnectToBluetoothErrorFirstFragment : Fragment() {

    private lateinit var binding: FragmentConnectToBluetoothErrorFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentConnectToBluetoothErrorFirstBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    private fun initBindings() {
        binding.buttonTryAgain.setOnClickListener {
            findNavController().safeNavigate(ConnectToBluetoothErrorFirstFragmentDirections.actionConnectToBluetoothErrorFirstFragmentToBluetoothFindDevicesFragment())
        }
    }
}