package com.sharkninja.ninja.connected.kitchen.sections.settings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentPreferencesBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.settings.viewmodels.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreferencesFragment : BaseSettingsFragment() {

    private val settingsViewModel: SettingsViewModel by navGraphViewModels(R.id.settings_graph) { settingsFactory }
    private lateinit var binding: FragmentPreferencesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPreferencesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initClickListeners()
        observeViewModel()
    }

    private fun initClickListeners() {
        binding.temperatureDropdownIcon.setOnClickListener {
            val directions = PreferencesFragmentDirections.actionPreferenceSelection(
                TEMPERATURE_UNIT_SELECTION
            )
            findNavController().safeNavigate(directions)
        }

        binding.weightDropdownIcon.setOnClickListener {
            val directions = PreferencesFragmentDirections.actionPreferenceSelection(
                WEIGHT_UNIT_SELECTION
            )
            findNavController().safeNavigate(directions)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    settingsViewModel.weightSelection.collectLatest {
                        withContext(Dispatchers.Main) {
                            binding.weightText.setText(it)
                        }
                    }
                }

//                launch {
//                    settingsViewModel.temperatureSelection.collectLatest {
//                        withContext(Dispatchers.Main) {
//                            binding.temperatureText.setText(it)
//                        }
//                    }
//                }
            }
        }
    }

    companion object {
        const val TEMPERATURE_UNIT_SELECTION = "temperatureUnitSelection"
        const val WEIGHT_UNIT_SELECTION = "weightUnitSelection"
    }


}