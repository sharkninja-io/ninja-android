package com.sharkninja.ninja.connected.kitchen.sections.cook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentMiniChartsBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CookTypeStrings
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CookingCard
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CustomChartSelectorAdapter
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.FoodTypeStrings
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class MiniChartsFragment : Fragment() {
    private lateinit var binding: FragmentMiniChartsBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    var cookMode = ""

    private lateinit var charts: MutableList<CookingCard>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMiniChartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBindings()
    }

    private fun initBindings() {
        cookMode = homeViewModel.cookMode.value.toString().lowercase()
        if(cookMode == CookTypeStrings.airCrisp) {
            binding.cookPromptText.text = "What are you thinking to\nair crisp today?"
        } else {
            binding.cookPromptText.text = "What are you thinking to\n${cookMode} today?"
        }

        calculateSpacingForHorizontalModeSelector()
        initHorizontalModeSelectorBindings()
        initChartsRecyclerView()
        disableFoodsWithoutCharts()
    }

    private fun disableFoodsWithoutCharts() {
        if (!homeViewModel.checkCurrentCookModeAndMeatTypeForCharts(FoodTypeStrings.chicken)) {
            binding.poultryContainer.visibility = View.GONE
            binding.spacerOneB.visibility = View.GONE
        }
        if (!homeViewModel.checkCurrentCookModeAndMeatTypeForCharts(FoodTypeStrings.beef)) {
            binding.beefContainer.visibility = View.GONE
            binding.spacerTwoB.visibility = View.GONE
        }
        if (!homeViewModel.checkCurrentCookModeAndMeatTypeForCharts(FoodTypeStrings.pork)) {
            binding.porkContainer.visibility = View.GONE
            binding.spacerThreeB.visibility = View.GONE
        }
        if (!homeViewModel.checkCurrentCookModeAndMeatTypeForCharts(FoodTypeStrings.seafood)) {
            binding.seafoodContainer.visibility = View.GONE
            binding.spacerFourB.visibility = View.GONE
        }
        if (!homeViewModel.checkCurrentCookModeAndMeatTypeForCharts(FoodTypeStrings.bread)) {
            binding.breadContainer.visibility = View.GONE
            binding.spacerFiveB.visibility = View.GONE
        }
        if (!homeViewModel.checkCurrentCookModeAndMeatTypeForCharts(FoodTypeStrings.fruit)) {
            binding.fruitContainer.visibility = View.GONE
            binding.spacerSixB.visibility = View.GONE
        }
        if (!homeViewModel.checkCurrentCookModeAndMeatTypeForCharts(FoodTypeStrings.lamb)) {
            binding.lambContainer.visibility = View.GONE
            binding.spacerSevenB.visibility = View.GONE
        }
        if (!homeViewModel.checkCurrentCookModeAndMeatTypeForCharts(FoodTypeStrings.veggies)) {
            binding.veggiesContainer.visibility = View.GONE
            binding.spacerEightB.visibility = View.GONE
        }
        if (!homeViewModel.checkCurrentCookModeAndOtherMeatTypeForCharts()) {
            binding.otherContainer.visibility = View.GONE
        }
    }

    private fun initChartsRecyclerView() {
        binding.poultryIcon.setBackgroundResource(R.drawable.circle_background_selected)
        binding.poultryIconImage.setBackgroundResource(R.drawable.ic_explore_poultry_selected)
        binding.poultryLabel.setTextColor(resources.getColor(R.color.black1))

        charts = mutableListOf()

        for (chart in homeViewModel.allCharts) {
            if (chart.meatType.lowercase() == FoodTypeStrings.chicken && chart.cookingMethod.lowercase() == cookMode) {
                charts.add(chart)
            }
        }

        setupAdapter()
    }

    private fun setupAdapter() {
        binding.chartsRecycler.layoutManager = GridLayoutManager(this.context, 2)
        val adapter = CustomChartSelectorAdapter(charts)
        binding.chartsRecycler.adapter = adapter

        adapter.setOnItemClickListener(
            object : CustomChartSelectorAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    homeViewModel.setSelectedChartFlow(adapter.getItem(position))
                    findNavController().navigate(MiniChartsFragmentDirections.miniChartToSelectedChart())
                }
            }
        )
    }

    private fun initHorizontalModeSelectorBindings() {
        binding.poultryIcon.setOnClickListener {
            unselectAll()

            binding.poultryIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.poultryIconImage.setBackgroundResource(R.drawable.ic_explore_poultry_selected)
            binding.poultryLabel.setTextColor(resources.getColor(R.color.black1))

            charts = mutableListOf()
            for (chart in homeViewModel.allCharts) {
                if (chart.meatType.lowercase() == FoodTypeStrings.chicken && chart.cookingMethod.lowercase() == cookMode) {
                    charts.add(chart)
                }
            }
            setupAdapter()
        }
        binding.beefIcon.setOnClickListener {
            unselectAll()

            binding.beefIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.beefIconImage.setBackgroundResource(R.drawable.ic_explore_beef_selected)
            binding.beefLabel.setTextColor(resources.getColor(R.color.black1))

            charts = mutableListOf()
            for (chart in homeViewModel.allCharts) {
                if (chart.meatType.lowercase() == FoodTypeStrings.beef && chart.cookingMethod.lowercase() == cookMode) {
                    charts.add(chart)
                }
            }
            setupAdapter()
        }

        binding.porkIcon.setOnClickListener {
            unselectAll()

            binding.porkIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.porkIconImage.setBackgroundResource(R.drawable.ic_explore_pork_selected)
            binding.porkLabel.setTextColor(resources.getColor(R.color.black1))

            charts = mutableListOf()
            for (chart in homeViewModel.allCharts) {
                if (chart.meatType.lowercase() == FoodTypeStrings.pork && chart.cookingMethod.lowercase() == cookMode) {
                    charts.add(chart)
                }
            }
            setupAdapter()
        }

        binding.seafoodIcon.setOnClickListener {
            unselectAll()

            binding.seafoodIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.seafoodIconImage.setBackgroundResource(R.drawable.ic_explore_seafood_selected)
            binding.seafoodLabel.setTextColor(resources.getColor(R.color.black1))

            charts = mutableListOf()
            for (chart in homeViewModel.allCharts) {
                if (chart.meatType.lowercase() == FoodTypeStrings.seafood && chart.cookingMethod.lowercase() == cookMode) {
                    charts.add(chart)
                }
            }
            setupAdapter()
        }

        binding.veggiesIcon.setOnClickListener {
            unselectAll()

            binding.veggiesIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.veggiesIconImage.setBackgroundResource(R.drawable.ic_explore_carrot_selected)
            binding.veggiesLabel.setTextColor(resources.getColor(R.color.black1))

            charts = mutableListOf()
            for (chart in homeViewModel.allCharts) {
                if (chart.meatType.lowercase() == FoodTypeStrings.veggies && chart.cookingMethod.lowercase() == cookMode) {
                    charts.add(chart)
                }
            }
            setupAdapter()
        }

        binding.breadIcon.setOnClickListener {
            unselectAll()

            binding.breadIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.breadIconImage.setBackgroundResource(R.drawable.ic_explore_cheese_selected)
            binding.breadLabel.setTextColor(resources.getColor(R.color.black1))

            charts = mutableListOf()
            for (chart in homeViewModel.allCharts) {
                if (chart.meatType.lowercase() == FoodTypeStrings.bread && chart.cookingMethod.lowercase() == cookMode) {
                    charts.add(chart)
                }
            }
            setupAdapter()
        }

        binding.fruitIcon.setOnClickListener {
            unselectAll()

            binding.fruitIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.fruitIconImage.setBackgroundResource(R.drawable.ic_explore_fruit_selected)
            binding.fruitLabel.setTextColor(resources.getColor(R.color.black1))

            charts = mutableListOf()
            for (chart in homeViewModel.allCharts) {
                if (chart.meatType.lowercase() == FoodTypeStrings.fruit && chart.cookingMethod.lowercase() == cookMode) {
                    charts.add(chart)
                }
            }
            setupAdapter()
        }

        binding.lambIcon.setOnClickListener {
            unselectAll()

            binding.lambIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.lambIconImage.setBackgroundResource(R.drawable.ic_explore_lamb_selected)
            binding.lambLabel.setTextColor(resources.getColor(R.color.black1))

            charts = mutableListOf()
            for (chart in homeViewModel.allCharts) {
                if (chart.meatType.lowercase() == FoodTypeStrings.lamb && chart.cookingMethod.lowercase() == cookMode) {
                    charts.add(chart)
                }
            }
            setupAdapter()
        }

        binding.otherIcon.setOnClickListener {
            unselectAll()

            binding.otherIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.otherIconImage.setBackgroundResource(R.drawable.ic_explore_other_selected)
            binding.otherLabel.setTextColor(resources.getColor(R.color.black1))

            charts = mutableListOf()
            for (chart in homeViewModel.allCharts) {
                if (homeViewModel.isOtherFoodType(chart.meatType.lowercase()) && chart.cookingMethod.lowercase() == cookMode) {
                    charts.add(chart)
                }
            }
            setupAdapter()
        }
    }

    private fun unselectAll() {
        binding.poultryIcon.setBackgroundResource(R.drawable.circle_background_unselected)
        binding.poultryIconImage.setBackgroundResource(R.drawable.ic_explore_poultry_unselected)
        binding.poultryLabel.setTextColor(resources.getColor(R.color.medium_grey))

        binding.beefIcon.setBackgroundResource(R.drawable.circle_background_unselected)
        binding.beefIconImage.setBackgroundResource(R.drawable.ic_explore_beef_unselected)
        binding.beefLabel.setTextColor(resources.getColor(R.color.medium_grey))

        binding.porkIcon.setBackgroundResource(R.drawable.circle_background_unselected)
        binding.porkIconImage.setBackgroundResource(R.drawable.ic_explore_pork_unselected)
        binding.porkLabel.setTextColor(resources.getColor(R.color.medium_grey))

        binding.seafoodIcon.setBackgroundResource(R.drawable.circle_background_unselected)
        binding.seafoodIconImage.setBackgroundResource(R.drawable.ic_explore_seafood_unselected)
        binding.seafoodLabel.setTextColor(resources.getColor(R.color.medium_grey))

        binding.veggiesIcon.setBackgroundResource(R.drawable.circle_background_unselected)
        binding.veggiesIconImage.setBackgroundResource(R.drawable.ic_explore_carrot_unselected)
        binding.veggiesLabel.setTextColor(resources.getColor(R.color.medium_grey))

        binding.breadIcon.setBackgroundResource(R.drawable.circle_background_unselected)
        binding.breadIconImage.setBackgroundResource(R.drawable.ic_explore_cheese_unselected)
        binding.breadLabel.setTextColor(resources.getColor(R.color.medium_grey))

        binding.fruitIcon.setBackgroundResource(R.drawable.circle_background_unselected)
        binding.fruitIconImage.setBackgroundResource(R.drawable.ic_explore_fruit_unselected)
        binding.fruitLabel.setTextColor(resources.getColor(R.color.medium_grey))

        binding.lambIcon.setBackgroundResource(R.drawable.circle_background_unselected)
        binding.lambIconImage.setBackgroundResource(R.drawable.ic_explore_lamb_unselected)
        binding.lambLabel.setTextColor(resources.getColor(R.color.medium_grey))

        binding.otherIcon.setBackgroundResource(R.drawable.circle_background_unselected)
        binding.otherIconImage.setBackgroundResource(R.drawable.ic_explore_other_unselected)
        binding.otherLabel.setTextColor(resources.getColor(R.color.medium_grey))
    }

    private fun calculateSpacingForHorizontalModeSelector() {
        val numberOfButtonsTotal = 8 //keep this current
        val xdpi = homeViewModel.xPix / homeViewModel.density

        var spacerSize = 0

        //this set of loops finds the most buttons that will fit on the screen
        //with the least amount of spacing between them
        for (i in 1 until numberOfButtonsTotal + 1) {
            var scrollViewExtraDP = (xdpi - 19).toInt()
            scrollViewExtraDP -= (74 * i)

            if (scrollViewExtraDP > 74) {
                continue
            } else {
                var foundMinimumSpacerSize = false
                var workingSpacingAmount = 0
                for (j in 1 until 30) {
                    val newSpacingAmount = (scrollViewExtraDP - (j * (i - 1)))
                    if ((-37 <= newSpacingAmount) && (newSpacingAmount <= -19)) {
                        foundMinimumSpacerSize = true
                        workingSpacingAmount = j
                        break
                    }
                }
                if (foundMinimumSpacerSize) {
                    spacerSize = workingSpacingAmount
                }
            }
        }

        setSpacingWidth(spacerSize * homeViewModel.density)
    }

    private fun setSpacingWidth(size: Float) {
        binding.spacerOneB.layoutParams.width = size.toInt()
        binding.spacerTwoB.layoutParams.width = size.toInt()
        binding.spacerThreeB.layoutParams.width = size.toInt()
        binding.spacerFourB.layoutParams.width = size.toInt()
        binding.spacerFiveB.layoutParams.width = size.toInt()
        binding.spacerSixB.layoutParams.width = size.toInt()
        binding.spacerSevenB.layoutParams.width = size.toInt()
        binding.spacerEightB.layoutParams.width = size.toInt()
    }
}