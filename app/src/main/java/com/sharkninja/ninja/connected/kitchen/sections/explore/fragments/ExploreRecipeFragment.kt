package com.sharkninja.ninja.connected.kitchen.sections.explore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardInfoAction
import com.sharkninja.ninja.connected.kitchen.data.enums.getDisplayName
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentExploreRecipeBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class  ExploreRecipeFragment : Fragment() {
    private lateinit var binding: FragmentExploreRecipeBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    private fun initBindings() {
        val displayChart = homeViewModel.selectedChartFlow.value
        binding.recipeImage.setImageResource(displayChart.image)
        binding.recipeTitle.text = displayChart.name
        binding.preparationContent.text = displayChart.preparation

        if (displayChart.grillTemperatureF == 0) {
            binding.grillTemperatureContent.text = displayChart.grillTemperatureGeneric
        } else {
            binding.grillTemperatureContent.text = displayChart.grillTemperatureF.toString() + getString(homeViewModel.userTemperatureUnit.value.getDisplayName())
        }

        binding.grillTimeContent.text = homeViewModel.secondsToHoursAndMins(displayChart.cookTime.toInt())
        binding.notesContent.text = displayChart.notes

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.useThisChartButton.setOnClickListener {
            homeViewModel.saveCookingChart()
            if(homeViewModel.canStartCookFromCharts()) {
                homeViewModel.updateAction(CookDashboardInfoAction.ShowChartsAccessoryModal)
            }
            homeViewModel.redirectToCook = true
            findNavController().safeNavigate(ExploreRecipeFragmentDirections.globalToHomeFragment())
        }
    }
}