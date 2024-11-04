package com.sharkninja.ninja.connected.kitchen.sections.settings.services

import com.sharkninja.ninja.connected.kitchen.data.models.ChangeEmailResult
import com.sharkninja.ninja.connected.kitchen.sections.home.services.interfaces.GrillManagementInterface
import kotlinx.coroutines.flow.Flow

interface SettingsInterface :
    EcommerceInterface,
    SignOutInterface,
    PreferenceSelectionInterface,
    DeleteAndRestoreApplianceInterface,
    GrillManagementInterface,
    NinjaPushInterface {

    fun getShopUriPath(region: String): String
    fun getCurrentEmail(): String
    fun changeEmail(newEmail: String): Flow<ChangeEmailResult>
    fun changePassword(currentPassword: String, newPassword: String): Flow<Boolean>
    fun deleteAccount(): Flow<Boolean>
}