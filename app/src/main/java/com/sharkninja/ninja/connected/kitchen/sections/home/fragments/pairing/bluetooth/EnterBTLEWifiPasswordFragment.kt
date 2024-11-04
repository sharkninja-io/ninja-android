package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.os.Bundle
import android.text.method.SingleLineTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentEnterBtleWifiPasswordBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditText
import com.sharkninja.ninja.connected.kitchen.ui.components.fields.PasswordEditTextInterface

class EnterBTLEWifiPasswordFragment : Fragment(), PasswordEditTextInterface {

    private lateinit var binding: FragmentEnterBtleWifiPasswordBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterBtleWifiPasswordBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    private fun initBindings() {
        binding.enterWifiPasswordEditText.apply {
            placeholder = getString(R.string.type_network_pw)
            delegate = this@EnterBTLEWifiPasswordFragment
            isValidationEnabled = false
            showCriteriaBoxes = false
            enableToggleButton()
            editText.transformationMethod = SingleLineTransformationMethod()
            imageAssetButton.setImageResource(R.drawable.ic_icon_ico_eye)
        }

        binding.wifiNetworkName.text = homeViewModel.selectedUserWifiNetwork.value?.ssid

        binding.buttonConnect.setOnClickListener {
            homeViewModel.logPairingEvent("${EnterBTLEWifiPasswordFragment::class.java.simpleName}: Wifi password entered. Connect Button Clicked")
            homeViewModel.onConnectWifi(
                password = binding.enterWifiPasswordEditText.editText.text.toString()
            )
            findNavController().safeNavigate(EnterBTLEWifiPasswordFragmentDirections.actionEnterBTLEWifiPasswordFragmentToConnectToBTLEWifiFragment())
        }
    }

    override fun passwordEditTextDidChangedHandler(chars: CharSequence) {

    }

    override fun passwordStateDidChangeHandler(state: PasswordEditText.PasswordState) {

    }
}