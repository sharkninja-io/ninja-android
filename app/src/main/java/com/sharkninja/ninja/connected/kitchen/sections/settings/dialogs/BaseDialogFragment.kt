package com.sharkninja.ninja.connected.kitchen.sections.settings.dialogs


import androidx.fragment.app.DialogFragment
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.EcommerceProfileService
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SettingsService
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SignOutService
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModelFactory

open class BaseDialogFragment : DialogFragment() {

    val settingsFactory by lazy {
        SettingsViewModelFactory(
            SettingsService(
                requireActivity().applicationContext,
                CacheService(),
                SignOutService(requireActivity().applicationContext),
                EcommerceProfileService.createInstance(requireActivity().applicationContext)
            ),
            CacheService()
        )
    }
}