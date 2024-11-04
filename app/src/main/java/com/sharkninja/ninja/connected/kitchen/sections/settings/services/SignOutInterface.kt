package com.sharkninja.ninja.connected.kitchen.sections.settings.services

import kotlinx.coroutines.flow.Flow

interface SignOutInterface {
    suspend fun signOut(): Flow<Unit>
}