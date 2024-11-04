package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentTermsOfUseWebviewBinding

class TermsOfUseFragment : BaseSettingsFragment() {

    private lateinit var binding: FragmentTermsOfUseWebviewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTermsOfUseWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWebview()
    }

    private fun setUpWebview() {
        binding.webView.loadUrl("https://www.ninjakitchen.com/page/terms-and-conditions")
        binding.webView.webViewClient = WebViewClient()
    }
}