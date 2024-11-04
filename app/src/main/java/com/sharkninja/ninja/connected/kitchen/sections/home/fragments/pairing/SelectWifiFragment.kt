package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.pairing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentSelectWifiBinding
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.ui.components.adapters.WifiNetworkRecyclerViewAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SelectWifiFragment : Fragment() {

    private lateinit var binding: FragmentSelectWifiBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var networkListAdapter: WifiNetworkRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectWifiBinding.inflate(inflater, container, false)
        initBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.clearUserSelectedWifiNetwork()
        setUpWifiNetWorkRv()

        homeViewModel.logPairingEvent("onViewCreated: ${SelectWifiFragment::class.java.simpleName}")

        subscribeToVM()

        homeViewModel.startPairingIoTDeviceProcess()

    }

    private fun setUpWifiNetWorkRv() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.wifiNetworkList.layoutManager = layoutManager
        networkListAdapter = WifiNetworkRecyclerViewAdapter(
            wifiNetworkList = emptyList(),
            onOptionSelected = homeViewModel::onUserWifiNetworkSelected
        )
        binding.wifiNetworkList.adapter = networkListAdapter
    }

    private fun initBindings() {
        binding.networkTipsText.setOnClickListener {
            findNavController().navigate(SelectWifiFragmentDirections.actionSelectWifiFragmentToNetworkTipsDialogFragment())
        }

        binding.wifiNetworkList.layoutManager = LinearLayoutManager(requireContext())
//        binding.wifiNetworkList.adapter = WifiNetworkRecyclerViewAdapter(
//            wifiNetworkList = listOf(WifiNetwork(0, "", 0, "", 3, "Dummy", "", ""),
//                WifiNetwork(0, "", 0, "", 3, "CuteBear", "", ""),
//                WifiNetwork(0, "", 0, "", 3, "StayOutOfMyHouse", "", "")),
//            onOptionSelected = { findNavController().navigate(SelectWifiFragmentDirections.actionSelectWifiFragmentToEnterWifiPasswordFragment()) }
//        )
    }

    private fun subscribeToVM() {
        lifecycleScope.launch {
            homeViewModel.waitForDeviceToComeOnline()
            homeViewModel.wifiNetworksVisibleToIoTDevice.collectLatest { networksSeenByDevice ->
//                binding.waitingForNetworksProgressIndicator.visibility =
//                    if (networksSeenByDevice.isNotEmpty()) View.GONE else View.VISIBLE
                networkListAdapter.updateList(networksSeenByDevice)
            }
        }

        homeViewModel.selectedUserWifiNetwork.observe(viewLifecycleOwner) { selectedWifiNetwork ->
            selectedWifiNetwork?.let {
                if (it.ssid != null) {
                    findNavController().navigate(SelectWifiFragmentDirections.actionSelectWifiFragmentToEnterWifiPasswordFragment())
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}