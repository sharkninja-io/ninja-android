package com.sharkninja.ninja.connected.kitchen.sections.authentication.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sharkninja.ninja.connected.kitchen.data.enums.RegionCode
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentCountryRegionBinding
import com.sharkninja.ninja.connected.kitchen.sections.authentication.adapters.RegionRecyclerAdapter
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import java.util.*

class CountryRegionFragment : Fragment() {

    private lateinit var binding: FragmentCountryRegionBinding
    private val authenticationViewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCountryRegionBinding.inflate(inflater, container, false)

        initBindings()
        return binding.root
    }

    private fun initBindings() {
        initRegionList(Locale.getDefault().country)

        binding.continueButton.setOnClickListener { view ->
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                authenticationViewModel.storeRegionSelection().flowOn(Dispatchers.IO)
                    .catch {
                        //TODO handle error
                    }
                    .collect {
                        Log.d(CountryRegionFragment::class.java.simpleName, "Region stored at key: $it")
                        findNavController().navigate(CountryRegionFragmentDirections.actionCountryRegionFragmentToCreateAccountFragment())
                    }
            }
        }
    }

    private fun initRegionList(suggestedCountrySelection: String) {
        var regionList = RegionCode.values()
//        regionList = regionList.dropLast(1).toTypedArray()
        val sortedRegionList = regionList.map { it.toString() }.sortedWith(compareBy {
            Locale("", it)
                .displayCountry
        }).toMutableList()

//        sortedRegionList.add(RegionCode.RestOfWorld.toString())

        var defaultSelectionPosition: Int? = null

        for (region in sortedRegionList) {
            if (region == suggestedCountrySelection) {
                sortedRegionList.remove(region)
                sortedRegionList.add(0, region)
                defaultSelectionPosition = 0
                break
            }
        }

        binding.regionRecyclerView.layoutManager = LinearLayoutManager(context)

        context?.let {
            val adapter = RegionRecyclerAdapter(
                it,
                sortedRegionList,
                defaultSelectionPosition,
                object : RegionRecyclerAdapter.RegionInterface {
                    override fun regionSelected(regionCode: String) {
                        binding.continueButton.isEnabled = true
                        authenticationViewModel.setRegion(regionCode)
                        Log.d(CountryRegionFragment::class.java.simpleName, "Region selected: $regionCode")
                    }
                })
            binding.regionRecyclerView.adapter = adapter
        }
    }
}