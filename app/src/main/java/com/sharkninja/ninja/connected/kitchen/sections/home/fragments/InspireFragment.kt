package com.sharkninja.ninja.connected.kitchen.sections.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentInspireBinding

class InspireFragment : Fragment() {

    private lateinit var binding: FragmentInspireBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInspireBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {

    }
}