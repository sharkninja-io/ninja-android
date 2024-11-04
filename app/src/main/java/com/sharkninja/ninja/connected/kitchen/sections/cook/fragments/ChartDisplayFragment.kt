package com.sharkninja.ninja.connected.kitchen.sections.cook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardInfoAction
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentChartDisplayBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class ChartDisplayFragment: Fragment() {
    private lateinit var binding: FragmentChartDisplayBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBindings()
    }

    private fun initBindings() {
        binding.name.text = homeViewModel.selectedChartFlow.value.name
        binding.description.text = homeViewModel.selectedChartFlow.value.description
        binding.preparation.text = homeViewModel.selectedChartFlow.value.preparation

        if (homeViewModel.selectedChartFlow.value.grillTemperatureF == 0) {
            binding.grillTemperature.text = homeViewModel.selectedChartFlow.value.grillTemperatureGeneric
        } else {
            binding.grillTemperature.text = "${homeViewModel.selectedChartFlow.value.grillTemperatureF}°F"
        }

        binding.cookTime.text = homeViewModel.secondsToHoursAndMins(homeViewModel.selectedChartFlow.value.cookTime.toInt())
        binding.notes.text = homeViewModel.selectedChartFlow.value.notes

        binding.startCookingButton.setOnClickListener {
            homeViewModel.saveCookingChart()
            if(homeViewModel.canStartCookFromCharts()) {
                homeViewModel.updateAction(CookDashboardInfoAction.ShowChartsAccessoryModal)
            }
            findNavController().navigate(ChartDisplayFragmentDirections.globalToCookFragment())
        }
    }
}