package com.sharkninja.ninja.connected.kitchen.sections.explore.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sharkninja.grillcore.CookMode
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentExploreAllFiltersBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CookTypeStrings
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CookingCard
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.FoodTypeStrings
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class ExploreAllFiltersFragment : Fragment() {
    private lateinit var binding: FragmentExploreAllFiltersBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    var allCharts: MutableList<CookingCard> = mutableListOf()
    var filteredCharts: MutableList<CookingCard> = mutableListOf()

    //this was used when only allowing the user to select one time range
    //var localCookTimeRangeFilter = 0

    var localAnyFilters = false

    var localTimeFilter = false
    var localFifteenMinBox = false
    var localFifteenToThirtyMinBox = false
    var localThirtyToSixtyMinBox = false
    var localSixtyToOneTwentyMinBox = false
    var localOneTwentyPlusBox = false

    var localSmokeFlavorFilter = false

    var localCookFilter = false
    var localGrillCookFilter = false
    var localSmokeCookFilter = false
    var localAirCrispCookFilter = false
    var localRoastCookFilter = false
    var localBakeCookFilter = false
    var localBroilCookFilter = false
    var localReheatCookFilter = false
    var localDehydrateCookFilter = false

    var localFoodFilter = false
    var localChickenFoodFilter = false
    var localBeefFoodFilter = false
    var localPorkFoodFilter = false
    var localFishFoodFilter = false
    var localVeggieFoodFilter = false
    var localBreadFoodFilter = false
    var localFruitFoodFilter = false
    var localLambFoodFilter = false
    var localOtherFoodFilter = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExploreAllFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allCharts = homeViewModel.allCharts

        initBindings()
    }

    private fun initBindings() {
        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

//        if (homeViewModel.smokeFlavorFilter) {
//            binding.woodFireTechSwitch.toggle()
//            filterCharts()
//        }
//        binding.woodFireTechSwitch.setOnCheckedChangeListener { _, isChecked ->
//            localSmokeFlavorFilter = isChecked
//            filterCharts()
//        }
        binding.viewResultsButton.setOnClickListener {
            saveState()
            findNavController().popBackStack()
        }

        copyViewModelState()
        initTimeSelector()
        initCookModeSelector()
        initFoodSelector()
        filterCharts()
    }

    private fun copyViewModelState() {
        localFoodFilter = homeViewModel.foodFilter
        localCookFilter = homeViewModel.cookFilter
        localTimeFilter = homeViewModel.timeFilter
        localSmokeFlavorFilter = homeViewModel.smokeFlavorFilter

        localGrillCookFilter = homeViewModel.grillCookFilter
        localSmokeCookFilter = homeViewModel.smokeCookFilter
        localAirCrispCookFilter = homeViewModel.airCrispCookFilter
        localRoastCookFilter = homeViewModel.roastCookFilter
        localDehydrateCookFilter = homeViewModel.dehydrateCookFilter
        localBroilCookFilter = homeViewModel.broilCookFilter
        localReheatCookFilter = homeViewModel.reheatCookFilter
        localBakeCookFilter = homeViewModel.bakeCookFilter

        localChickenFoodFilter = homeViewModel.chickenFoodFilter
        localBeefFoodFilter = homeViewModel.beefFoodFilter
        localPorkFoodFilter = homeViewModel.porkFoodFilter
        localFishFoodFilter = homeViewModel.fishFoodFilter
        localVeggieFoodFilter = homeViewModel.veggieFoodFilter
        localBreadFoodFilter = homeViewModel.breadFoodFilter
        localFruitFoodFilter = homeViewModel.fruitFoodFilter
        localLambFoodFilter = homeViewModel.lambFoodFilter
        localOtherFoodFilter = homeViewModel.otherFoodFilter

        localFifteenMinBox = homeViewModel.fifteenMinBox
        localFifteenToThirtyMinBox = homeViewModel.fifteenToThirtyMinBox
        localThirtyToSixtyMinBox = homeViewModel.thirtyToSixtyMinBox
        localSixtyToOneTwentyMinBox = homeViewModel.sixtyToOneTwentyMinBox
        localOneTwentyPlusBox = homeViewModel.oneTwentyPlusBox
    }

    private fun saveState() {
        homeViewModel.foodFilter = localFoodFilter
        homeViewModel.cookFilter = localCookFilter
        homeViewModel.timeFilter = localTimeFilter
        homeViewModel.smokeFlavorFilter = localSmokeFlavorFilter

        homeViewModel.grillCookFilter = localGrillCookFilter
        homeViewModel.smokeCookFilter = localSmokeCookFilter
        homeViewModel.airCrispCookFilter = localAirCrispCookFilter
        homeViewModel.roastCookFilter = localRoastCookFilter
        homeViewModel.dehydrateCookFilter = localDehydrateCookFilter
        homeViewModel.broilCookFilter = localBroilCookFilter
        homeViewModel.reheatCookFilter = localReheatCookFilter
        homeViewModel.bakeCookFilter = localBakeCookFilter

        homeViewModel.chickenFoodFilter = localChickenFoodFilter
        homeViewModel.beefFoodFilter = localBeefFoodFilter
        homeViewModel.porkFoodFilter = localPorkFoodFilter
        homeViewModel.fishFoodFilter = localFishFoodFilter
        homeViewModel.veggieFoodFilter = localVeggieFoodFilter
        homeViewModel.breadFoodFilter = localBreadFoodFilter
        homeViewModel.fruitFoodFilter = localFruitFoodFilter
        homeViewModel.lambFoodFilter = localLambFoodFilter
        homeViewModel.otherFoodFilter = localOtherFoodFilter

        homeViewModel.fifteenMinBox = localFifteenMinBox
        homeViewModel.fifteenToThirtyMinBox = localFifteenToThirtyMinBox
        homeViewModel.thirtyToSixtyMinBox = localThirtyToSixtyMinBox
        homeViewModel.sixtyToOneTwentyMinBox = localSixtyToOneTwentyMinBox
        homeViewModel.oneTwentyPlusBox = localOneTwentyPlusBox

        homeViewModel.filterCharts()
    }

    private fun initTimeSelector() {
        if (homeViewModel.fifteenMinBox) {
            binding.fifteenMinBox.isChecked = true
        }
        if (homeViewModel.fifteenToThirtyMinBox) {
            binding.fifteenToThirtyMinBox.isChecked = true
        }
        if (homeViewModel.thirtyToSixtyMinBox) {
            binding.thirtyToSixtyMinBox.isChecked = true
        }
        if (homeViewModel.sixtyToOneTwentyMinBox) {
            binding.sixtyToOneTwentyMinBox.isChecked = true
        }
        if (homeViewModel.oneTwentyPlusBox) {
            binding.oneTwentyPlusBox.isChecked = true
        }

        calculateTimeFilter()

        binding.fifteenMinBox.setOnClickListener {
            if (localFifteenMinBox) {
                binding.fifteenMinBox.isChecked = false
                localFifteenMinBox = false
                calculateTimeFilter()
            } else {
                localFifteenMinBox = true
                binding.fifteenMinBox.isChecked = true
                calculateTimeFilter()
            }

            filterCharts()
        }

        binding.fifteenToThirtyMinBox.setOnClickListener {
            if (localFifteenToThirtyMinBox) {
                binding.fifteenToThirtyMinBox.isChecked = false
                localFifteenToThirtyMinBox = false
                calculateTimeFilter()
            } else {
                localFifteenToThirtyMinBox = true
                binding.fifteenToThirtyMinBox.isChecked = true
                calculateTimeFilter()
            }

            filterCharts()
        }

        binding.thirtyToSixtyMinBox.setOnClickListener {
            if (localThirtyToSixtyMinBox) {
                binding.thirtyToSixtyMinBox.isChecked = false
                localThirtyToSixtyMinBox = false
                calculateTimeFilter()
            } else {
                localThirtyToSixtyMinBox = true
                binding.thirtyToSixtyMinBox.isChecked = true
                calculateTimeFilter()
            }

            filterCharts()
        }

        binding.sixtyToOneTwentyMinBox.setOnClickListener {
            if (localSixtyToOneTwentyMinBox) {
                binding.sixtyToOneTwentyMinBox.isChecked = false
                localSixtyToOneTwentyMinBox = false
                calculateTimeFilter()
            } else {
                localSixtyToOneTwentyMinBox = true
                binding.sixtyToOneTwentyMinBox.isChecked = true
                calculateTimeFilter()
            }

            filterCharts()
        }

        binding.oneTwentyPlusBox.setOnClickListener {
            if (localOneTwentyPlusBox) {
                binding.oneTwentyPlusBox.isChecked = false
                localOneTwentyPlusBox = false
                calculateTimeFilter()
            } else {
                localOneTwentyPlusBox = true
                binding.oneTwentyPlusBox.isChecked = true
                calculateTimeFilter()
            }

            filterCharts()
        }
    }

    private fun calculateLocalFoodFilter() {
        localFoodFilter = (localChickenFoodFilter || localBeefFoodFilter || localPorkFoodFilter ||
                localFishFoodFilter || localVeggieFoodFilter || localBreadFoodFilter ||
                localLambFoodFilter || localFruitFoodFilter || localOtherFoodFilter)
    }

    private fun calculateLocalCookFilter() {
        localCookFilter = (localGrillCookFilter || localSmokeCookFilter ||
                localAirCrispCookFilter || localRoastCookFilter || localBakeCookFilter||
                localBroilCookFilter || localDehydrateCookFilter)
    }

    private fun calculateLocalAnyFilters() {
        localAnyFilters = (localTimeFilter || localCookFilter || localFoodFilter || localSmokeFlavorFilter)
    }

    private fun calculateTimeFilter() {
        localTimeFilter = (localFifteenMinBox ||
            localFifteenToThirtyMinBox ||
            localThirtyToSixtyMinBox ||
            localSixtyToOneTwentyMinBox ||
            localOneTwentyPlusBox)
    }

    private fun filterCharts() {
        calculateLocalCookFilter()
        calculateLocalFoodFilter()
        calculateLocalAnyFilters()
        calculateTimeFilter()
        filteredCharts = mutableListOf<CookingCard>()

        if (localAnyFilters) {
            var filteredByMeatCharts = mutableListOf<CookingCard>()
            var filteredByCookingTypeCharts = mutableListOf<CookingCard>()
            var filteredByTimeCharts = mutableListOf<CookingCard>()
            var filteredBySmokeCharts = mutableListOf<CookingCard>()

            if (localFoodFilter) {
                for (chart in allCharts) {
                    if (localChickenFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.chicken) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (localBeefFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.beef) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (localPorkFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.pork) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (localFishFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.seafood) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (localVeggieFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.veggies) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (localBreadFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.bread) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (localFruitFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.fruit) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (localLambFoodFilter) {
                        if (chart.meatType.lowercase() == FoodTypeStrings.lamb) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                    if (localOtherFoodFilter) {
                        if (homeViewModel.isOtherFoodType(chart.meatType.lowercase())) {
                            filteredByMeatCharts.add(chart)
                        }
                    }
                }
            } else {
                filteredByMeatCharts = allCharts
            }

            if (localCookFilter) {
                for (chart in filteredByMeatCharts) {
                    if (localGrillCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.grill) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (localSmokeCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.smoke) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (localAirCrispCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.airCrisp) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (localRoastCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.roast) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (localBakeCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.bake) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (localBroilCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.broil) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (localDehydrateCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.dehydrate) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                    if (localReheatCookFilter) {
                        if (chart.cookingMethod.lowercase() == CookTypeStrings.reheat) {
                            filteredByCookingTypeCharts.add(chart)
                        }
                    }
                }
            } else {
                filteredByCookingTypeCharts = filteredByMeatCharts
            }

            if (localTimeFilter) {
                for (chart in filteredByCookingTypeCharts) {
                    if (localFifteenMinBox) {
                        if (chart.cookTime.toInt() < 900) {
                            filteredByTimeCharts.add(chart)
                        }
                    }
                    if (localFifteenToThirtyMinBox) {
                        if (chart.cookTime.toInt() in 900..1799) {
                            filteredByTimeCharts.add(chart)
                        }
                    }
                    if (localThirtyToSixtyMinBox) {
                        if (chart.cookTime.toInt() in 1800..3599) {
                            filteredByTimeCharts.add(chart)
                        }
                    }
                    if (localSixtyToOneTwentyMinBox) {
                        if (chart.cookTime.toInt() in 3600..7199) {
                            filteredByTimeCharts.add(chart)
                        }
                    }
                    if (localOneTwentyPlusBox) {
                        if (chart.cookTime.toInt() >= 7200) {
                            filteredByTimeCharts.add(chart)
                        }
                    }
                }
            } else {
                filteredByTimeCharts = filteredByCookingTypeCharts
            }

            if (localSmokeFlavorFilter) {
                for (chart in filteredByTimeCharts) {
                    if (chart.cookMode.toString().lowercase() != CookTypeStrings.broil) {
                        filteredBySmokeCharts.add(chart)
                    }
                }
            } else {
                filteredBySmokeCharts = filteredByTimeCharts
            }

            filteredCharts = filteredBySmokeCharts

            if (filteredCharts.size > 0) {
                binding.viewResultsButtonText.text = getString(R.string.charts_filters_view_results, filteredCharts.size.toString())
                binding.viewResultsButton.isEnabled = true
                binding.viewResultsButton.setBackgroundResource(R.drawable.rounded_edges_ninja_green_25dp)
            } else {
                binding.viewResultsButtonText.text = getString(R.string.charts_filters_view_results, "0")
                binding.viewResultsButton.isEnabled = false
                binding.viewResultsButton.setBackgroundResource(R.drawable.rounded_edges_disabled_25dp)
            }

        } else {
            binding.viewResultsButtonText.text = getString(R.string.charts_filters_view_results, "0")
            binding.viewResultsButton.isEnabled = false
            binding.viewResultsButton.setBackgroundResource(R.drawable.rounded_edges_disabled_25dp)
        }
    }

    private fun setInitialCookModeSelectorStates() {
        if (homeViewModel.grillCookFilter) {
            binding.grillIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.grillIconImage.setBackgroundResource(R.drawable.ic_grill_selected)
            binding.grillLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.smokeCookFilter) {
            binding.smokeIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.smokeIconImage.setBackgroundResource(R.drawable.ic_explore_smoke_selected)
            binding.smokeLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.airCrispCookFilter) {
            binding.aircrispIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.aircrispIconImage.setBackgroundResource(R.drawable.ic_aircrisp_selected)
            binding.aircrispLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.roastCookFilter) {
            binding.roastIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.roastIconImage.setBackgroundResource(R.drawable.ic_explore_roast_selected)
            binding.roastLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.dehydrateCookFilter) {
            binding.dehydrateIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.dehydrateIconImage.setBackgroundResource(R.drawable.ic_explore_dehydrate_selected)
            binding.dehydrateLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.broilCookFilter) {
            binding.broilIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.broilIconImage.setBackgroundResource(R.drawable.ic_explore_broil_selected)
            binding.broilLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.reheatCookFilter) {
            binding.reheatIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.reheatIconImage.setBackgroundResource(R.drawable.ic_explore_reheat_selected)
            binding.reheatLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.bakeCookFilter) {
            binding.bakeIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.bakeIconImage.setBackgroundResource(R.drawable.ic_explore_roast_selected)
            binding.bakeLabel.setTextColor(resources.getColor(R.color.black1))
        }
    }

    private fun setInitialFoodSelectorStates() {
        if (homeViewModel.chickenFoodFilter) {
            binding.poultryIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.poultryIconImage.setBackgroundResource(R.drawable.ic_explore_poultry_selected)
            binding.poultryLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.beefFoodFilter) {
            binding.beefIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.beefIconImage.setBackgroundResource(R.drawable.ic_explore_beef_selected)
            binding.beefLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.porkFoodFilter) {
            binding.porkIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.porkIconImage.setBackgroundResource(R.drawable.ic_explore_pork_selected)
            binding.porkLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.fishFoodFilter) {
            binding.seafoodIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.seafoodIconImage.setBackgroundResource(R.drawable.ic_explore_seafood_selected)
            binding.seafoodLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.veggieFoodFilter) {
            binding.veggiesIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.veggiesIconImage.setBackgroundResource(R.drawable.ic_explore_carrot_selected)
            binding.veggiesLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.breadFoodFilter) {
            binding.breadIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.breadIconImage.setBackgroundResource(R.drawable.ic_explore_bread_selected)
            binding.breadLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.lambFoodFilter) {
            binding.lambIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.lambIconImage.setBackgroundResource(R.drawable.ic_explore_lamb_selected)
            binding.lambLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.fruitFoodFilter) {
            binding.fruitIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.fruitIconImage.setBackgroundResource(R.drawable.ic_explore_fruit_selected)
            binding.fruitLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.otherFoodFilter) {
            binding.otherIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.otherIconImage.setBackgroundResource(R.drawable.ic_explore_other_selected)
            binding.otherLabel.setTextColor(resources.getColor(R.color.black1))
        }
    }

    private fun initCookModeSelector() {
        calculateSpacingForHorizontalModeSelector()
        setInitialCookModeSelectorStates()

        binding.grillIcon.setOnClickListener {
            if (localGrillCookFilter) {
                localGrillCookFilter = false

                binding.grillIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.grillIconImage.setBackgroundResource(R.drawable.ic_grill)
                binding.grillLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localGrillCookFilter = true

                binding.grillIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.grillIconImage.setBackgroundResource(R.drawable.ic_grill_selected)
                binding.grillLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }

        binding.smokeIcon.setOnClickListener {
            if (localSmokeCookFilter) {
                localSmokeCookFilter = false

                binding.smokeIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.smokeIconImage.setBackgroundResource(R.drawable.ic_smoke)
                binding.smokeLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localSmokeCookFilter = true

                binding.smokeIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.smokeIconImage.setBackgroundResource(R.drawable.ic_explore_smoke_selected)
                binding.smokeLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }

        binding.aircrispIcon.setOnClickListener {
            if (localAirCrispCookFilter) {
                localAirCrispCookFilter = false

                binding.aircrispIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.aircrispIconImage.setBackgroundResource(R.drawable.ic_aircrisp)
                binding.aircrispLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localAirCrispCookFilter = true

                binding.aircrispIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.aircrispIconImage.setBackgroundResource(R.drawable.ic_aircrisp_selected)
                binding.aircrispLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }

        binding.roastIcon.setOnClickListener {
            if (localRoastCookFilter) {
                localRoastCookFilter = false

                binding.roastIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.roastIconImage.setBackgroundResource(R.drawable.ic_roast)
                binding.roastLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localRoastCookFilter = true

                binding.roastIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.roastIconImage.setBackgroundResource(R.drawable.ic_explore_roast_selected)
                binding.roastLabel.setTextColor(resources.getColor(R.color.black1))
            }

            homeViewModel.filterCharts()
        }

        binding.bakeIcon.setOnClickListener {
            if (localBakeCookFilter) {
                localBakeCookFilter = false

                binding.bakeIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.bakeIconImage.setBackgroundResource(R.drawable.bake)
                binding.bakeLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localBakeCookFilter = true

                binding.bakeIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.bakeIconImage.setBackgroundResource(R.drawable.ic_explore_bake_selected)
                binding.bakeLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }

        binding.broilIcon.setOnClickListener {
            if (localBroilCookFilter) {
                localBroilCookFilter = false

                binding.broilIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.broilIconImage.setBackgroundResource(R.drawable.ic_broil)
                binding.broilLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localBroilCookFilter = true

                binding.broilIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.broilIconImage.setBackgroundResource(R.drawable.ic_explore_broil_selected)
                binding.broilLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }

        binding.reheatIcon.setOnClickListener {
            if (localReheatCookFilter) {
                localReheatCookFilter = false

                binding.reheatIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.reheatIconImage.setBackgroundResource(R.drawable.ic_reheat)
                binding.reheatLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localReheatCookFilter = true

                binding.reheatIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.reheatIconImage.setBackgroundResource(R.drawable.ic_explore_reheat_selected)
                binding.reheatLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }

        binding.dehydrateIcon.setOnClickListener {
            if (localDehydrateCookFilter) {
                localDehydrateCookFilter = false

                binding.dehydrateIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.dehydrateIconImage.setBackgroundResource(R.drawable.ic_dehydrate)
                binding.dehydrateLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localDehydrateCookFilter = true

                binding.dehydrateIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.dehydrateIconImage.setBackgroundResource(R.drawable.ic_explore_dehydrate_selected)
                binding.dehydrateLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }

        disableModesWithNoCharts()
        hideShowModesBasedOnRegion()
    }

    private fun hideShowModesBasedOnRegion() {
        if (homeViewModel.isUSGrill()) {
            binding.reheatIcon.visibility = View.GONE
            binding.reheatLabel.visibility = View.GONE
            binding.reheatIconImage.visibility = View.GONE
            binding.reheatContainer.visibility = View.GONE

            binding.spacerSeven.visibility = View.GONE
        } else {
            binding.broilIcon.visibility = View.GONE
            binding.broilLabel.visibility = View.GONE
            binding.broilIconImage.visibility = View.GONE
            binding.broilContainer.visibility = View.GONE

            binding.spacerSix.visibility = View.GONE
        }
    }

    private fun disableModesWithNoCharts() {
        if (!homeViewModel.checkIfModeHasChart(CookMode.Grill)) {
            binding.grillIcon.isEnabled = false
            binding.grillIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.grillIconImage.setBackgroundResource(R.drawable.ic_grill_selected)
            binding.grillLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Smoke)) {
            binding.smokeIcon.isEnabled = false
            binding.smokeIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.smokeIconImage.setBackgroundResource(R.drawable.ic_explore_smoke_selected)
            binding.smokeLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.AirCrisp)) {
            binding.aircrispIcon.isEnabled = false
            binding.aircrispIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.aircrispIconImage.setBackgroundResource(R.drawable.ic_aircrisp_selected)
            binding.aircrispLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Roast)) {
            binding.roastIcon.isEnabled = false
            binding.roastIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.roastIconImage.setBackgroundResource(R.drawable.ic_explore_roast_selected)
            binding.roastLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Bake)) {
            binding.bakeIcon.isEnabled = false
            binding.bakeIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.bakeIconImage.setBackgroundResource(R.drawable.ic_explore_bake_selected)
            binding.bakeLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Broil)) {
            binding.broilIcon.isEnabled = false
            binding.broilIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.broilIconImage.setBackgroundResource(R.drawable.ic_explore_broil_selected)
            binding.broilLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Reheat)) {
            binding.reheatIcon.isEnabled = false
            binding.reheatIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.reheatIconImage.setBackgroundResource(R.drawable.ic_explore_reheat_selected)
            binding.reheatLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Dehydrate)) {
            binding.dehydrateIcon.isEnabled = false
            binding.dehydrateIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.dehydrateIconImage.setBackgroundResource(R.drawable.ic_explore_dehydrate_selected)
            binding.dehydrateLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
    }

    private fun initFoodSelector() {
        setInitialFoodSelectorStates()

        binding.poultryIcon.setOnClickListener {
            if (localChickenFoodFilter) {
                localChickenFoodFilter = false

                binding.poultryIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.poultryIconImage.setBackgroundResource(R.drawable.ic_explore_poultry_unselected)
                binding.poultryLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localChickenFoodFilter = true

                binding.poultryIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.poultryIconImage.setBackgroundResource(R.drawable.ic_explore_poultry_selected)
                binding.poultryLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }
        binding.beefIcon.setOnClickListener {
            if (localBeefFoodFilter) {
                localBeefFoodFilter = false

                binding.beefIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.beefIconImage.setBackgroundResource(R.drawable.ic_explore_beef_unselected)
                binding.beefLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localBeefFoodFilter = true

                binding.beefIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.beefIconImage.setBackgroundResource(R.drawable.ic_explore_beef_selected)
                binding.beefLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }
        binding.porkIcon.setOnClickListener {
            if (localPorkFoodFilter) {
                localPorkFoodFilter = false

                binding.porkIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.porkIconImage.setBackgroundResource(R.drawable.ic_explore_pork_unselected)
                binding.porkLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localPorkFoodFilter = true

                binding.porkIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.porkIconImage.setBackgroundResource(R.drawable.ic_explore_pork_selected)
                binding.porkLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }
        binding.seafoodIcon.setOnClickListener {
            if (localFishFoodFilter) {
                localFishFoodFilter = false

                binding.seafoodIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.seafoodIconImage.setBackgroundResource(R.drawable.ic_explore_seafood_unselected)
                binding.seafoodLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localFishFoodFilter = true

                binding.seafoodIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.seafoodIconImage.setBackgroundResource(R.drawable.ic_explore_seafood_selected)
                binding.seafoodLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }
        binding.veggiesIcon.setOnClickListener {
            if (localVeggieFoodFilter) {
                localVeggieFoodFilter = false

                binding.veggiesIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.veggiesIconImage.setBackgroundResource(R.drawable.ic_explore_carrot_unselected)
                binding.veggiesLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localVeggieFoodFilter = true

                binding.veggiesIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.veggiesIconImage.setBackgroundResource(R.drawable.ic_explore_carrot_selected)
                binding.veggiesLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }
        binding.breadIcon.setOnClickListener {
            if (localBreadFoodFilter) {
                localBreadFoodFilter = false

                binding.breadIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.breadIconImage.setBackgroundResource(R.drawable.ic_explore_cheese_unselected)
                binding.breadLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localBreadFoodFilter = true

                binding.breadIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.breadIconImage.setBackgroundResource(R.drawable.ic_explore_cheese_selected)
                binding.breadLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }
        binding.fruitIcon.setOnClickListener {
            if (localFruitFoodFilter) {
                localFruitFoodFilter = false

                binding.fruitIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.fruitIconImage.setBackgroundResource(R.drawable.ic_explore_fruit_unselected)
                binding.fruitLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localFruitFoodFilter = true

                binding.fruitIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.fruitIconImage.setBackgroundResource(R.drawable.ic_explore_fruit_selected)
                binding.fruitLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }
        binding.lambIcon.setOnClickListener {
            if (localLambFoodFilter) {
                localLambFoodFilter = false

                binding.lambIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.lambIconImage.setBackgroundResource(R.drawable.ic_explore_lamb_unselected)
                binding.lambLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localLambFoodFilter = true

                binding.lambIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.lambIconImage.setBackgroundResource(R.drawable.ic_explore_lamb_selected)
                binding.lambLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }
        binding.otherIcon.setOnClickListener {
            if (localOtherFoodFilter) {
                localOtherFoodFilter = false

                binding.otherIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.otherIconImage.setBackgroundResource(R.drawable.ic_explore_other_unselected)
                binding.otherLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localOtherFoodFilter = true

                binding.otherIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.otherIconImage.setBackgroundResource(R.drawable.ic_explore_other_selected)
                binding.otherLabel.setTextColor(resources.getColor(R.color.black1))
            }

            filterCharts()
        }
    }

    private fun calculateSpacingForHorizontalModeSelector() {
        val numberOfButtonsTotal = 9 //keep this current
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
        binding.spacerOne.layoutParams.width = size.toInt()
        binding.spacerTwo.layoutParams.width = size.toInt()
        binding.spacerThree.layoutParams.width = size.toInt()
        binding.spacerFour.layoutParams.width = size.toInt()
        binding.spacerFive.layoutParams.width = size.toInt()
        binding.spacerSix.layoutParams.width = size.toInt()
        binding.spacerSeven.layoutParams.width = size.toInt()

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