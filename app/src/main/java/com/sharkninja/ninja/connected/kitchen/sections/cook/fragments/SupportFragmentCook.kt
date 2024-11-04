package com.sharkninja.ninja.connected.kitchen.sections.cook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentSupportBinding

class SupportFragmentCook : Fragment() {

    private lateinit var binding: FragmentSupportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWebview()
    }

    private fun setUpWebview() {
        binding.supportWebview.loadUrl("https://support.ninjakitchen.com/hc/en-us/sections/9185734924060-OG901-Series-")
        binding.supportWebview.webViewClient = WebViewClient()
    }
}