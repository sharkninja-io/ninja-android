package com.sharkninja.ninja.connected.kitchen.data.models

data class AuthUser(val username: String, val password: String, val resetToken: String, val verifyAccountToken: String,
                    val hasAgreedToCollectInfo: Boolean, val hasAgreedToTerms: Boolean, val regionCode: String)