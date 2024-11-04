package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.dialogs

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sharkninja.ninja.connected.kitchen.databinding.PlugInModalBinding

class MoreInfoDialog: BottomSheetDialogFragment() {
    private lateinit var binding: PlugInModalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PlugInModalBinding.inflate(inflater, container, false)

        binding.gotItButton.setOnClickListener { dismiss() }

        return binding.root
    }

    companion object {
        const val TAG = "MoreInfoDialog"
    }
}