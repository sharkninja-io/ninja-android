package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentApplianceLandingBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel

class ApplianceLandingFragment : BaseSettingsFragment() {

    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }
    private lateinit var binding: FragmentApplianceLandingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApplianceLandingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBindings()
    }

    private fun initBindings() {
        setPageHeader(settingsViewModel.currentAppliance.value.name)

        binding.applianceDetailCard.setOnClick {
            val directions = ApplianceLandingFragmentDirections.actionApplianceLandingFragmentToApplianceDetailFragment()
            findNavController().safeNavigate(directions)
        }
        binding.supportCard.setOnClick {
            val directions = ApplianceLandingFragmentDirections.actionApplianceLandingToSupportFragment()
            findNavController().safeNavigate(directions)
        }
        binding.warrantyInformationCard.setOnClick {
            findNavController().navigate(ApplianceLandingFragmentDirections.actionApplianceLandingFragmentToWarrantyInformationWebViewFragment())
        }
    }

    private fun setPageHeader(name: String) {
        binding.pageHeader.text = name
    }
}