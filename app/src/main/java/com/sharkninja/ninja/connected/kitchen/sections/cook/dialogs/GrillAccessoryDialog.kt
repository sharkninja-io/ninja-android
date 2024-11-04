package com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardInfoAction
import com.sharkninja.ninja.connected.kitchen.data.enums.getMenuItem
import com.sharkninja.ninja.connected.kitchen.databinding.DialogGrillAccessoryBinding
import com.sharkninja.ninja.connected.kitchen.ext.logBreadCrumbClickEvent
import com.sharkninja.ninja.connected.kitchen.ext.toPx
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class GrillAccessoryDialog : DialogFragment() {

    private lateinit var binding: DialogGrillAccessoryBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogGrillAccessoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = LinearLayout.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 24.toPx())
        dialog?.window?.setBackgroundDrawable(inset)
    }

    private fun initBindings() {
        val cookModeAccessoryItem = homeViewModel.cookMode.value.getMenuItem().accessoryItem
        binding.dialogTitle.text = getString(cookModeAccessoryItem.title)
        binding.dialogDescription.text = getString(cookModeAccessoryItem.description)
        binding.allAccessoriesContainer.isVisible = cookModeAccessoryItem.allAccessories
        binding.trayAndPlateContainer.isVisible = cookModeAccessoryItem.allAccessories.not()

        binding.doneButton.setOnClickListener {
            dismissNow()
            logBreadCrumbClickEvent("START COOK")
            homeViewModel.updateAction(CookDashboardInfoAction.StartCook)
        }
    }
}