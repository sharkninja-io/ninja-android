package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentAboutThisAppBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel

class AboutThisAppFragment : BaseSettingsFragment() {

    private lateinit var binding: FragmentAboutThisAppBinding
    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutThisAppBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    private fun initBindings() {
        binding.termsOfUseCard.setOnClick {
            findNavController().navigate(AboutThisAppFragmentDirections.actionAboutThisAppFragmentToTermsOfUseWebViewFragment())
        }

        binding.infoLegalNoticeCard.setOnClick {
            findNavController().navigate(AboutThisAppFragmentDirections.actionAboutThisAppFragmentToEulaFragment())
        }

//        binding.dataProtectionCard.setOnClick {
//            findNavController().navigate(AboutThisAppFragmentDirections.actionAboutThisAppFragmentToDataProtectionFragment())
//        }

        binding.privacyPolicyCard.setOnClick {
            val directions =
                AboutThisAppFragmentDirections.actionAboutThisAppFragmentToPrivacyPolicyFragment()
            findNavController().safeNavigate(directions)
        }
    }
}