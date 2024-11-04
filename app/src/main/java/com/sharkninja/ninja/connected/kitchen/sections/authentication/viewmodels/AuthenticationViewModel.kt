package com.sharkninja.ninja.connected.kitchen.sections.authentication.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bugsnag.android.Bugsnag
import com.sharkninja.grillcore.User
import com.sharkninja.ninja.connected.kitchen.data.enums.RegionCode
import com.sharkninja.ninja.connected.kitchen.data.enums.toRegionCode
import com.sharkninja.ninja.connected.kitchen.data.models.AuthUser
import com.sharkninja.ninja.connected.kitchen.sections.authentication.services.interfaces.AuthenticationInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val authService: AuthenticationInterface
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _authUser = MutableStateFlow(
        AuthUser(
            username = "", password = "", resetToken = "", verifyAccountToken = "",
            hasAgreedToCollectInfo = false, hasAgreedToTerms = false, regionCode = "US"
        )
    )
    private val _isAccountVerified = MutableStateFlow<Boolean?>(null)
    val isAccountVerified = _isAccountVerified.asStateFlow()

    private val _isAuthProgressVisible = MutableStateFlow<Boolean>(false)
    val isAuthProgressVisible = _isAuthProgressVisible.asStateFlow()

    val authUser = _authUser.asStateFlow()
    suspend fun login(): Result<Unit> {
        return authService.login(authUser.value.username, authUser.value.password)
    }

    suspend fun loginAfterCreateAccount(email: String, password: String): Result<Unit> {
        return authService.login(email, password)
    }

    suspend fun createAccount(): Flow<Any> {
        return authService.createAccount(
            authUser.value.username, authUser.value.password,
            null, null, null,
            authUser.value.regionCode
        )
    }

    fun createEcommerceAccount() {
        viewModelScope.launch(context = Dispatchers.IO) {
            authService.createEcommerceUserProfile(
                authUser.value.username,
                authUser.value.regionCode
            ).catch { error ->
                Log.e(AuthenticationViewModel::class.java.simpleName, error.message, error)
            }.collect()
        }
    }

    fun checkEcommerceAccountCreation() {
        viewModelScope.launch {
            authService.getEcommerceUserProfile(authUser.value.regionCode)
                .catch { error ->
                    Log.e(AuthenticationViewModel::class.java.simpleName, error.message, error)
                    checkUserLoggedIn()
                }.collect { profile ->
                    if (profile == null) {
                        authService.createEcommerceUserProfile(
                            authUser.value.username,
                            authUser.value.regionCode
                        ).catch { error ->
                            Log.e(AuthenticationViewModel::class.java.simpleName, error.message, error)
                        }.collect {
                            checkUserLoggedIn()
                        }
                    } else {
                        checkUserLoggedIn()
                    }
                }
        }
    }

    fun setBugSnagUser() {
        viewModelScope.launch(Dispatchers.IO) {
            User.getUUID().getOrNull()?.let {
                Bugsnag.setUser(it, null, null)
            }
        }
    }

    suspend fun requestPasswordReset(): Flow<Any> {
        return authService.requestPasswordReset(authUser.value.username, null, null, null,
        authUser.value.regionCode)
    }

    suspend fun resetPassword(): Flow<Any> {
        return authService.resetPassword(
            authUser.value.resetToken.trim(),
            authUser.value.password, authUser.value.password
        )
    }

    suspend fun sendConfirmation(): Flow<Any> {
        return authService.sendConfirmation(authUser.value.username, null, null, null,
        authUser.value.regionCode)
    }

    suspend fun checkUserLoggedIn() {
        _isLoggedIn.value = authService.isLoggedIn()
    }

    private suspend fun isLoggedIn(): Boolean {
        return authService.isLoggedIn()
    }

    private suspend fun hasUserSession(): Boolean {
        return authService.hasUserSession()
    }

    private suspend fun confirmAccount(token: String): Flow<Any> {
        return authService.confirmAccount(token)
    }

    fun setVerifyAccountToken(token: String) {
        Log.i("SignIn Credentials", "confirmAccount: $token ")
        _authUser.update { it.copy(
            verifyAccountToken = token
        ) }
    }

    fun verifyUserAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            if (authUser.value.verifyAccountToken.isNotEmpty()) {
                showAuthProgressBar(true)
                delay(3000)
                confirmAccount(authUser.value.verifyAccountToken).collect {
                    if(it is Throwable){
                        Log.e(AuthenticationViewModel::class.java.simpleName, it.message, it)
                    }
                    _isAccountVerified.value = (it is Throwable).not()
                    showAuthProgressBar(false)
                }
            }
        }
    }

    private fun showAuthProgressBar(isVisible: Boolean) {
        _isAuthProgressVisible.value = isVisible
    }

    suspend fun logInReturningUserIfPossible(): Flow<Boolean> {
        return flow {
            getRegionSelection()
                .catch { cause -> throw Throwable("${AuthenticationViewModel::class.java.simpleName}: Failed to get region selection.\n${cause.message}") }
                .collect {
                    if (!hasUserSession()) {
                        // there is no any session, we are sure user is not logged in
                        emit(false)
                    }
                    else if (!isLoggedIn()) {
                        emit(false)
                    } else {
                        // true, the user is logged in
                        emit(true)
                    }
                }
        }
    }

    suspend fun getRegionSelection(): Flow<RegionCode> {
        return authService.getSelectedRegionCode()
    }

    suspend fun storeRegionSelection(): Flow<Any> {
        return authService.setSelectedRegionCode(authUser.value.regionCode.toRegionCode())
    }

    suspend fun isRegionSelected() = flow {
        authService.getSelectedRegionCode()
            .catch {
                emit(false)
            }.collect {
                emit(true)
            }
    }

    suspend fun isTermsAccepted() = flow {
        authService.isTermsAccepted().catch { error ->
            Log.e("AuthenticationViewModel", error.message, error)
            emit(false)
        }.collect {
            emit(it)
        }
    }

    suspend fun setTermsAccepted(): Flow<Any> {
        return authService.setTermsAccepted()
    }

    fun setUser(user: String) {
        _authUser.update { it.copy(username = user) }
    }

    fun setPassword(password: String) {
        _authUser.update { it.copy(password = password) }
    }

    fun setCredentials(user: String, password: String) {
        _authUser.update { it.copy(username = user, password = password) }
    }

    fun setRegion(regionCode: String) {
        _authUser.update { it.copy(regionCode = regionCode) }
    }

    fun setTermsAgreement(hasAgreed: Boolean) {
        _authUser.update { it.copy(hasAgreedToTerms = hasAgreed) }
    }

    fun setCollectInfoAgreement(hasAgreed: Boolean) {
        _authUser.update { it.copy(hasAgreedToCollectInfo = hasAgreed) }
    }

    fun setToken(token: String) {
        _authUser.update { it.copy(resetToken = token) }
    }

//    private fun defaultToDevEnvironment(user: String): String {
//        return if (user.substring(0, 4) == "dev@") {
//            user
//        } else {
//            "dev@$user"
//        }
//    }

    override fun onCleared() {
        super.onCleared()
        println("VIEWMODEL CLEARED")
    }
}