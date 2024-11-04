package com.sharkninja.ninja.connected.kitchen.sections.settings.services

import android.content.Context
import android.util.Log
import com.sharkninja.grillcore.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class SignOutService(context: Context) :
    SignOutInterface
//    ,EventMonitoringInterface
{
    override suspend fun signOut(): Flow<Unit> {
        return withContext(Dispatchers.IO) {
            callbackFlow {
                val logoutResult = User.logout()
                try {
                    trySend(logoutResult.getOrThrow())
                } catch (throwable: Throwable) {
                    trySend(Unit)
                    Log.d(SignOutService::class.java.simpleName, "Failed to logout.")
//                    logBugsnagEvent(Throwable("${SignOutService::class.java.simpleName}: Failed to logout. ${throwable.message}"))
                }
                awaitClose()
            }
        }
    }

//    override fun logBugsnagEvent(error: Throwable) {
//        Bugsnag.notify(error)
//    }
}