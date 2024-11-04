package com.sharkninja.ninja.connected.kitchen.infrastructure.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sharkninja.grillcore.NotificationManager.Companion.updateToken
import com.sharkninja.ninja.connected.kitchen.R
import com.sharkninja.ninja.connected.kitchen.infrastructure.cache.CacheService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val COOK_NOTIFICATION_CHANNEL = "Cook Updates"

class NinjaMessaging : FirebaseMessagingService() {

    val cache = CacheService()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("New Firebase Token Fired: $token")
        storeToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        println("What's the payload look like? -- $message")

        val intent = Intent().apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
        val pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val messageTitle = message.notification?.title
        val messageBody = message.notification?.body

        messageTitle?.let {
            messageBody?.let {
                val notification = NotificationCompat.Builder(this, COOK_NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.drawable.ninja_push_icon)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setContentIntent(pending)

                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val channel = NotificationChannel(
                    COOK_NOTIFICATION_CHANNEL,
                    COOK_NOTIFICATION_CHANNEL,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                manager.createNotificationChannel(channel)
                manager.notify(0, notification.build())
            }
        }
    }

    private fun storeToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            cache.setAppCacheValue(
                FIREBASE_REGISTRATION_TOKEN,
                token
            ).collect {
                updateToken()
                    .onSuccess { println("Grillcore token update SUCCESS!") }
                    .onFailure { println("Grillcore token update FAILED!") }
            }
        }
    }
}

const val FIREBASE_REGISTRATION_TOKEN = "push_token"