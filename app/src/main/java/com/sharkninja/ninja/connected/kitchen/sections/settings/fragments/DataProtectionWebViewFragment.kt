package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentDataProtectionWebviewBinding

class DataProtectionWebViewFragment : BaseSettingsFragment() {

    private lateinit var binding: FragmentDataProtectionWebviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataProtectionWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWebview()
    }

    private fun setUpWebview() {
        binding.webView.loadUrl("http://www.sharkninja.com/privacy")
        binding.webView.webViewClient = WebViewClient()
    }
}