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
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentPermissionsErrorBinding

class PermissionsErrorFragment : Fragment() {

    private lateinit var binding: FragmentPermissionsErrorBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPermissionsErrorBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.buttonSettings.setOnClickListener {
//            findNavController().navigate(PermissionsErrorFragmentDirections.actionPermissionsErrorFragmentToConnectToGrillErrorFragment())

            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", requireActivity().packageName, null)
            })
        }
    }
}