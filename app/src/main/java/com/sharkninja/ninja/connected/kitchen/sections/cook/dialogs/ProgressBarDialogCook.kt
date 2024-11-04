package com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.data.enums.CookDashboardInfoAction
import com.sharkninja.ninja.connected.kitchen.databinding.LayoutProgressBarDialogBinding
import com.sharkninja.ninja.connected.kitchen.ext.hasStoppedCook
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class ProgressBarDialogCook : DialogFragment() {

    private lateinit var binding: LayoutProgressBarDialogBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    private val timer by lazy {
        object: CountDownTimer(COUNT_DOWN_LENGTH, COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                homeViewModel.isNavigatingCookToPreCook = false
                dismissAllowingStateLoss()
            }
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutProgressBarDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.isNavigatingCookToPreCook = true
        subscribeToVM()
        timer.start()
    }

    override fun onResume() {
        super.onResume()
        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun subscribeToVM() {
        homeViewModel.selectedGrillState.onEach {
            if (it.state.hasStoppedCook()) {
                timer.cancel()
                homeViewModel.updateAction(CookDashboardInfoAction.ActionProcessed)
                findNavController().safeNavigate(ProgressBarDialogCookDirections.globalToCookFragment())
                dismissAllowingStateLoss()
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        homeViewModel.infoAction.onEach {
            if(it is CookDashboardInfoAction.StopCookError) {
                timer.cancel()
                dismissAllowingStateLoss()
                homeViewModel.updateAction(CookDashboardInfoAction.ActionProcessed)
                homeViewModel.isNavigatingCookToPreCook = false
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }
}