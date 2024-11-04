package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.models.Appliance
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentAppliancesBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import com.sharkninja.ninja.connected.kitchen.ui.adapters.ApplianceListAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription

class AppliancesFragment : BaseSettingsFragment() {

    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentAppliancesBinding
    private lateinit var adapter: ApplianceListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppliancesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()
        initClickListeners()
    }

    private fun initClickListeners() {

        binding.applianceBtn.setOnClickListener {
            homeViewModel.isActionAddAppliance = true
            findNavController().safeNavigate(AppliancesFragmentDirections.actionAppliancesFragmentToHome())
        }
    }

    override fun onResume() {
        super.onResume()
        val applianceActivity = (requireActivity() as HomeActivity)
        applianceActivity.setToolbarNavCustomClickListener {
            findNavController().safeNavigate(AppliancesFragmentDirections.actionAppliancesFragmentToSettingsFragment())
        }
        applianceActivity.hideCloseButton()
    }

    private fun observeViewModel() {
        settingsViewModel.applianceList.onSubscription {
            settingsViewModel.fetchGrills()
        }.onEach {
            if (it.isEmpty()) {
                showNoApplianceContainer()
            } else {
                showApplianceContainer()
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun showNoApplianceContainer() {
        binding.noApplianceContainer.visibility = View.VISIBLE
        binding.appliancesContainer.visibility = View.GONE
        binding.applianceBtn.text = getString(R.string.add_appliance_btn)
    }

    private fun showApplianceContainer() {
        binding.noApplianceContainer.visibility = View.GONE
        binding.appliancesContainer.visibility = View.VISIBLE
        binding.applianceBtn.text = getString(R.string.add_another_appliance)
        setUpApplianceList()
    }

    private fun setUpApplianceList() {
        binding.appliancesRv.layoutManager = LinearLayoutManager(requireContext())
        adapter = ApplianceListAdapter(settingsViewModel.applianceList.value, ::onListItemClick)
        binding.appliancesRv.adapter = adapter
    }

    private fun onListItemClick(appliance: Appliance) {
        settingsViewModel.setCurrentAppliance(appliance)
        val directions =
            AppliancesFragmentDirections.actionAppliancesFragmentToApplianceLandingFragment()
        findNavController().safeNavigate(directions)
    }

}