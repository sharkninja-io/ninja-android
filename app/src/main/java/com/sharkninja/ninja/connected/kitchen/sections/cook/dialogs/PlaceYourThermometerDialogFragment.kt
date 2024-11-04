package com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.sharkninja.ninja.connected.kitchen.databinding.DialogPlaceYourThermometerBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class PlaceYourThermometerDialogFragment : DialogFragment() {

    private lateinit var binding: DialogPlaceYourThermometerBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPlaceYourThermometerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    private fun initBindings() {
        binding.placeThermometerCb.setOnCheckedChangeListener { _, b ->
            homeViewModel.setShouldShowPlaceYourThermometerDialog(b)
        }

        binding.continueButton.setOnClickListener {
            homeViewModel.setShouldShowPlaceYourThermometerDialog(binding.placeThermometerCb.isChecked.not())
            dismiss()
        }
    }
}