package com.sharkninja.ninja.connected.kitchen.sections.authentication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentEnterYourCodeBinding

class EnterYourCodeFragment : Fragment() {

    private lateinit var binding: FragmentEnterYourCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterYourCodeBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
//        binding.resetPasswordButton.setOnClickListener {
//            findNavController().navigate(EnterYourCodeFragmentDirections.actionEnterYourCodeFragmentToResetPasswordFragment())
//        }
    }
}