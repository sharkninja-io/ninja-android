package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.DialogYourGrillBinding
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentNameYourGrillBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

class NameYourGrillFragment : Fragment() {
    private lateinit var binding: FragmentNameYourGrillBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNameYourGrillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBindings()
    }

    private fun initBindings() {
        binding.grillNameContinue.setOnClickListener {
            val dialog = Dialog(requireContext())
            val dialogBinding = DialogYourGrillBinding.inflate(layoutInflater)
            val grillName = if (binding.grillNameEditText.text.toString().isEmpty())
                binding.grillNameEditText.hint else binding.grillNameEditText.text

            homeViewModel.setGrillName(grillName.toString())

            dialogBinding.nameConfirmedSub.text = getString(R.string.name_grill_confirm_text, grillName.toString())
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            dialog.setContentView(dialogBinding.root)
            dialogBinding.buttonGetStarted.setOnClickListener {
                dialog.dismiss()
                findNavController().navigate(R.id.global_to_home_fragment)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                homeViewModel.renameGrill(homeViewModel.newGrillDSN.value).flowOn(Dispatchers.IO).collect {
                    dialog.show()
                }
            }
         }
    }
}