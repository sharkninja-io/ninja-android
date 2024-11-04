package com.sharkninja.ninja.connected.kitchen.sections.authentication.services.interfaces

import kotlinx.coroutines.flow.Flow

interface PasswordResetInterface {
    suspend fun requestPasswordReset(email: String, emailTemplateId: String?, emailSubject: String?, emailBodyHTML: String?, regionCode: String): Flow<Any>
    suspend fun resetPassword(token: String, password: String, passwordConfirmation: String): Flow<Any>
}