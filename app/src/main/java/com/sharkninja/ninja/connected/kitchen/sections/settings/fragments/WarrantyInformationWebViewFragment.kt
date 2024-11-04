package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentWarrantyInformationWebviewBinding

class WarrantyInformationWebViewFragment : BaseSettingsFragment() {

    private lateinit var binding: FragmentWarrantyInformationWebviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWarrantyInformationWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWebview()
    }

    private fun setUpWebview() {
        binding.webView.loadUrl("https://support.ninjakitchen.com/hc/en-us/sections/4402881170194-Warranty")
        binding.webView.webViewClient = WebViewClient()
    }
}