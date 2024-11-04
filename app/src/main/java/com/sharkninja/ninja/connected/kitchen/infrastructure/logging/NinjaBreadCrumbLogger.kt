package com.sharkninja.ninja.connected.kitchen.infrastructure.logging

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.bugsnag.android.BreadcrumbType
import com.bugsnag.android.Bugsnag


class NinjaBreadCrumbLogger: FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        logNavigationBreadcrumb(f, "onFragmentViewCreated")
    }

    override fun onFragmentCreated(
        fm: FragmentManager,
        f: Fragment,
        savedInstanceState: Bundle?
    ) {
        logNavigationBreadcrumb(f, "onFragmentCreated")
    }

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        logNavigationBreadcrumb(f, "onFragmentAttached")
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        logNavigationBreadcrumb(f, "onFragmentDetached")
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        logNavigationBreadcrumb(f, "onFragmentDestroyed")
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        logNavigationBreadcrumb(f, "onFragmentViewDestroyed")
    }


    private fun logNavigationBreadcrumb(fragment: Fragment, lifecycle: String) {
        // Don't log NavHostFragment as it's not a real fragment
        if (fragment !is NavHostFragment) {
            val metadata = mapOf("lifecycle" to lifecycle)
            Bugsnag.leaveBreadcrumb(
                fragment::class.java.simpleName,
                metadata,
                BreadcrumbType.NAVIGATION
            )
        }
    }
}