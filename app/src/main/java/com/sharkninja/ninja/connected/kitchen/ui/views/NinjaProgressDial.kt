package com.sharkninja.ninja.connected.kitchen.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.sharkninja.ninja.connected.kitchen.R

class NinjaProgressDial : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var dialState: DialState? = DialState.Preheating
    private val preheatColors = intArrayOf(
        ContextCompat.getColor(context, R.color.lightBlue),
        ContextCompat.getColor(context, R.color.pink1),
        ContextCompat.getColor(context, R.color.pink1),
        ContextCompat.getColor(context, R.color.pink1),
        ContextCompat.getColor(context, R.color.warm2),
    )
    private val preheatPositions = floatArrayOf(0.0f, 0.459f, 0.551f, 0.66f, 0.88f)

    private val cookColors = intArrayOf(
        ContextCompat.getColor(context, R.color.warm1),
        ContextCompat.getColor(context, R.color.warm2),
        ContextCompat.getColor(context, R.color.warm3),
        ContextCompat.getColor(context, R.color.warm4),
        ContextCompat.getColor(context, R.color.warm5),
        ContextCompat.getColor(context, R.color.warm6)
    )
    private val cookPositions = floatArrayOf(0f, 0.16f, 0.32f, 0.48f, 0.64f, 0.80f)

    private val restColors = intArrayOf(
        ContextCompat.getColor(context, R.color.pink1),
        ContextCompat.getColor(context, R.color.lightBlue),
        ContextCompat.getColor(context, R.color.lightBlue),
        ContextCompat.getColor(context, R.color.lightBlue),
        ContextCompat.getColor(context, R.color.resting2),
        ContextCompat.getColor(context, R.color.resting2)
    )
    private val restPositions = floatArrayOf(0f, 0.16f, 0.32f, 0.48f, 0.64f, 0.80f)

    private val completeColors = intArrayOf(
        ContextCompat.getColor(context, R.color.ninja_green),
        ContextCompat.getColor(context, R.color.ninja_green)
    )
    private val completePositions = floatArrayOf(0.0f, 1.0f)

    private val progressRing: Paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 90f
        strokeCap = Paint.Cap.ROUND
    }

    private val preheatBackingRing = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 90f
        color = ContextCompat.getColor(context, R.color.dialBackgroundPreheat)
    }

    private val cookBackingRing = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 90f
        color = ContextCompat.getColor(context, R.color.dialBackgroundCooking)
    }

    private val restBackingRing = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 90f
        color = ContextCompat.getColor(context, R.color.dialBackgroundPreheat)
    }

    private val completeBackingRing = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 90f
        color = ContextCompat.getColor(context, R.color.restingBackground100)
    }

    private val addFoodRing = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 90f
        color = ContextCompat.getColor(context, R.color.add_food_end_color)
    }

    private val addFoodColors = intArrayOf(
        ContextCompat.getColor(context, R.color.add_food_start_color),
        ContextCompat.getColor(context, R.color.add_food_end_color)
    )

    private val addFoodPositions = null

    private val lidOpenRing = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 90f
        color = ContextCompat.getColor(context, R.color.lid_open_start_color)
    }

    private val lidOpenColors = intArrayOf(
        ContextCompat.getColor(context, R.color.lid_open_start_color),
        ContextCompat.getColor(context, R.color.lid_open_middle_color),
        ContextCompat.getColor(context, R.color.lid_open_end_color)
    )

    private val lidOpenPositions = null

    private val offlineRing = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 90f
        color = ContextCompat.getColor(context, R.color.dial_background_offline)
    }

    private val offlinePositions = floatArrayOf(0f, 0.16f, 0.32f, 0.48f, 0.64f, 0.80f)

    private val baseRect = RectF()
    private val gradientRotation = 270f
    private val gradientMatrix = Matrix()
    private var dialDiameter = 0f
    private var dialProgressAngle = 0f
    private var prevProgress = 0f


    fun setDialState(state: DialState) {
        dialState = state
        invalidate()
    }

    fun setProgress(progress: Float) {
//        dialProgressAngle = 360f / 100f * progress
//        invalidate()
        animateDial(progress)
        prevProgress = progress
    }

    private fun animateDial(progress: Float) {
        ValueAnimator.ofFloat(prevProgress, progress).apply {
            interpolator = LinearInterpolator()
            duration = 750
            addUpdateListener {
                dialProgressAngle = 340f / 100f * it.animatedValue as Float
                invalidate()
            }
        }.start()
    }

    override fun onDraw(canvas: Canvas?) {
        when (dialState) {
            DialState.NoGrills -> { }
            DialState.Offline -> {
                baseRect.set(90f, 90f, dialDiameter - 90f, dialDiameter - 90f)
                canvas?.drawArc(baseRect, -90f, 360f, false, offlineRing)
                val gradient = SweepGradient(measuredWidth.toFloat() / 2f, measuredHeight.toFloat() / 2f, cookColors, offlinePositions)
                gradient.setLocalMatrix(gradientMatrix)
                progressRing.shader = gradient
                canvas?.drawArc(baseRect, -80.2f, dialProgressAngle, false, progressRing) }
            DialState.Calculating -> { }
            DialState.Idle -> { }
            DialState.LidOpen -> {
                baseRect.set(45f, 45f, dialDiameter - 45f, dialDiameter - 45f)
                lidOpenRing.shader = LinearGradient(0f, 0f, measuredWidth.toFloat(),0f, lidOpenColors, lidOpenPositions, Shader.TileMode.CLAMP)
                canvas?.drawArc(baseRect, -90f, 360f, false, lidOpenRing)
            }
            DialState.Preheating,
            DialState.Ignition-> {
                canvas?.drawArc(baseRect, -90f, 360f, false, preheatBackingRing)
                val gradient = SweepGradient(measuredWidth.toFloat() / 2f, measuredHeight.toFloat() / 2f, preheatColors, preheatPositions)
                gradient.setLocalMatrix(gradientMatrix)
                progressRing.shader = gradient
                canvas?.drawArc(baseRect, -80.2f, dialProgressAngle, false, progressRing)
            }
            DialState.AddFood -> {
                baseRect.set(45f, 45f, dialDiameter - 45f, dialDiameter - 45f)
                addFoodRing.shader = LinearGradient(0f, 0f, measuredWidth.toFloat(),0f, addFoodColors, addFoodPositions, Shader.TileMode.CLAMP)
                canvas?.drawArc(baseRect, -90f, 360f, false, addFoodRing)
            }
            DialState.FlipFood -> {}
            DialState.Cooking -> {
                baseRect.set(90f, 90f, dialDiameter - 90f, dialDiameter - 90f)
                canvas?.drawArc(baseRect, -90f, 360f, false, cookBackingRing)
                val gradient = SweepGradient(measuredWidth.toFloat() / 2f, measuredHeight.toFloat() / 2f, cookColors, cookPositions)
                gradient.setLocalMatrix(gradientMatrix)
                progressRing.shader = gradient
                canvas?.drawArc(baseRect, -80.2f, dialProgressAngle, false, progressRing)
            }
            DialState.Resting -> {
                canvas?.drawArc(baseRect, -90f, 360f, false, restBackingRing)
                val gradient = SweepGradient(measuredWidth.toFloat() / 2f, measuredHeight.toFloat() / 2f, restColors, restPositions)
                gradient.setLocalMatrix(gradientMatrix)
                progressRing.shader = gradient
                canvas?.drawArc(baseRect, -80.2f, dialProgressAngle, false, progressRing)
            }
            DialState.Complete -> {
                canvas?.drawArc(baseRect, -90f, 360f, false, completeBackingRing)
                val gradient = SweepGradient(measuredWidth.toFloat() / 2f, measuredHeight.toFloat() / 2f, completeColors, completePositions)
                gradient.setLocalMatrix(gradientMatrix)
                progressRing.shader = gradient
                canvas?.drawArc(baseRect, -80.2f, 360f, false, progressRing)
            }
            DialState.Error -> { }

            else -> { }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        dialDiameter = w.coerceAtMost(h).toFloat()
        gradientMatrix.preRotate(gradientRotation, measuredWidth.toFloat() / 2f, measuredHeight.toFloat() / 2f)
        baseRect.set(90f, 90f, dialDiameter - 90f, dialDiameter - 90f)
    }
}

enum class DialState(val dialSubText: Int) {
    NoGrills(R.string.empty),
    Offline(R.string.all_offline),
    Calculating(R.string.empty),
    Idle(R.string.empty),
    Preheating(R.string.preheating),
    Ignition(R.string.ignition),
    AddFood(R.string.empty),
    FlipFood(R.string.empty),
    Cooking(R.string.dial_state_offline_timed_cooking),
    Resting(R.string.resting),
    Complete(R.string.empty),
    LidOpen(R.string.empty),
    Error(R.string.empty)
}