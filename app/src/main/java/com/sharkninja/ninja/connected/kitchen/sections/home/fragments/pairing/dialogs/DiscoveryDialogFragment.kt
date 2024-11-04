package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.dialogs

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentDiscoveryDialogBinding

class DiscoveryDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDiscoveryDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiscoveryDialogBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.buttonGotIt.setOnClickListener {
            dismiss()
        }
    }
}