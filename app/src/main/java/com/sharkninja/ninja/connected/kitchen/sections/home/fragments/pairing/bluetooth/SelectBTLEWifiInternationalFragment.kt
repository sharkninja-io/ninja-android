package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing.bluetooth

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentSelectBtleWifiInternationalBinding
import com.sharkninja.ninja.connected.kitchen.ext.hide
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.ext.show
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.components.adapters.WifiNetworkRecyclerViewAdapter
import kotlinx.coroutines.launch

class SelectBTLEWifiInternationalFragment : Fragment() {

    private lateinit var binding: FragmentSelectBtleWifiInternationalBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var networkListAdapter: WifiNetworkRecyclerViewAdapter

    private val timer by lazy {
        object: CountDownTimer(
            com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs.COUNT_DOWN_LENGTH,
            com.sharkninja.ninja.connected.kitchen.sections.cook.dialogs.COUNT_DOWN_INTERVAL
        ) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                showWifiNetworkList()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectBtleWifiInternationalBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.clearUserSelectedWifiNetwork()
        subscribeToVM()
        timer.start()
    }

    private fun initBindings() {
        binding.networkTipsText.setOnClickListener {
            findNavController().safeNavigate(SelectBTLEWifiInternationalFragmentDirections.actionSelectBTLEWifiInternationalFragmentToNetworkTipsDialogFragment())
        }

        binding.wifiNetworkList.layoutManager = LinearLayoutManager(requireContext())
        networkListAdapter = WifiNetworkRecyclerViewAdapter(
            emptyList(),
            onOptionSelected = homeViewModel::onUserBTLEWifiNetworkSelected
        )
        binding.wifiNetworkList.adapter = networkListAdapter
    }

    private fun subscribeToVM() {
        homeViewModel.setBTLEWifiOnlineCallback()
        homeViewModel.listenForDeviceRegistration()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                println("Networks: SENDING SCAN REQUEST")
                homeViewModel.sendScanRequestCommand()

                homeViewModel.fetchGrillCoreWifiNetworks().collect {
                    val wifiNetworkList = it.filter { network -> network.ssid!!.isNotEmpty() }
                    networkListAdapter.updateList(wifiNetworkList)
                    updateUI(wifiNetworkList.isEmpty())
                }
            }
        }

        homeViewModel.selectedUserWifiNetwork.observe(viewLifecycleOwner) { selectedWifiNetwork ->
            selectedWifiNetwork?.let {
                if (it.ssid != null) {
                    findNavController().safeNavigate(SelectBTLEWifiInternationalFragmentDirections
                        .actionSelectBTLEWifiInternationalFragmentToEnterBTLEWifiInternationalPasswordFragment())
                }
            }
        }

    }

    private fun updateUI(isListEmpty: Boolean) {
        if(isListEmpty) showWaitingIndicator()
        else showWifiNetworkList()
    }

    private fun showWaitingIndicator() {
        binding.wifiNetworkList.hide()
        binding.ellipseWaiting.show()
    }

    private fun showWifiNetworkList() {
        timer.cancel()
        binding.ellipseWaiting.hide()
        binding.wifiNetworkList.show()
    }
}