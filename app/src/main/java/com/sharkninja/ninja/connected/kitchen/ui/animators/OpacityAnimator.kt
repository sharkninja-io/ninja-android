package com.sharkninja.ninja.connected.kitchen.ui.animators

import android.view.View

class OpacityAnimator {

    companion object {
        fun fadeInOut(view: View, displayDuration: Long) {
            view.animate().alpha(1.0f).setDuration(1000).setStartDelay(0).withEndAction {
                view.animate().alpha(0.0f)
                    .setDuration(1000).setStartDelay(displayDuration).start()
            }.start()
        }
    }
}