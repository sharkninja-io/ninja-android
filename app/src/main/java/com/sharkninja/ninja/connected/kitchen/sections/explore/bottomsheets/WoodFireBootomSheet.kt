package com.sharkninja.ninja.connected.kitchen.sections.explore.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sharkninja.ninja.connected.kitchen.databinding.WoodFireBottomSheetBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class WoodFireBootomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: WoodFireBottomSheetBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private var localIsChecked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = WoodFireBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBindings()
    }

    private fun initBindings() {
        localIsChecked = homeViewModel.smokeFlavorFilter
        if (homeViewModel.smokeFlavorFilter) {
            binding.woodfireToggle.toggle()
        }
        binding.woodfireToggle.setOnCheckedChangeListener { _, isChecked ->
            localIsChecked = isChecked
        }

        binding.applyFiltersButton.setOnClickListener {
            saveState()
            dismiss()
        }
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
        homeViewModel.smokeFlavorFilter = localIsChecked
    }

    companion object {
        const val TAG = "Woodfire Bottom Sheet"
    }
}