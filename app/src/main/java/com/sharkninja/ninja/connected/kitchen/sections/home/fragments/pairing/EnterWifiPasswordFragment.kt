package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing

import android.os.Bundle
import android.text.method.SingleLineTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentEnterWifiPasswordBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditText
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditTextInterface

class EnterWifiPasswordFragment : Fragment(), PasswordEditTextInterface {

    private lateinit var binding: FragmentEnterWifiPasswordBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterWifiPasswordBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.enterWifiPasswordEditText.apply {
            placeholder = "Type your network password"
            delegate = this@EnterWifiPasswordFragment
            isValidationEnabled = false
            showCriteriaBoxes = false
            enableToggleButton()
            editText.transformationMethod = SingleLineTransformationMethod()
            imageAssetButton.setImageResource(R.drawable.ic_icon_ico_eye)
        }

        binding.wifiNetworkName.text = homeViewModel.selectedUserWifiNetwork.value?.ssid

        binding.buttonConnect.setOnClickListener {
            homeViewModel.logPairingEvent("${EnterWifiPasswordFragment::class.java.simpleName}: Wifi password entered. Connect Button Clicked")
            homeViewModel.onConnectWifi(
                password = binding.enterWifiPasswordEditText.editText.text.toString()
            )
            findNavController().navigate(EnterWifiPasswordFragmentDirections.actionEnterWifiPasswordFragmentToConnectToWifiFragment())
        }
    }

    override fun passwordEditTextDidChangedHandler(chars: CharSequence) {

    }

    override fun passwordStateDidChangeHandler(state: PasswordEditText.PasswordState) {

    }
}