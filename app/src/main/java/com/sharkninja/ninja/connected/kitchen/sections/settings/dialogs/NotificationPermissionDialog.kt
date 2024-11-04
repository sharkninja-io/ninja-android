package com.sharkninja.ninja.connected.kitchen.sections.settings.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.sharkninja.ninja.connected.kitchen.databinding.DialogNotificationPermissionBinding
import com.sharkninja.ninja.connected.kitchen.ext.toPx


class NotificationPermissionDialog : BaseDialogFragment() {

    private lateinit var binding: DialogNotificationPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNotificationPermissionBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.cancelButton.setOnClickListener { this.dismiss() }
        binding.settingsButton.setOnClickListener {
            goToSettings()
            this.dismiss()
        }
    }

    private fun goToSettings() {
            startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName))
    }
}