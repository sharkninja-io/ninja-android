package com.sharkninja.ninja.connected.kitchen.sections.authentication.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class AuthenticationDeepLinkHelper(private val intent: Intent,
                                   private val applicationContext: Context,
                                   private val regionCode: String) {
    private val appLinkData: Uri? = intent.data

    fun isConfirmAccountEmailAppLink(): Boolean {
        return when (regionCode) {
            "GB", "DE", "FR" -> {
                (appLinkData?.path?.contains(CONFIRM_EMAIL_LINK_PATH_EU) == true
                        || intent.hasExtra(CONFIRM_EMAIL_TOKEN_EXTRA))
            }
            else -> {
                // account for old and new urls temporary
                (appLinkData?.path?.contains(CONFIRM_EMAIL_LINK_PATH_NA) == true || (appLinkData?.path?.contains(CONFIRM_EMAIL_LINK_PATH) == true)
                        || intent.hasExtra(CONFIRM_EMAIL_TOKEN_EXTRA))
            }
        }
    }

    fun isPasswordResetAppLink(): Boolean {
        return when (regionCode) {
            "GB", "DE", "FR" -> {
                (appLinkData?.path == PASSWORD_RESET_LINK_PATH_EU
                        || intent.hasExtra(PASSWORD_RESET_TOKEN_EXTRA))
            }
            else -> {
                (appLinkData?.path == PASSWORD_RESET_LINK_PATH_NA
                        || intent.hasExtra(PASSWORD_RESET_TOKEN_EXTRA))
            }
        }
    }

    fun isAppStartedFromAppLink(): Boolean {
        return isConfirmAccountEmailAppLink() || isPasswordResetAppLink()
    }

    fun getPasswordResetIntent(): Intent {
        val authIntent =
            Intent(
                applicationContext,
                AuthenticationActivity::class.java
            ).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        //example link: https://checkout.sharkclean.com/account/reset_password?resettoken=2jesxe8X
        val resetToken = when (regionCode) {
            "GB", "DE", "FR" -> {
                appLinkData?.getQueryParameter(PASSWORD_RESET_TOKEN_QUERY_PARAM_EU)
            }
            else -> {
                appLinkData?.getQueryParameter(PASSWORD_RESET_TOKEN_QUERY_PARAM)
            }
        }
        Log.d(AuthenticationDeepLinkHelper::class.java.simpleName, "Password Reset token: $resetToken")
        resetToken?.let {
            authIntent.putExtra(PASSWORD_RESET_TOKEN_EXTRA, it)
        } ?: run {
            Log.e(AuthenticationDeepLinkHelper::class.java.simpleName, "No reset token found for reset password app link. Sending to SignIn Screen.")
        }
        return authIntent
    }

    fun getConfirmAccountEmailIntent(): Intent {
        // example link: https://www.sharkclean.com/myaccount/accountsettings/verify/hzPVUTt2/
        val authIntent =
            Intent(
                applicationContext,
                AuthenticationActivity::class.java
            ).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val confirmToken = when (regionCode) {
            "GB", "DE", "FR" -> {
                appLinkData?.getQueryParameter(CONFIRM_EMAIL_TOKEN_QUERY_PARAM_EU)
            }
            else -> {
                appLinkData?.pathSegments?.lastOrNull { it != null }
            }
        }
        Log.d(AuthenticationDeepLinkHelper::class.java.simpleName, "Account Confirmation token: $confirmToken")
        confirmToken?.let {
            authIntent.putExtra(CONFIRM_EMAIL_TOKEN_EXTRA, it)
        } ?: run {
            Log.e(AuthenticationDeepLinkHelper::class.java.simpleName, "No confirm token found for email confirmation app link. Sending to SignIn Screen.")
        }
        return authIntent
    }

    companion object {
        private const val PASSWORD_RESET_TOKEN_QUERY_PARAM_OLD = "resettoken"
        private const val PASSWORD_RESET_TOKEN_QUERY_PARAM = "reset_password_token"
        private const val PASSWORD_RESET_TOKEN_QUERY_PARAM_EU = "reset_password_token"

//        private const val PASSWORD_RESET_LINK_PATH = "/account/reset_password"
        private const val PASSWORD_RESET_LINK_PATH_EU = "/myaccount/resetpassword"
        private const val PASSWORD_RESET_LINK_PATH_NA = "/forgotPassword/updatePassword"

//        private const val CONFIRM_EMAIL_TOKEN_QUERY_PARAM_EU = "confirmationtoken"
        private const val CONFIRM_EMAIL_TOKEN_QUERY_PARAM_EU = "ConfirmationToken"

        private const val CONFIRM_EMAIL_LINK_PATH = "/myaccount/accountsettings/verify/"
        private const val CONFIRM_EMAIL_LINK_PATH_EU = "/myaccount/accountsettings/verify"
        private const val CONFIRM_EMAIL_LINK_PATH_NA = "/login"

        const val PASSWORD_RESET_TOKEN_EXTRA = "passwordAppLinkResetToken"
        const val CONFIRM_EMAIL_TOKEN_EXTRA = "confirmEmailAppLinkToken"

        const val SP_KEY_USER = "sharedPrefUserName"
        const val SP_KEY_PW = "sharedPrefPassword"


        fun getConfirmAccountEmailToken(intent: Intent): String? {
            return intent.getStringExtra(CONFIRM_EMAIL_TOKEN_EXTRA)
        }

        fun getPasswordResetToken(intent: Intent): String? {
            return intent.getStringExtra(PASSWORD_RESET_TOKEN_EXTRA)
        }
    }
}