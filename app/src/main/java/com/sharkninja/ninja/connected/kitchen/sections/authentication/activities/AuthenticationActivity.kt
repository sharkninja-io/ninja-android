package com.sharkninja.ninja.connected.kitchen.sections.authentication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.sharkninja.ninja.connected.kitchen.BaseActivity
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.EcommerceProfileService
import com.sharkninja.ninja.connected.kitchen.sections.authentication.services.AuthenticationService
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModel
import com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels.AuthenticationViewModelFactory
import com.sharkninja.ninja.connected.kitchen.sections.home.activities.HomeActivity
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AuthenticationActivity : BaseActivity() {

    lateinit var authenticationViewModel: AuthenticationViewModel

    private val authHostFragment by lazy { supportFragmentManager.findFragmentById(R.id.auth_host_fragment) as NavHostFragment }
    private val authNavController by lazy { authHostFragment.navController }
    private lateinit var deepLinkHelper: AuthenticationDeepLinkHelper

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = AuthenticationViewModelFactory(
            AuthenticationService(
                CacheService(),
                EcommerceProfileService.createInstance(applicationContext)
            )
        )
        authenticationViewModel = ViewModelProvider(this, factory).get(AuthenticationViewModel::class.java)

        setContentView(R.layout.activity_authentication)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.createAccountFragment, R.id.signInFragment, R.id.confirmCodeFragment,
                R.id.emailVerificationFragment, R.id.forgotPasswordFragment, R.id.resetPasswordFragment,
                R.id.countryRegionFragment, R.id.verifySignInFragment
            )
        )
        val toolbar = findViewById<Toolbar>(R.id.auth_toolbar)
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(authNavController, appBarConfiguration)
        supportActionBar?.title = ""
//        authNavController.addOnDestinationChangedListener { _, destination, _ ->
//            when (destination.id) {
//                R.id.signInFragment, R.id.forgotPasswordFragment, R.id.resetPasswordFragment -> {
//                    toolbar.apply {
//                        if (toolbar.menu.size() > 0) {
//                            toolbar.menu.findItem(R.id.createAccountFragment).isVisible = true
//                            toolbar.menu.findItem(R.id.signInFragment).isVisible = false
//                        }
//                    }
//                }
//                R.id.createAccountFragment, R.id.emailVerificationFragment -> {
//                    toolbar.apply {
//                        if (toolbar.menu.size() > 0) {
//                            toolbar.menu.findItem(R.id.createAccountFragment).isVisible = false
//                            toolbar.menu.findItem(R.id.signInFragment).isVisible = true
//                        }
//                    }
//                }
//            }
//        }

        observeViewModel()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        // eeeeeewww, must find better way
//        menu?.add(Menu.NONE, R.id.createAccountFragment, Menu.NONE, "SIGN UP")
//            ?.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
//            ?.isVisible = authHostFragment.childFragmentManager.fragments[0] is SignInFragment
//
//        menu?.add(Menu.NONE, R.id.signInFragment, Menu.NONE, "SIGN IN")
//            ?.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)?.isVisible = false
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return item.onNavDestinationSelected(authNavController) || super.onOptionsItemSelected(item)
//    }

    private fun observeViewModel() {
        lifecycleScope.launch {

            authenticationViewModel.getRegionSelection()
                .catch { updateNavHostFragment() }
                .onEach {
                    deepLinkHelper = AuthenticationDeepLinkHelper(intent, applicationContext, it.toString())
                    updateNavHostFragment(it.toString())
                    authenticationViewModel.setRegion(it.toString())
                }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)


            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authenticationViewModel.isLoggedIn.collectLatest {
                        if (it) {
                            authenticationViewModel.setBugSnagUser()
                            val homeIntent =
                                Intent(
                                    this@AuthenticationActivity,
                                    HomeActivity::class.java
                                ).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            startActivity(homeIntent)
                        }
                    }
                }

                launch {
                    authenticationViewModel.authUser.collectLatest {
                        val props = mutableListOf<String>()
                        props.add(it.username)
                        props.add(it.password)
                        props.add(it.hasAgreedToTerms.toString())
                        props.add(it.hasAgreedToCollectInfo.toString())
                        props.add(it.regionCode)
                        props.add(it.resetToken)
                    }
                }
            }
        }
    }

    private fun updateNavHostFragment(regionCode: String? = null) {
        val authGraph = authNavController.navInflater.inflate(R.navigation.authentication_graph)

        regionCode?.let {
            when {
                deepLinkHelper.isConfirmAccountEmailAppLink() -> {
                    authGraph.setStartDestination(R.id.emailVerificationFragment)
                }
                deepLinkHelper.isPasswordResetAppLink() -> {
                    authGraph.setStartDestination(R.id.resetPasswordFragment)
                }
                else -> {
                    authGraph.setStartDestination(R.id.signInFragment)
                }
            }
        } ?: authGraph.setStartDestination(R.id.countryRegionFragment)
        authNavController.graph = authGraph
    }
}