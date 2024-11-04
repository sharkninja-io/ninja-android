package com.sharkninja.ninja.connected.kitchen.ext

import com.bugsnag.android.Bugsnag

fun logBreadCrumbClickEvent(action: String) {
    Bugsnag.leaveBreadcrumb("CLICKED $action BUTTON")
}