package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.DialogYourGrillBinding
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentBluetoothNameYourGrillBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.NameYourGrillFragmentDirections
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.delay

class BluetoothInternationalNameYourGrillFragment : Fragment() {
    private lateinit var binding: FragmentBluetoothNameYourGrillBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBluetoothNameYourGrillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    private fun initBindings() {

        binding.userText.setOnFocusChangeListener { view, b ->
            binding.boxUnclickedPencil.visibility = View.GONE
            binding.boxUnclickedCounter.visibility = View.GONE
            binding.charCounter.visibility = View.VISIBLE
        }

        binding.userText.doAfterTextChanged {
            binding.charCounter.text = getString(R.string.name_length_label, binding.userText.text.length.toString())
            binding.continueButton.text = if (it.isNullOrEmpty()) getString(R.string.name_your_grill_empty_continue_button) else getString(R.string.continue_button)
        }

        binding.continueButton.setOnClickListener {
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogYourGrillBinding.inflate(layoutInflater)
            dialogBinding.buttonGetStarted.visibility = View.GONE

            val grillName = if (binding.userText.text.toString().isEmpty())
                binding.userText.hint else binding.userText.text

            homeViewModel.setGrillName(grillName.toString())

            dialogBinding.nameConfirmedSub.text = getString(R.string.name_grill_confirm_text, grillName.toString())
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            dialog.setContentView(dialogBinding.root)

            dialog.show()

            lifecycleScope.launchWhenStarted {
                delay(3000)
                dialog.dismiss()
                findNavController().safeNavigate(BluetoothInternationalNameYourGrillFragmentDirections.actionBluetoothInternationalNameYourGrillFragmentToWifiInternationalOptionFragment())
            }
        }
    }
}