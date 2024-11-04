package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.dialogs

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentNetworkTipsDialogBinding

class NetworkTipsDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNetworkTipsDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNetworkTipsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }
}