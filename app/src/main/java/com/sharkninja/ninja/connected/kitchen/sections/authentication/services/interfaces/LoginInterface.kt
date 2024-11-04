package com.sharkninja.ninja.connected.kitchen.sections.authentication.services.interfaces

interface LoginInterface {
    suspend fun login(email: String, password: String): Result<Unit>
//    suspend fun loginWithPhone(phone: String, password: String): Result<Unit>
    suspend fun isLoggedIn(): Boolean
    suspend fun hasUserSession(): Boolean
}