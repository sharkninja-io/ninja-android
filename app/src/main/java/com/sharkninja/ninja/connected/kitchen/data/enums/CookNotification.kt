package com.sharkninja.ninja.connected.kitchen.data.enums

import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.sharkninja.ninja.connected.kitchen.R

enum class CookNotification(val iconLeft: Int, @StringRes val title: Int, @StringRes val body: Int) {
    AddFood(R.drawable.iconoir_fire_flame, R.string.add_food_notification_title, R.string.add_food_notification_body),
    CloseLid(R.drawable.iconoir_basic_error, R.string.close_lid_notification_title, R.string.close_lid_notification_body),
    FlipFood(R.drawable.iconoir_flip_flood, R.string.flip_food_notification_title, R.string.flip_food_notification_body),
    GetFoodThermometerOne(R.drawable.iconoir_get_food, R.string.remove_food_notification_title,R.string.get_food_therm_one_notification_body ),
    GetFoodThermometerTwo(R.drawable.iconoir_get_food, R.string.remove_food_notification_title,R.string.get_food_therm_two_notification_body ),
    Done(R.drawable.complete_00, R.string.cook_complete, R.string.cook_complete_notification_body)
}

sealed class LocalNotification {
    object OfflineNotification: LocalNotification()
    object ActionProcessed: LocalNotification()
}

sealed class BannerEvent {
    object AddFood: BannerEvent()
//    object GetFood: BannerEvent()
    object CloseLid: BannerEvent()
    object FlipFood: BannerEvent()
    object ShowTherm1GetFood: BannerEvent()
    object ShowTherm2GetFood: BannerEvent()
    object ActionProcessed: BannerEvent()
}

fun getCookBackgroundColorsToast(context: Context): IntArray {
    return intArrayOf(
        ContextCompat.getColor(context, R.color.add_food_end_color),
        ContextCompat.getColor(context, R.color.add_food_start_color),
        ContextCompat.getColor(context, R.color.notification_banner_end_color)
    )
}

fun getCloseLidBackgroundColorsToast(context: Context): IntArray {
    return intArrayOf(
        ContextCompat.getColor(context, R.color.lid_open_start_color),
        ContextCompat.getColor(context, R.color.lid_open_middle_color),
        ContextCompat.getColor(context, R.color.lid_open_end_color)
    )
}

fun getRestBackgroundColorsToast(context: Context): IntArray {
   return intArrayOf(
        ContextCompat.getColor(context, R.color.resting2),
        ContextCompat.getColor(context, R.color.restingGradient1),
        ContextCompat.getColor(context, R.color.resting_gradient_3),
        ContextCompat.getColor(context, R.color.pink1)
    )
}