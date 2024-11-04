package com.sharkninja.ninja.connected.kitchen.sections.authentication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentConfirmCodeBinding
import com.sharkninja.ninja.connected.kitchen.ext.show
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModel
import com.sharkninja.ninja.connected.kitchen.ui.animators.OpacityAnimator


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ConfirmCodeFragment : Fragment() {

    private lateinit var binding: FragmentConfirmCodeBinding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmCodeBinding.inflate(layoutInflater, container, false)

        initBindings()
        return binding.root
    }

    private fun initBindings() {

        binding.codeResend.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                authenticationViewModel.requestPasswordReset().flowOn(Dispatchers.IO).collect {
                    when (it) {
                        is Unit -> {
                            withContext(Dispatchers.Main) {
                                binding.codeConfirmationMessage.show()
                                OpacityAnimator.fadeInOut(binding.codeConfirmationMessage, 3000)
                            }
                        }
                        is Throwable -> {
                            Log.e("ConfirmCode", it.message, it)
                        }
                    }
                }
            }
        }
    }
}