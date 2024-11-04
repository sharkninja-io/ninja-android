package com.sharkninja.ninja.connected.kitchen.sections.home.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sharkninja.grillcore.CookingCharts.Companion.getCookingCharts
import com.sharkninja.grillcore.Temp
import com.sharkninja.ninja.connected.kitchen.BaseActivity
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.data.models.LocalMessage
import com.sharkninja.ninja.connected.kitchen.infrastructure.bluetooth.BluetoothService
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.firebase.COOK_NOTIFICATION_CHANNEL
import com.sharkninja.ninja.connected.kitchen.sections.authentication.activities.AuthenticationActivity
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.CookService
import com.sharkninja.ninja.connected.kitchen.sections.home.CookingChartCard
import com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.CookingCard
import com.sharkninja.ninja.connected.kitchen.sections.home.services.WifiPairingService
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModel
import com.sharkninja.ninja.connected.kitchen.sections.home.viewmodels.HomeViewModelFactory
import com.sharkninja.ninja.connected.kitchen.sections.settings.services.SignOutService
import com.sharkninja.ninja.connected.kitchen.ui.views.VerticalTwoButtonPositiveDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeActivity : BaseActivity() {

    private val factory by lazy {
        HomeViewModelFactory(
            WifiPairingService(applicationContext, SignOutService(applicationContext)),
            BluetoothService(applicationContext),
            CookService(CacheService())
        )
    }
    val homeViewModel: HomeViewModel by viewModels(factoryProducer = { factory })

    private val homeHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.home_host_fragment) as NavHostFragment }
    private val homeNavController by lazy { homeHostFragment.navController }
    private lateinit var toolbar: Toolbar

    private var isGrillsNotEmpty: Boolean = true

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //This is for getting the dimensions of the display to use in pixel calculations
        homeViewModel.xPix = resources.displayMetrics.widthPixels
        homeViewModel.density = resources.displayMetrics.density
        homeViewModel.xdpi = resources.displayMetrics.xdpi

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavBar.setupWithNavController(homeNavController)

        toolbar = findViewById(R.id.home_toolbar)

        homeNavController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {

                R.id.bluetoothPermissionsFragment,
                R.id.selectWifiFragment,
                R.id.pairingPreparationFragment,
                R.id.bluetoothPairingPreparationFragment,
                R.id.connectToGrillFragment,
                R.id.connectToWifiFragment,
                R.id.pairingPermissionsFragment,
                R.id.bluetoothGrillDetectedFragment,
                R.id.bluetoothPairingFragment,
                R.id.bluetoothNameYourGrillFragment,
                R.id.bluetoothFindDevicesFragment,
                R.id.selectBTLEWifiFragment,
                R.id.enterBTLEWifiPasswordFragment,
                R.id.connectToBTLEWifiFragment,
                R.id.enterWifiPasswordFragment,
                R.id.connectToBTLEWifiErrorPersistentFragment,
                R.id.connectToBTLEWifiErrorFirstFragment,
                R.id.bluetoothInternationalFindDevicesFragment,
                R.id.bluetoothInternationalGrillDetectedFragment,
                R.id.bluetoothInternationalNameYourGrillFragment,
                R.id.bluetoothInternationalPairingFragment,
                R.id.bluetoothInternationalPermissionsFragment,
                R.id.bluetoothInternationalPairingPreparationFragment,
                R.id.bluetoothInternationalSecondPairingPreparationFragment,
                R.id.connectToBTLEWifiInternationalFragment,
                R.id.enterBTLEWifiInternationalPasswordFragment,
                R.id.selectBTLEWifiInternationalFragment,
                R.id.wifiInternationalOptionFragment,
                R.id.connectToBluetoothInternationalErrorFirstFragment,
                R.id.connectToBTLEWifiInternationalErrorPersistentFragment,
                R.id.connectToBTLEWifiInternationalErrorFirstFragment -> {
                    hideNavIcon()
                    toolbar.visibility = View.VISIBLE
                    bottomNavBar.visibility = View.GONE
                    setToolbarCloseButtonClickListenerAndVisibility()
                }
                R.id.connectToWifiErrorPersistentFragment,
                R.id.connectToWifiErrorFirstFragment,
                R.id.connectToBluetoothErrorFirstFragment,
                R.id.networkTipsDialogFragment,
                R.id.startDialogFragment,
                R.id.plugDialogFragment,
                R.id.discoveryDialogFragment -> {
                    toolbar.visibility = View.VISIBLE
                    bottomNavBar.visibility = View.GONE
                }

                R.id.bluetoothPersistentErrorFragment,
                R.id.wifiOptionFragment -> {
                    toolbar.visibility = View.GONE
                    bottomNavBar.visibility = View.GONE
                }


                R.id.deleteAccountFragment -> {
                    toolbar.visibility = View.VISIBLE
                    bottomNavBar.visibility = View.GONE
                    hideNavIcon()
                }

                R.id.homeFragment -> bottomNavBar.visibility = View.VISIBLE
                R.id.miniChartFragment,
                R.id.chartDisplayFragment,
                R.id.editCookTimeTempFragment,
                R.id.editGrillTempFragment,
                R.id.editFoodTempProbe0Fragment,
                R.id.exploreAllFiltersFragment,
                R.id.editFoodTempProbe1Fragment,
                R.id.exploreRecipeFragment -> {
                    bottomNavBar.visibility = View.GONE
                    toolbar.visibility = View.GONE
                }

                R.id.cookFragment,
                R.id.progressBarDialogCook,
                R.id.cookEditTempFragment,
                R.id.cookTimeTempFragment,
                R.id.cookEditFoodTempProbe0Fragment,
                R.id.cookEditFoodTempProbe1Fragment,
                R.id.plugInThermometer1Dialog,
                R.id.plugInThermometer2Dialog -> {
                    bottomNavBar.visibility = View.GONE
                    toolbar.visibility = View.GONE
                    setStatusBarColorDark()
                }

                R.id.timedCookDashboard,
                R.id.grillAccessoryDialog-> {
                    toolbar.visibility = View.GONE
                }

                R.id.preCookFragment,
                R.id.progressBarDialogPreCook -> {
                    toolbar.visibility = View.GONE
                }

                R.id.inspireFragment,
                R.id.recipesFragment,
                R.id.settingsFragment -> {
                    setStatusBarColorLight()
                    hideNavIcon()
                    hideCloseButton()
                    toolbar.visibility = View.VISIBLE
                    bottomNavBar.visibility = View.VISIBLE
                }

                R.id.exploreFragment -> {
                    toolbar.visibility = View.GONE
                    bottomNavBar.visibility = View.VISIBLE
                    setStatusBarColorLight()
                    hideNavIcon()
                    hideCloseButton()
                }

                R.id.appliancesFragment -> {
                    bottomNavBar.visibility = View.GONE
                }

                R.id.supportFragmentCook -> {
                    toolbar.visibility = View.VISIBLE
                    bottomNavBar.visibility = View.GONE
                    setToolbarNavClickListener()
                    hideCloseButton()
                }

                R.id.aboutThisAppFragment,
                R.id.accountFragment,
                R.id.supportFragment,
                R.id.applianceLandingFragment,
                R.id.applianceDetailFragment,
                R.id.changeEmailFragment,
                R.id.changePasswordFragment,
                R.id.preferencesFragment,
                R.id.privacyPolicyFragment -> {
                    setToolbarNavClickListener()
                    toolbar.visibility = View.VISIBLE
                    hideCloseButton()
                    bottomNavBar.visibility = View.GONE
                }
            }
        }
        subscribeToVm()

    }

    private fun subscribeToVm() {
        homeViewModel.grills.onSubscription {
            homeViewModel.fetchGrills()
        }.onEach {
            isGrillsNotEmpty = it.isNotEmpty()
            populateAllCharts()
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun setStatusBarColorDark() {
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = false
        window.statusBarColor = ContextCompat.getColor(this, R.color.deep_black)
    }

    fun setStatusBarColorLight() {
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).isAppearanceLightStatusBars = true
        window.decorView.isForceDarkAllowed = false
        window.statusBarColor = ContextCompat.getColor(this, R.color.white_8F)
    }

    fun showBottomTabNavigation() {
        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavBar.visibility = View.VISIBLE
    }

    fun setToolbarNavCustomClickListener(onClick: () -> Unit) {
        toolbar.navigationIcon = resources.getDrawable(R.drawable.iconoir_arrow_left, theme)
        toolbar.setNavigationOnClickListener { onClick() }
    }

    private fun setToolbarCloseButtonClickListenerAndVisibility() {
        toolbar.findViewById<ImageButton>(R.id.close_button).isVisible = true
        toolbar.findViewById<ImageButton>(R.id.close_button).setOnClickListener {
                if(isGrillsNotEmpty) {
                    showCancelPairingDialog()
                } else {
                    showLogoutDialog()
                }
            }
    }

    fun hideCloseButton() {
        toolbar.findViewById<ImageButton>(R.id.close_button).visibility = View.GONE
    }

    private fun showLogoutDialog() {
        VerticalTwoButtonPositiveDialog {
            title = getString(R.string.sign_out_pairing_dialog_title)
            description = getString(R.string.sign_out_pairing_dialog_description)
            topButton = VerticalTwoButtonPositiveDialog.ButtonConfiguration(
                text = getString(R.string.sign_out_pairing_dialog_top_button),
                action = {
                    signOut()
                }
            )
            bottomButton = VerticalTwoButtonPositiveDialog.ButtonConfiguration(
                text = getString(R.string.sign_out_pairing_dialog_top_bottom_button)
            )
            topButtonColor = R.color.ninja_green
        }.show(supportFragmentManager, SIGN_OUT_TAG)
    }

    private fun signOut() {
         scope.launch {
             homeViewModel.signOut()
                 .collect {
                     withContext(Dispatchers.Main) {
                         val intent = Intent(this@HomeActivity, AuthenticationActivity::class.java)
                         startActivity(intent.apply {
                             flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                         })
                     }
                 }
         }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun showCancelPairingDialog() {
        VerticalTwoButtonPositiveDialog {
            title = getString(R.string.cancel_pairing_dialog_title)
            description = getString(R.string.cancel_pairing_dialog_description)
            topButton = VerticalTwoButtonPositiveDialog.ButtonConfiguration(
                text = getString(R.string.cancel_pairing_dialog_top_button),
                action = {
                    homeViewModel.isBackFromAddAppliance = true
                    homeNavController.navigate(R.id.settings_graph)
                }
            )
            bottomButton = VerticalTwoButtonPositiveDialog.ButtonConfiguration(
                text = getString(R.string.cancel_pairing_dialog_bottom_button)
            )
            topButtonColor = R.color.ninja_green
        }.show(supportFragmentManager, CANCEL_PAIRING_TAG)
    }

    private fun setToolbarNavClickListener() {
        toolbar.navigationIcon = resources.getDrawable(R.drawable.iconoir_arrow_left, theme)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onPause() {
        homeViewModel.setBtAvailable(false)
        super.onPause()
    }

    private fun hideNavIcon() {
        toolbar.navigationIcon = null
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.setBTAvailableOnResume()
    }

    companion object {
        const val SIGN_OUT_TAG = "SIGN_OUT_DIALOG"
        const val CANCEL_PAIRING_TAG = "CANCEL_PAIRING_DIALOG"
    }

    private var jsonString = ""

    private fun populateAllCharts() {
        val single = getCookingCharts()
        println("HODOR:::NumberOfCharts:${single.size}")
        var recipes = mutableListOf<CookingChartCard>()

        for (a in single) {
            val title = try {
                val titleString = a.title
                if (titleString == "null") {
                    ""
                } else {
                    titleString
                }
            } catch (e: Exception) { //default value
                println(e)

                ""
            }

            val protein = a.group.lowercase()
            val mode = a.mode.name.lowercase()

            val image = try {
                var name = a.image

                if (name == "null") {
                    resources.getIdentifier("@drawable/defaultlogoimage", null, packageName)
                } else {
                    resources.getIdentifier("@drawable/$name", null, packageName)
                }
            } catch (e: java.lang.Exception) { //default value
                println(e)
                resources.getIdentifier("@drawable/placeholder", null, packageName)
            }

            val prep = try {
                var prepString = a.prep

                prepString ?: ""
            } catch (e: java.lang.Exception) { //default value
                println(e)

                ""
            }

            var fahrenheitTemp = 0
            var genericTemp = ""

            when (a.temp) {
                is Temp.GenericTemp -> {
                    genericTemp = (a.temp as Temp.GenericTemp).temp.name
                }
                is Temp.DegreeTemp -> {
                    fahrenheitTemp = (a.temp as Temp.DegreeTemp).temp
                }
            }

            val time = a.time
            val notes: String = a.notes ?: ""

            recipes.add(
                CookingChartCard(
                    title = title,
                    protein = protein,
                    mode = mode,
                    image = image,
                    prep = prep,
                    fahrenheitTemp = fahrenheitTemp,
                    genericTemp = genericTemp,
                    time = time,
                    notes = notes,
                    countries = listOf()
                )
            )
        }


        homeViewModel.allCharts.clear()


        for (card in recipes) {
            homeViewModel.allCharts.add(
                CookingCard(
                    image = card.image,
                    meatType = card.protein,
                    cookingMethod = card.mode,
                    name = card.title,
                    description = "",
                    preparation = card.prep,
                    grillTemperatureF = card.fahrenheitTemp,
                    grillTemperatureGeneric = card.genericTemp,
                    cookTime = card.time.toString(),
                    notes = card.notes
                )
            )
        }

        homeViewModel.setSelectedChartFlow(homeViewModel.allCharts[0])
    }

    fun createLocalNotification(message: LocalMessage) {
        if(homeViewModel.isPushEnabled.value) {
            val intent = Intent().apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
            val pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val messageTitle = message.notificationTitle
            val messageBody = message.notificationBody

            val notification = NotificationCompat.Builder(this, COOK_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ninja_push_icon)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setStyle(NotificationCompat.BigTextStyle())
                .setAutoCancel(true)
                .setContentIntent(pending)

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                COOK_NOTIFICATION_CHANNEL,
                COOK_NOTIFICATION_CHANNEL,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
            manager.notify(0, notification.build())
        }
    }
}