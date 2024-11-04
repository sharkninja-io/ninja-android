package com.sharkninja.ninja.connected.kitchen.ui.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarItemView
import com.sharkninja.ninja.connected.kitchen.R
import kotlinx.coroutines.*

class NinjaBottomNavigationView : BottomNavigationView {

    private var currentNavigationItemId = -1
    private var currentCircleId = -1
    private val menuViewGroupId = View.generateViewId()

    private lateinit var rootLayout: RelativeLayout
    private var disabledColor =
        ContextCompat.getColor(context, R.color.grey2)
    private var enabledColor = ContextCompat.getColor(context, R.color.theme_background)
    private var textColor = ContextCompat.getColor(context, R.color.ninja_green)

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
                init(attrs)
            }

    private fun init(attrs: AttributeSet? = null) {
        setupLayout()
        selectFirstItem()
    }

    private fun setupLayout() {
        val menuViewGroup = getChildAt(0) as BottomNavigationMenuView
        menuViewGroup.id = menuViewGroupId
        rootLayout = RelativeLayout(context)
        removeView(menuViewGroup)
        rootLayout.addView(menuViewGroup)
        addView(rootLayout)
    }

    private fun disableClipOnParents(view: View) {
        if (view is ViewGroup) {
            view.clipChildren = false
        }

        if (view.parent is View) {
            disableClipOnParents(view.parent as View)
        }
    }

    private fun selectFirstItem() {
        if (
            rootLayout.childCount > 0 &&
            ((rootLayout.getChildAt(0)) as BottomNavigationMenuView).childCount > 0
        ) {
            val navigationItemView =
                (
                        (rootLayout.getChildAt(0) as BottomNavigationMenuView)
                            .getChildAt(0) as NavigationBarItemView
                        )

            navigationItemView.viewTreeObserver.addOnGlobalLayoutListener {
                animateBottomIcon(selectedItemId)
            }
        }
    }

    private fun animateBottomIcon(itemId: Int): Boolean {
        if (itemId != currentNavigationItemId) {
            val itemView = getNavigationBarItemView(itemId)
            val icon = getAppCompatImageView(itemView)
            disableClipOnParents(icon)
            val subText = getSubTextView(itemView)
            val animatorSet = AnimatorSet()

            setSubTextStyle(subText)

            if (currentNavigationItemId != -1) {
                val currentItemView = getNavigationBarItemView(currentNavigationItemId)
                val currentView = getPreviousAppCompatImageView(currentItemView)
                val oldCircle = rootLayout.findViewById<ImageView>(currentCircleId)

                currentView.drawable.setTint(Color.BLACK)

                animatorSet.playTogether(
                    buildTranslateIconAnimator(currentView, -50f, 0f),
                    buildTranslateCircleAnimator(oldCircle, -50f, 0f),
                    buildTintAnimator(currentView, enabledColor, disabledColor)
                )
                oldCircle.animate()
                    .alpha(0F)
                    .duration = 50

                GlobalScope.launch {
                    delay(50)
                    withContext(Dispatchers.Main) {
                        rootLayout.removeView(oldCircle)
                    }
                }
            }

            val circleView = buildBackgroundCircle()
            currentCircleId = circleView.id

            rootLayout.addView(circleView)
            findViewById<BottomNavigationMenuView>(menuViewGroupId).bringToFront()

            setCircleSizeAndPosition(
                circleView,
                icon.width * 2,
                itemView.x + itemView.width / 2 - icon.width
            )

            animatorSet.playTogether(
                buildTranslateIconAnimator(icon, -50f, -(height / 2.7).toFloat()),
                buildTranslateCircleAnimator(circleView, -50f, -(height / 2.7).toFloat()),
                buildTintAnimator(icon, disabledColor, enabledColor)
            )

            circleView.animate()
                .alpha(1F)
                .duration = 50

            currentNavigationItemId = itemId
            animatorSet.start()
        }

        return true
    }

    private fun getNavigationBarItemView(itemId: Int): NavigationBarItemView {
        return findViewById(itemId)
    }

    private fun getAppCompatImageView(itemView: NavigationBarItemView): AppCompatImageView {
        val item: AppCompatImageView = itemView.findViewById(
            com.google.android.material.R.id.navigation_bar_item_icon_view
        )

        val margins = MarginLayoutParams(item.width, item.height)
        margins.topMargin = item.height / 2
        item.layoutParams = LayoutParams(margins)

        return item
    }

    private fun getPreviousAppCompatImageView(itemView: NavigationBarItemView): AppCompatImageView {
        val item: AppCompatImageView = itemView.findViewById(
            com.google.android.material.R.id.navigation_bar_item_icon_view
        )

        val margins = MarginLayoutParams(item.width, item.height)
        margins.topMargin = 0
        item.layoutParams = LayoutParams(margins)

        return item
    }


    private fun getSubTextView(itemView: NavigationBarItemView): TextView {
        return itemView.findViewById(
            com.google.android.material.R.id.navigation_bar_item_large_label_view
        )
    }

    private fun setSubTextStyle(textView: TextView) {
        textView.setTypeface(textView.typeface, Typeface.BOLD)
        textView.setTextColor(textColor)
    }

    private fun buildTranslateIconAnimator(currentView: View, from: Float, to: Float):
            ObjectAnimator {
        return ObjectAnimator.ofFloat(
            currentView,
            "translationY",
            from, to
        ).setDuration(50)
    }

    private fun buildTranslateCircleAnimator(oldCircle: View, from: Float, to: Float):
            ObjectAnimator {
        return ObjectAnimator.ofFloat(
            oldCircle,
            "translationY",
            from, to
        ).setDuration(50)
    }

    private fun buildTintAnimator(currentView: AppCompatImageView, from: Int, to: Int):
            ValueAnimator {
        val animateTint = ValueAnimator.ofArgb(from, to)
        animateTint.duration = 50
        animateTint.addUpdateListener {
            currentView.drawable.setTint(it.animatedValue as Int)
        }

        return animateTint
    }

    private fun buildBackgroundCircle(): ImageView {
        val circleView = ImageView(context)
        circleView.id = View.generateViewId()
        circleView.alpha = 0F

        val drawable = ContextCompat.getDrawable(context, R.drawable.nav_ic_background_update)
        circleView.setImageDrawable(drawable)

        return circleView
    }

    private fun setCircleSizeAndPosition(
        circleView: ImageView,
        size: Int,
        x: Float,
    ) {
        val params = circleView.layoutParams
        params.width = size
        params.height = size
        circleView.layoutParams = params
        circleView.x = x
    }
}