package com.sharkninja.ninja.connected.kitchen.sections.explore.bottomsheets

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sharkninja.grillcore.CookMode
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.databinding.CookModeSelectorBottomSheetBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel

class CookModeSelectorBottomSheet: BottomSheetDialogFragment() {
    private lateinit var binding: CookModeSelectorBottomSheetBinding
    private val homeViewModel: HomeViewModel by activityViewModels()

    var localGrillCookFilter = false
    var localSmokeCookFilter = false
    var localAirCrispCookFilter = false
    var localRoastCookFilter = false
    var localBakeCookFilter = false
    var localBroilCookFilter = false
    var localReheatCookFilter = false
    var localDehydrateCookFilter = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CookModeSelectorBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var onDismissFunction: () -> Unit = {}
    fun setOnDismissFunction(block: () -> Unit) {
        onDismissFunction = block
    }
    override fun onDismiss(dialog: DialogInterface) {
        onDismissFunction()
        super.onDismiss(dialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initBindings()
    }

    private fun initBindings() {
        initCookModeSelector()

        binding.applyFiltersButton.setOnClickListener {
            saveState()
            dismiss()
        }
    }

    private fun saveState() {
        homeViewModel.grillCookFilter = localGrillCookFilter
        homeViewModel.smokeCookFilter = localSmokeCookFilter
        homeViewModel.airCrispCookFilter = localAirCrispCookFilter
        homeViewModel.roastCookFilter = localRoastCookFilter
        homeViewModel.dehydrateCookFilter = localDehydrateCookFilter
        homeViewModel.broilCookFilter = localBroilCookFilter
        homeViewModel.bakeCookFilter = localBakeCookFilter
    }

    private fun initCookModeSelector() {
        setInitialCookModeSelectorStates()

        binding.grillIcon.setOnClickListener {
            if (localGrillCookFilter) {
                localGrillCookFilter = false

                binding.grillIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.grillIconImage.setBackgroundResource(R.drawable.ic_grill)
                binding.grillLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localGrillCookFilter = true

                binding.grillIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.grillIconImage.setBackgroundResource(R.drawable.ic_grill_selected)
                binding.grillLabel.setTextColor(resources.getColor(R.color.black1))
            }
        }

        binding.smokeIcon.setOnClickListener {
            if (localSmokeCookFilter) {
                localSmokeCookFilter = false

                binding.smokeIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.smokeIconImage.setBackgroundResource(R.drawable.ic_smoke)
                binding.smokeLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localSmokeCookFilter = true

                binding.smokeIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.smokeIconImage.setBackgroundResource(R.drawable.ic_explore_smoke_selected)
                binding.smokeLabel.setTextColor(resources.getColor(R.color.black1))
            }
        }

        binding.aircrispIcon.setOnClickListener {
            if (localAirCrispCookFilter) {
                localAirCrispCookFilter = false

                binding.aircrispIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.aircrispIconImage.setBackgroundResource(R.drawable.ic_aircrisp)
                binding.aircrispLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localAirCrispCookFilter = true

                binding.aircrispIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.aircrispIconImage.setBackgroundResource(R.drawable.ic_aircrisp_selected)
                binding.aircrispLabel.setTextColor(resources.getColor(R.color.black1))
            }
        }

        binding.roastIcon.setOnClickListener {
            if (localRoastCookFilter) {
                localRoastCookFilter = false

                binding.roastIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.roastIconImage.setBackgroundResource(R.drawable.ic_roast)
                binding.roastLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localRoastCookFilter = true

                binding.roastIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.roastIconImage.setBackgroundResource(R.drawable.ic_explore_roast_selected)
                binding.roastLabel.setTextColor(resources.getColor(R.color.black1))
            }
        }

        binding.bakeIcon.setOnClickListener {
            if (localBakeCookFilter) {
                localBakeCookFilter = false

                binding.bakeIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.bakeIconImage.setBackgroundResource(R.drawable.bake)
                binding.bakeLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localBakeCookFilter = true

                binding.bakeIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.bakeIconImage.setBackgroundResource(R.drawable.ic_explore_bake_selected)
                binding.bakeLabel.setTextColor(resources.getColor(R.color.black1))
            }
        }

        binding.broilIcon.setOnClickListener {
            if (localBroilCookFilter) {
                localBroilCookFilter = false

                binding.broilIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.broilIconImage.setBackgroundResource(R.drawable.ic_broil)
                binding.broilLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localBroilCookFilter = true

                binding.broilIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.broilIconImage.setBackgroundResource(R.drawable.ic_explore_broil_selected)
                binding.broilLabel.setTextColor(resources.getColor(R.color.black1))
            }
        }

        binding.reheatIcon.setOnClickListener {
            if (localReheatCookFilter) {
                localReheatCookFilter = false

                binding.reheatIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.reheatIconImage.setBackgroundResource(R.drawable.ic_reheat)
                binding.reheatLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localReheatCookFilter = true

                binding.reheatIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.reheatIconImage.setBackgroundResource(R.drawable.ic_explore_reheat_selected)
                binding.reheatLabel.setTextColor(resources.getColor(R.color.black1))
            }
        }

        binding.dehydrateIcon.setOnClickListener {
            if (localDehydrateCookFilter) {
                localDehydrateCookFilter = false

                binding.dehydrateIcon.setBackgroundResource(R.drawable.circle_background_unselected)
                binding.dehydrateIconImage.setBackgroundResource(R.drawable.ic_dehydrate)
                binding.dehydrateLabel.setTextColor(resources.getColor(R.color.medium_grey))
            } else {
                localDehydrateCookFilter = true

                binding.dehydrateIcon.setBackgroundResource(R.drawable.circle_background_selected)
                binding.dehydrateIconImage.setBackgroundResource(R.drawable.ic_explore_dehydrate_selected)
                binding.dehydrateLabel.setTextColor(resources.getColor(R.color.black1))
            }
        }

        disableModesWithNoCharts()
        hideShowModesBasedOnRegion()
    }

    private fun hideShowModesBasedOnRegion() {
        if (homeViewModel.isUSGrill()) {
            binding.reheatIcon.visibility = View.GONE
            binding.reheatLabel.visibility = View.GONE
            binding.reheatIconImage.visibility = View.GONE
            binding.reheatContainer.visibility = View.GONE

            binding.spacerSeven.visibility = View.GONE
        } else {
            binding.broilIcon.visibility = View.GONE
            binding.broilLabel.visibility = View.GONE
            binding.broilIconImage.visibility = View.GONE
            binding.broilContainer.visibility = View.GONE

            binding.spacerSix.visibility = View.GONE
        }
    }

    private fun disableModesWithNoCharts() {
        if (!homeViewModel.checkIfModeHasChart(CookMode.Grill)) {
            binding.grillIcon.isEnabled = false
            binding.grillIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.grillIconImage.setBackgroundResource(R.drawable.ic_grill_selected)
            binding.grillLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Smoke)) {
            binding.smokeIcon.isEnabled = false
            binding.smokeIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.smokeIconImage.setBackgroundResource(R.drawable.ic_explore_smoke_selected)
            binding.smokeLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.AirCrisp)) {
            binding.aircrispIcon.isEnabled = false
            binding.aircrispIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.aircrispIconImage.setBackgroundResource(R.drawable.ic_aircrisp_selected)
            binding.aircrispLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Roast)) {
            binding.roastIcon.isEnabled = false
            binding.roastIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.roastIconImage.setBackgroundResource(R.drawable.ic_explore_roast_selected)
            binding.roastLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Bake)) {
            binding.bakeIcon.isEnabled = false
            binding.bakeIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.bakeIconImage.setBackgroundResource(R.drawable.ic_explore_bake_selected)
            binding.bakeLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Broil)) {
            binding.broilIcon.isEnabled = false
            binding.broilIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.broilIconImage.setBackgroundResource(R.drawable.ic_explore_broil_selected)
            binding.broilLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Reheat)) {
            binding.reheatIcon.isEnabled = false
            binding.reheatIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.reheatIconImage.setBackgroundResource(R.drawable.ic_explore_reheat_selected)
            binding.reheatLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
        if (!homeViewModel.checkIfModeHasChart(CookMode.Dehydrate)) {
            binding.dehydrateIcon.isEnabled = false
            binding.dehydrateIcon.setBackgroundResource(R.drawable.circle_background_disabled)
            binding.dehydrateIconImage.setBackgroundResource(R.drawable.ic_explore_dehydrate_selected)
            binding.dehydrateLabel.setTextColor(resources.getColor(R.color.medium_grey))
        }
    }

    private fun setInitialCookModeSelectorStates() {
        localGrillCookFilter = homeViewModel.grillCookFilter
        localSmokeCookFilter = homeViewModel.smokeCookFilter
        localAirCrispCookFilter = homeViewModel.airCrispCookFilter
        localRoastCookFilter = homeViewModel.roastCookFilter
        localDehydrateCookFilter = homeViewModel.dehydrateCookFilter
        localBroilCookFilter = homeViewModel.broilCookFilter
        localReheatCookFilter = homeViewModel.reheatCookFilter
        localBakeCookFilter = homeViewModel.bakeCookFilter

        if (homeViewModel.grillCookFilter) {
            binding.grillIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.grillIconImage.setBackgroundResource(R.drawable.ic_grill_selected)
            binding.grillLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.smokeCookFilter) {
            binding.smokeIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.smokeIconImage.setBackgroundResource(R.drawable.ic_explore_smoke_selected)
            binding.smokeLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.airCrispCookFilter) {
            binding.aircrispIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.aircrispIconImage.setBackgroundResource(R.drawable.ic_aircrisp_selected)
            binding.aircrispLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.roastCookFilter) {
            binding.roastIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.roastIconImage.setBackgroundResource(R.drawable.ic_explore_roast_selected)
            binding.roastLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.dehydrateCookFilter) {
            binding.dehydrateIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.dehydrateIconImage.setBackgroundResource(R.drawable.ic_explore_dehydrate_selected)
            binding.dehydrateLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.broilCookFilter) {
            binding.broilIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.broilIconImage.setBackgroundResource(R.drawable.ic_explore_broil_selected)
            binding.broilLabel.setTextColor(resources.getColor(R.color.black1))
        }
        if (homeViewModel.bakeCookFilter) {
            binding.bakeIcon.setBackgroundResource(R.drawable.circle_background_selected)
            binding.bakeIconImage.setBackgroundResource(R.drawable.ic_explore_bake_selected)
            binding.bakeLabel.setTextColor(resources.getColor(R.color.black1))
        }
    }

    companion object {
        const val TAG = "CookModeSelectorBottomSheet"
    }
}