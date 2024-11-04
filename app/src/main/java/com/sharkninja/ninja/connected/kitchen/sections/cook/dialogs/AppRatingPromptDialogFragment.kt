package com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.enums.SurveyAction
import com.sharkninja.ninja.connected.kitchen.databinding.DialogAppRatingPromptBinding
import com.sharkninja.ninja.connected.kitchen.ext.disable
import com.sharkninja.ninja.connected.kitchen.ext.enable
import com.sharkninja.ninja.connected.kitchen.ext.hide
import com.sharkninja.ninja.connected.kitchen.ext.show
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class AppRatingPromptDialogFragment : DialogFragment() {

    private lateinit var binding: DialogAppRatingPromptBinding
    //    private val mixpanel by lazy { context?.let { MixpanelWrapper.getInstance(it) } }
    private var selectedItem: String = NO_RATING
    private val homeViewModel: HomeViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAppRatingPromptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initBindings()
        homeViewModel.setAppRatingPromptHasShown()
    }

    override fun getTheme() = R.style.RoundedCornersDialog

    private fun initBindings() {
        with(binding) {
            groupToggleBtn.addOnButtonCheckedListener { group, checkedId, isChecked ->
                if (isChecked) {
                    btnSubmit.enable()
                    when (checkedId) {
                        btnThumbsUp.id -> {
                            updateItemSelected(HAPPY_RATING)
                        }
                        btnThumbsDown.id -> {
                            updateItemSelected(SAD_RATING)
                        }
                    }
                } else {
                    if (groupToggleBtn.checkedButtonId == View.NO_ID) {
                        updateItemSelected(NO_RATING)
                        binding.btnSubmit.disable()
                    }
                }

                binding.btnSubmit.setOnClickListener { onSubmit() }
            }
        }
    }

    private fun updateItemSelected(item: String) {
        selectedItem = item
    }


    private fun onSubmit() {
        if (selectedItem == SAD_RATING) {
//            mixpanel?.logAppRating(SAD_RATING)
            showSadPrompt()
        } else if (selectedItem == HAPPY_RATING) {
//            mixpanel?.logAppRating(HAPPY_RATING)
            homeViewModel.updateSurveyAction(SurveyAction.AppRatingPositiveAction)
        }
    }

    private fun showSadPrompt() {
        binding.appRatingPrompt.hide()
        binding.containerSad.show()
        binding.btnOk.setOnClickListener {
            homeViewModel.updateSurveyAction(SurveyAction.AppRatingNegativeAction)
            dismiss()
        }
        binding.btnDismiss.setOnClickListener { dismiss() }
    }

    companion object {
        const val TAG = "AppRatingDialog"
        private const val PACKAGE_NAME = "com.sharkninja.ninja.connected.kitchen"
        const val URI = "market://details?id=${PACKAGE_NAME}"
        const val FULL_URI =
            "https://play.google.com/store/apps/details?id=${PACKAGE_NAME}"
        const val SAD_RATING = "thumbs down"
        const val HAPPY_RATING = "thumbs up"
        const val NO_RATING = ""
    }
}