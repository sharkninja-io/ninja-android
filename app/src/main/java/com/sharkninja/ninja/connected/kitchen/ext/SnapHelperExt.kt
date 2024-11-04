package com.sharkninja.ninja.connected.kitchen.sections.home.fragments.cook.services

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.OnSnapPositionChangeListener
import com.sharkninja.ninja.connected.kitchen.ui.views.SnapOnScrollListener

fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
    layoutManager: LinearLayoutManager,
    onSnapPositionChangeListener: OnSnapPositionChangeListener,
) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = SnapOnScrollListener(snapHelper,behavior, onSnapPositionChangeListener,layoutManager)
    addOnScrollListener(snapOnScrollListener)
}