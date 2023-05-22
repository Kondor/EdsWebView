package com.example.appwebgps.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.appwebgps.utils.StringConstants

class PreferenceHelper(context: Context) {

    private val preferenceName = StringConstants.prefsName
    private var sharedPref: SharedPreferences
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val editor: SharedPreferences.Editor

    init {
        sharedPref = EncryptedSharedPreferences.create(
            preferenceName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        editor = sharedPref.edit()
    }

    fun saveString(name: String, value: String?) {
        editor.putString(name, value).apply()
    }

    fun removeString(name: String) {
        editor.remove(name).apply()
    }

    fun getString(name: String) = sharedPref.getString(name, null)

    fun saveBoolean(name: String, value: Boolean) {
        editor.putBoolean(name, value).apply()
    }

    fun getBoolean(name: String) = sharedPref.getBoolean(name, false)

    fun removeBoolean(name: String) = editor.remove(name)
}