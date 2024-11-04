package com.sharkninja.ninja.connected.kitchen.sections.explore.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.getDisplayName
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentExploreBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.explore.bottomsheets.CookModeSelectorBottomSheet
import com.sharkninja.ninja.connected.kitchen.sections.explore.bottomsheets.FoodSelectorBottomSheet
import com.sharkninja.ninja.connected.kitchen.sections.explore.bottomsheets.TimeFilterBottomSheet
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CookingCard
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CustomChartSelectorAdapter
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CustomFilteredChartSelectectorAdaptor
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class ExploreFragment : Fragment() {

    private lateinit var binding: FragmentExploreBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var charts: MutableList<CookingCard>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setupFilteredAdapter() {
        binding.chartsRecycler.layoutManager = GridLayoutManager(this.context, 1)

        var unitString = getString(homeViewModel.userTemperatureUnit.value.getDisplayName())

        val adapter = CustomFilteredChartSelectectorAdaptor(homeViewModel.filteredCharts, unitString)

        binding.chartsRecycler.adapter = adapter

        adapter.setOnItemClickListener(
            object : CustomFilteredChartSelectectorAdaptor.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    homeViewModel.setSelectedChartFlow(adapter.getItem(position))
                    findNavController().safeNavigate(ExploreFragmentDirections.actionExploreFragmentToExploreRecipeFragment())
                }
            }
        )
    }

    private fun refreshGUI() {
        if (homeViewModel.anyFilters) {
            binding.clearAllFiltersButton.visibility = View.VISIBLE
        } else {
            binding.clearAllFiltersButton.visibility = View.GONE
        }

        if (homeViewModel.foodFilter) {
            binding.foodButtonText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_8F))
            binding.foodButton.setBackgroundResource(R.drawable.rounded_edges_ninja_green_16dp_radius)
        } else {
            binding.foodButtonText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black1))
            binding.foodButton.setBackgroundResource(R.drawable.rounded_edges_grey4_16dp_radius)
        }

        if (homeViewModel.timeFilter) {
            binding.timeButtonText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_8F))
            binding.timeButton.setBackgroundResource(R.drawable.rounded_edges_ninja_green_16dp_radius)
        } else {
            binding.timeButtonText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black1))
            binding.timeButton.setBackgroundResource(R.drawable.rounded_edges_grey4_16dp_radius)
        }

        if (homeViewModel.cookFilter) {
            binding.modeButtonText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_8F))
            binding.modeButton.setBackgroundResource(R.drawable.rounded_edges_ninja_green_16dp_radius)
        } else {
            binding.modeButtonText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black1))
            binding.modeButton.setBackgroundResource(R.drawable.rounded_edges_grey4_16dp_radius)
        }

//        if (homeViewModel.smokeFlavorFilter) {
//            binding.woodFireButtonText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_8F))
//            binding.woodFireButtonSelectedBackground.setBackgroundResource(R.drawable.rounded_edges_ninja_green_16dp_radius)
//        } else {
//            binding.woodFireButtonText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black1))
//            binding.woodFireButtonSelectedBackground.setBackgroundResource(R.drawable.rounded_edges_grey4_16dp_radius)
//        }

        if (homeViewModel.cookFilter || homeViewModel.timeFilter ||
            homeViewModel.foodFilter || homeViewModel.smokeFlavorFilter) {
            setupFilteredAdapter()
            binding.popularFilterGroupingsText.text = getString(R.string.charts_popular_groupings_results, homeViewModel.filteredCharts.size.toString())
        } else {
            setupAdapter()
            binding.popularFilterGroupingsText.text = getString(R.string.charts_popular_groupings)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBindings()
        overrideBackPressed()
    }

    private fun initBindings() {
        initChartsRecyclerView()

        binding.allFilters.setOnClickListener {
            findNavController().navigate(ExploreFragmentDirections.actionExploreFragmentToExploreAllFiltersFragment())
        }

        binding.clearAllFiltersButton.setOnClickListener {
            homeViewModel.clearAllFilters()
            homeViewModel.filterCharts()
            refreshGUI()
        }

        val cookModeSelectorBottomSheet = CookModeSelectorBottomSheet()
        cookModeSelectorBottomSheet.setOnDismissFunction {
            homeViewModel.filterCharts()
            refreshGUI()
        }
        binding.modeButton.setOnClickListener {
            cookModeSelectorBottomSheet.show(parentFragmentManager.beginTransaction().remove(cookModeSelectorBottomSheet), CookModeSelectorBottomSheet.TAG)
        }

        binding.timeButton.setOnClickListener {
            val timeFilterBottomSheet = TimeFilterBottomSheet()
            timeFilterBottomSheet.setOnDismissFunction {
                homeViewModel.filterCharts()
                refreshGUI()
            }
            timeFilterBottomSheet.show(parentFragmentManager.beginTransaction().remove(timeFilterBottomSheet), TimeFilterBottomSheet.TAG)
        }

        val foodSelectorBottomSheet = FoodSelectorBottomSheet()
        foodSelectorBottomSheet.setOnDismissFunction {
            homeViewModel.filterCharts()
            refreshGUI()
        }
        binding.foodButton.setOnClickListener {
            foodSelectorBottomSheet.show(parentFragmentManager.beginTransaction().remove(foodSelectorBottomSheet), FoodSelectorBottomSheet.TAG)
        }

//        binding.woodFireButton.setOnClickListener {
//            val woodFireBottomSheet = WoodFireBootomSheet()
//            woodFireBottomSheet.setOnDismissFunction {
//                homeViewModel.filterCharts()
//                refreshGUI()
//            }
//            woodFireBottomSheet.show(parentFragmentManager, WoodFireBootomSheet.TAG)
//        }

        refreshGUI()
    }

    private fun initChartsRecyclerView() {
        charts = mutableListOf()

        for (chart in homeViewModel.allCharts) {
            charts.add(chart)
        }

        //setupAdapter()
    }

    private fun setupAdapter() {
        binding.chartsRecycler.layoutManager = GridLayoutManager(this.context, 2)
        val adapter = CustomChartSelectorAdapter(charts)
        binding.chartsRecycler.adapter = adapter

        adapter.setOnItemClickListener(
            object : CustomChartSelectorAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    homeViewModel.setSelectedChartFlow(adapter.getItem(position))
                    findNavController().safeNavigate(ExploreFragmentDirections.actionExploreFragmentToExploreRecipeFragment())
                }
            }
        )
    }

    private fun overrideBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }
}