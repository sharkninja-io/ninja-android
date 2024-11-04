package com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sharkninja.ninja.connected.kitchen.sections.authentication.services.interfaces.AuthenticationInterface

class AuthenticationViewModelFactory(private val auth: AuthenticationInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(AuthenticationInterface::class.java).newInstance(auth)
    }
}