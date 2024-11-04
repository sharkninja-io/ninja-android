package com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.DialogDarkThemeBinding
import com.sharkninja.ninja.connected.kitchen.ext.toPx
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PlugInThermometer2Dialog: DialogFragment() {

    private lateinit var binding: DialogDarkThemeBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDarkThemeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
        subscribeToVm()
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
        isCancelable = false
        binding.dialogTitle.text = getString(R.string.dialog_thermometer_not_plugged_in_title)
        binding.dialogDescription.text = getString(R.string.dialog_thermometer_2_not_plugged_in_dashboard_description)
        binding.topButton.text = getString(R.string.dialog_thermometer_not_plugged_in_dashboard_button)

        binding.topButton.setOnClickListener {
            dismiss()
        }

        binding.bottomButton.visibility = View.GONE
    }

    private fun subscribeToVm() {
        homeViewModel.selectedGrillState.onEach {
            if(it.probe2.pluggedIn) {
                dismiss()
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }
}