package com.sharkninja.ninja.connected.kitchen.sections.settings.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.SettingsEvent
import com.sharkninja.ninja.connected.kitchen.databinding.DialogSignOutBinding
import com.sharkninja.ninja.connected.kitchen.ext.toPx
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel


class SignOutDialog : BaseDialogFragment() {

    private lateinit var binding: DialogSignOutBinding
    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSignOutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // This sets the width of the dialog to fill the screen
        val params = dialog?.window?.attributes
        params?.width = LinearLayout.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 24.toPx())
        dialog?.window?.setBackgroundDrawable(inset)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.cancelButton.setOnClickListener { this.dismiss() }
        binding.confirmButton.setOnClickListener {
            dismiss()
            settingsViewModel.updateSettingsEvent(SettingsEvent.SignOut)
        }
    }
}