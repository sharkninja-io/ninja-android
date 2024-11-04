package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.dialogs

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentPlugDialogBinding

class PlugDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentPlugDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlugDialogBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.buttonGotIt.setOnClickListener {
            dismiss()
        }
    }
}