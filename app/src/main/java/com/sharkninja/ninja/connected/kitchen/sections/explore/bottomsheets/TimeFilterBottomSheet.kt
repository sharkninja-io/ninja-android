package com.sharkninja.ninja.connected.kitchen.sections.explore.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sharkninja.ninja.connected.kitchen.databinding.TimeFilterBottomSheetBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class TimeFilterBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: TimeFilterBottomSheetBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    var localFifteenMinBox = false
    var localFifteenToThirtyMinBox = false
    var localThirtyToSixtyMinBox = false
    var localSixtyToOneTwentyMinBox = false
    var localOneTwentyPlusBox = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TimeFilterBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBindings()
    }

    private var onDismissFunction: () -> Unit = {}
    fun setOnDismissFunction(block: () -> Unit) {
        onDismissFunction = block
    }
    override fun onDismiss(dialog: DialogInterface) {
        onDismissFunction()
        super.onDismiss(dialog)
    }

    private fun initBindings() {
        getViewModelState()
        initTimeSelector()

        binding.applyFiltersButton.setOnClickListener {
            saveState()
            dismiss()
        }
    }

    private fun getViewModelState() {
        localFifteenMinBox = homeViewModel.fifteenMinBox
        localFifteenToThirtyMinBox = homeViewModel.fifteenToThirtyMinBox
        localThirtyToSixtyMinBox = homeViewModel.thirtyToSixtyMinBox
        localSixtyToOneTwentyMinBox = homeViewModel.sixtyToOneTwentyMinBox
        localOneTwentyPlusBox = homeViewModel.oneTwentyPlusBox
    }

    private fun saveState() {
        homeViewModel.fifteenMinBox = localFifteenMinBox
        homeViewModel.fifteenToThirtyMinBox = localFifteenToThirtyMinBox
        homeViewModel.thirtyToSixtyMinBox = localThirtyToSixtyMinBox
        homeViewModel.sixtyToOneTwentyMinBox = localSixtyToOneTwentyMinBox
        homeViewModel.oneTwentyPlusBox = localOneTwentyPlusBox
    }

    private fun initTimeSelector() {
        binding.fifteenMinBox.isChecked = homeViewModel.fifteenMinBox
        binding.fifteenToThirtyMinBox.isChecked = homeViewModel.fifteenToThirtyMinBox
        binding.thirtyToSixtyMinBox.isChecked = homeViewModel.thirtyToSixtyMinBox
        binding.sixtyToOneTwentyMinBox.isChecked = homeViewModel.sixtyToOneTwentyMinBox
        binding.oneTwentyPlusBox.isChecked = homeViewModel.oneTwentyPlusBox

        binding.fifteenMinBox.setOnClickListener {
            if (localFifteenMinBox) {
                binding.fifteenMinBox.isChecked = false
                localFifteenMinBox = false
            } else {
                localFifteenMinBox = true
                binding.fifteenMinBox.isChecked = true
            }
        }

        binding.fifteenToThirtyMinBox.setOnClickListener {
            if (localFifteenToThirtyMinBox) {
                binding.fifteenToThirtyMinBox.isChecked = false
                localFifteenToThirtyMinBox = false
            } else {
                localFifteenToThirtyMinBox = true
                binding.fifteenToThirtyMinBox.isChecked = true
            }
        }

        binding.thirtyToSixtyMinBox.setOnClickListener {
            if (localThirtyToSixtyMinBox) {
                binding.thirtyToSixtyMinBox.isChecked = false
                localThirtyToSixtyMinBox = false
            } else {
                localThirtyToSixtyMinBox = true
                binding.thirtyToSixtyMinBox.isChecked = true
            }
        }

        binding.sixtyToOneTwentyMinBox.setOnClickListener {
            if (localSixtyToOneTwentyMinBox) {
                binding.sixtyToOneTwentyMinBox.isChecked = false
                localSixtyToOneTwentyMinBox = false
            } else {
                localSixtyToOneTwentyMinBox = true
                binding.sixtyToOneTwentyMinBox.isChecked = true
            }
        }

        binding.oneTwentyPlusBox.setOnClickListener {
            if (localOneTwentyPlusBox) {
                binding.oneTwentyPlusBox.isChecked = false
                localOneTwentyPlusBox = false
            } else {
                localOneTwentyPlusBox = true
                binding.oneTwentyPlusBox.isChecked = true
            }
        }
    }

    private fun deselectAllTimes() {
        binding.fifteenMinBox.isChecked = false
        binding.fifteenToThirtyMinBox.isChecked = false
        binding.thirtyToSixtyMinBox.isChecked = false
        binding.sixtyToOneTwentyMinBox.isChecked = false
        binding.oneTwentyPlusBox.isChecked = false
    }

    companion object {
        const val TAG = "TimeFilterBottomSheet"
    }
}