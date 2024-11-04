package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sharkninja.ninja.connected.kitchen.databinding.PressStartModalBinding

class PressStartModal: BottomSheetDialogFragment() {
    private lateinit var binding: PressStartModalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PressStartModalBinding.inflate(inflater, container, false)

        binding.gotItButton.setOnClickListener { dismiss() }

        return binding.root
    }

    companion object {
        const val TAG = "PressStartModal"
    }
}