package com.sharkninja.ninja.connected.kitchen.data.models

import android.content.Context
import com.sharkninja.ninja.connected.kitchen.R

data class LocalMessage(
    val notificationTitle: String,
    val notificationBody: String
)

fun grillOfflineNotification(context: Context): LocalMessage {
    return LocalMessage(
        context.getString(R.string.local_notification_offline_title),
        context.getString(R.string.local_notification_offline_body)
    )
}