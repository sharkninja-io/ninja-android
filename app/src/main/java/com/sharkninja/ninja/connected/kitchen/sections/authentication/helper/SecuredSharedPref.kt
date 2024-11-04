package com.sharkninja.ninja.connected.kitchen.sections.authentication.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecuredSharedPref {

    companion object {
        private const val TAG = "SecuredSharedPref"
        private const val SECURE_PREF_FILE_NAME = "secure_prefs"
        private lateinit var securePreferences: SharedPreferences
        private var secureObj: SecuredSharedPref? = null

        fun create(context: Context): SecuredSharedPref? {
            // creating alias for security
            val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
            val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

            if(::securePreferences.isInitialized.not()) {
                try {// creating secure shared preferences
                    securePreferences = EncryptedSharedPreferences.create(
                        SECURE_PREF_FILE_NAME,
                        mainKeyAlias,
                        context.applicationContext,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                    secureObj = SecuredSharedPref()
                } catch (e: Exception) {
                    e.printStackTrace()
                    secureObj = null
                }
            }
            return secureObj
        }
    }

    fun putSecureString(key: String, value: String){
        with(securePreferences.edit()){
            putString(key, value)
            apply()
        }
    }
    fun getSecureString(key: String): String {
        return securePreferences.getString(key,"") ?: ""
    }

    fun removeSecureKey(key: String) {
        securePreferences.edit().remove(key).apply()
    }

    fun clear() {
        securePreferences.edit().clear().apply()
    }

}