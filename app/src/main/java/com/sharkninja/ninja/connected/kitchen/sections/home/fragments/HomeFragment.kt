package com.sharkninja.ninja.connected.kitchen.sections.home.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sharkninja.ninja.connected.kitchen.databinding.FragmentHomeBinding
import com.sharkninja.ninja.connected.kitchen.ext.safeNavigate
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var binding: FragmentHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
//        initBindings()
        checkForRedirectToCookFromExplore()
        checkForRedirectFromAddAppliance()
        loadData()
        redirectToPreCook()
        return binding.root
    }

    private fun checkForRedirectFromAddAppliance() {
        if(homeViewModel.isActionAddAppliance) {
            homeViewModel.isActionAddAppliance = false

            //findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToBlueToothPermissionsFragment())

            if (homeViewModel.isUSGrill()) {
                findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToBlueToothPermissionsFragment())
            } else {
                findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToBluetoothInternationalPermissionsFragment())
            }
        }
    }

    private fun checkForRedirectToCookFromExplore() {
        if (homeViewModel.redirectToCook) {
            homeViewModel.redirectToCook = false
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToToPreCook())
        }
    }

    private fun redirectToPreCook() {
        if (!homeViewModel.wasRedirectedAtLaunch) {
            homeViewModel.wasRedirectedAtLaunch = true
            homeViewModel.observeDevicePushEnabled()
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToToPreCook())
        } else {
            checkForRequiredPairing()
        }
    }

    private fun checkForRequiredPairing() {
        homeViewModel.grills.onSubscription {
            homeViewModel.fetchGrills()
        }.onEach {
            if(it.isNullOrEmpty().not()) {
                homeViewModel.observeDevicePushEnabled()
                findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToToPreCook())
            } else {
                //findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToBlueToothPermissionsFragment())

                if (homeViewModel.isUSGrill()) {
                    findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToBlueToothPermissionsFragment())
                } else {
                    findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToBluetoothInternationalPermissionsFragment())
                }
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

//    private fun initBindings() {
//        binding.greenBlobButton.setOnClickListener {
//            animateLogout()
//        }
//
//        binding.pairingButton.setOnClickListener {
////            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPairingFragment())
//        }
//
//        binding.wifiButton.setOnClickListener {
//            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToPairingPermissionsFragment())
//        }
//
//        binding.bluetoothButton.setOnClickListener {
//            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBlueToothPermissionsFragment())
//        }
//
//        animateIcon()
//    }

    private fun loadData() {
//        homeViewModel.getCachedGrills()
        homeViewModel.getCurrentGrill()
        homeViewModel.getUserTemperatureUnit()
    }

//    private fun animateIcon() {
//        ValueAnimator.ofFloat(1f, 3f).apply {
//            interpolator = LinearInterpolator()
//            duration = 1000
//            repeatCount = Animation.INFINITE
//            addUpdateListener {
//                val scale = it.animatedValue as Float
//                val fade = 1 - scale / 3
//                binding.greenBlobPulse.scaleX = scale
//                binding.greenBlobPulse.scaleY = scale
//                binding.greenBlobPulse.alpha = fade
//            }
//            start()
//        }
//    }

//    private fun animateLogout() {
//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            User.logout()
//            .onSuccess {
//                    ValueAnimator.ofFloat(1f, 20f).apply {
//                        interpolator = AnticipateOvershootInterpolator()
//                        duration = 1000
//                        addUpdateListener {
//                            val scale = it.animatedValue as Float
//                            binding.greenBlobButton.scaleX = scale
//                            binding.greenBlobButton.scaleY = scale
//                        }
//                        doOnEnd {
//                            startActivity(Intent(context, AuthenticationActivity::class.java)
//                                .apply {
//                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                })
//                        }
//                        start()
//                    }
//                }
//                .onFailure { println("uh oh!") }
//        }
//    }
}