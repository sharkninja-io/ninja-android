package com.sharkninja.ninja.connected.kitchen.sections.authentication.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentTermsOfUseWebviewBinding

class TermsAndConditionsBottomSheet: BottomSheetDialogFragment() {
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