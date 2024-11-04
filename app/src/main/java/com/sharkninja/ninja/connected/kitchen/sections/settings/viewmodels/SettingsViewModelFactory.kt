package com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.interfaces.CacheInterface
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SettingsInterface

class SettingsViewModelFactory(
    private val settings: SettingsInterface,
    private val cache: CacheInterface
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(
            SettingsInterface::class.java,
            CacheInterface::class.java
        ).newInstance(settings, cache)
    }
}