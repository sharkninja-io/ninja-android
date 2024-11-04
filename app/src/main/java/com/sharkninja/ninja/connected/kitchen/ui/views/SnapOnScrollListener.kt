package com.sharkninja.ninja.connected.kitchen.ui.views

import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.sharkninja.ninja.connected.kitchen.sections.cook.services.interfaces.OnSnapPositionChangeListener

class SnapOnScrollListener(
    private val snapHelper: SnapHelper,
    var behavior: Behavior = Behavior.NOTIFY_ON_SCROLL,
    var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null,
    var layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    enum class Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL) {
            notifySnapPositionChange()
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE
            && newState == RecyclerView.SCROLL_STATE_IDLE) {
            notifySnapPositionChange()
        }
    }

    private fun notifySnapPositionChange() {
        val centerView = snapHelper.findSnapView(layoutManager)
        centerView?.let{
            val position = layoutManager.getPosition(it)
            val snapPositionChanged = this.snapPosition != position
            if (snapPositionChanged) {
                onSnapPositionChangeListener?.onSnapPositionChange(position)
                this.snapPosition = position
            }
        }
    }
}