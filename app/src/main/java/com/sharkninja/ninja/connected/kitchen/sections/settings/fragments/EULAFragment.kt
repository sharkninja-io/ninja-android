package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentEulaBinding

class EULAFragment: Fragment() {
    private lateinit var binding: FragmentEulaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEulaBinding.inflate(inflater, container, false)
        return binding.root
    }
}