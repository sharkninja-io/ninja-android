package com.sharkninja.ninja.connected.kitchen.sections.explore.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FoodSelectorBottomSheetBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class FoodSelectorBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: FoodSelectorBottomSheetBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FoodSelectorBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBindings()
    }

    private fun initBindings() {
        binding.applyFiltersButton.setOnClickListener {
            saveState()
            dismiss()
        }

        initFoodSelector()
    }

    private var onDismissFunction: () -> Unit = {}
    fun setOnDismissFunction(block: () -> Unit) {
        onDismissFunction = block
    }
    override fun onDismiss(dialog: DialogInterface) {
        onDismissFunction()
        super.onDismiss(dialog)
    }

    private fun saveState() {
        homeViewModel.chickenFoodFilter = localChickenFoodFilter
        homeViewModel.beefFoodFilter = localBeefFoodFilter
        homeViewModel.porkFoodFilter = localPorkFoodFilter
        homeViewModel.fishFoodFilter = localFishFoodFilter
        homeViewModel.veggieFoodFilter = localVeggieFoodFilter
        homeViewModel.breadFoodFilter = localBreadFoodFilter
        homeViewModel.fruitFoodFilter = localFruitFoodFilter
        homeViewModel.lambFoodFilter = localLambFoodFilter
        homeViewModel.otherFoodFilter = localOtherFoodFilter
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
        }
        binding.fruitIcon.setOnClickListener {
            if (localFruitFoodFilter) {
                localFruitFoodFilter = false

                binding.breadIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.breadIconImage.setBackgroundResource(R.drawable.ic_explore_cheese_unselected)
                binding.breadLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localBreadFoodFilter = true

                binding.breadIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.breadIconImage.setBackgroundResource(R.drawable.ic_explore_cheese_selected)
                binding.breadLabel.setTextColor(resources.getColor(R.color.black1))
            }
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
        }
    }

    private fun setInitialFoodSelectorStates() {
        localChickenFoodFilter = homeViewModel.chickenFoodFilter
        localBeefFoodFilter = homeViewModel.beefFoodFilter
        localPorkFoodFilter = homeViewModel.porkFoodFilter
        localFishFoodFilter = homeViewModel.fishFoodFilter
        localVeggieFoodFilter = homeViewModel.veggieFoodFilter
        localBreadFoodFilter = homeViewModel.breadFoodFilter
        localFruitFoodFilter = homeViewModel.fruitFoodFilter
        localLambFoodFilter = homeViewModel.lambFoodFilter

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
            binding.breadIconImage.setBackgroundResource(R.drawable.ic_explore_cheese_selected)
            binding.breadLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.fruitFoodFilter) {
            binding.fruitIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.fruitIconImage.setBackgroundResource(R.drawable.ic_explore_fruit_selected)
            binding.fruitLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.lambFoodFilter) {
            binding.lambIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.lambIconImage.setBackgroundResource(R.drawable.ic_explore_lamb_selected)
            binding.lambLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.otherFoodFilter) {
            binding.otherIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.otherIconImage.setBackgroundResource(R.drawable.ic_explore_other_selected)
            binding.otherLabel.setTextColor(resources.getColor(R.color.black1))
        }
    }

    companion object {
        const val TAG = "FoodSelectorBottomSheet"
    }
}