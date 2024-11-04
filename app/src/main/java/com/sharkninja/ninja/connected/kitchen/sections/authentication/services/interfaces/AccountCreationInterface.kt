package com.sharkninja.ninja.connected.kitchen.sections.authentication.services.interfaces

import com.sharkninja.ninja.connected.kitchen.infrastructure.ecommerce.models.EcommerceProfile
import kotlinx.coroutines.flow.Flow

interface AccountCreationInterface {
    suspend fun createAccount(email: String, password: String, emailTemplateId: String?, emailSubject: String?, emailBodyHTML: String?, regionCode: String): Flow<Any>
    suspend fun confirmAccount(token: String): Flow<Any>
    suspend fun sendConfirmation(email: String, emailTemplateId: String?, emailSubject: String?, emailBodyHTML: String?, regionCode: String): Flow<Any>

    suspend fun createEcommerceUserProfile(email: String, countryCode: String): Flow<Unit>

    suspend fun getEcommerceUserProfile(countryCode: String): Flow<EcommerceProfile?>
}