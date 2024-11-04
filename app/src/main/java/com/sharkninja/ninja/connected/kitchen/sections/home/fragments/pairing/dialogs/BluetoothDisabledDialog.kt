package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.sharkninja.ninja.connected.kitchen.databinding.DialogBluetoothDisabledBinding
import com.sharkninja.ninja.connected.kitchen.ext.toPx
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class BluetoothDisabledDialog : DialogFragment() {

    private lateinit var binding: DialogBluetoothDisabledBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBluetoothDisabledBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 24.toPx())
        dialog?.window?.setBackgroundDrawable(inset)

        if (homeViewModel.getBTAdapter()?.isEnabled == true) {
            homeViewModel.updateBluetoothEnabled(bluetoothEnabled = true)
            dismiss()
        }
    }

    private fun initBindings() {
        binding.button.setOnClickListener { openBluetoothSettings() }
    }

    private fun openBluetoothSettings() {
        startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
    }
}