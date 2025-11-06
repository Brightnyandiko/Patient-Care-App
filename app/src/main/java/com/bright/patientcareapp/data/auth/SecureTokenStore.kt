package com.bright.patientcareapp.data.auth

import android.content.Context
import androidx.security.crypto.MasterKeys
import androidx.security.crypto.EncryptedSharedPreferences
//import androidx.security.crypto.MasterKeys
import com.bright.patientcareapp.data.remote.model.AuthToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.getValue

@Singleton
class SecureTokenStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPrefs by lazy {
        EncryptedSharedPreferences.create(
            "patient_care_secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun saveToken(token: AuthToken) = withContext(Dispatchers.IO) {
        sharedPrefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, token.token)
            putInt(KEY_USER_ID, token.userId)
            putString(KEY_USER_EMAIL, token.userEmail)
            putString(KEY_USER_NAME, token.userName)
        }.apply()
    }

    suspend fun getToken(): AuthToken? = withContext(Dispatchers.IO) {
        val token = sharedPrefs.getString(KEY_ACCESS_TOKEN, null)
        val userId = sharedPrefs.getInt(KEY_USER_ID, -1)
        val userEmail = sharedPrefs.getString(KEY_USER_EMAIL, null)
        val userName = sharedPrefs.getString(KEY_USER_NAME, null)

        if (token != null && userId != -1 && userEmail != null && userName != null) {
            AuthToken(token, userId, userEmail, userName)
        } else null
    }

    suspend fun clearToken() = withContext(Dispatchers.IO) {
        sharedPrefs.edit().clear().apply()
    }

    suspend fun hasToken(): Boolean = withContext(Dispatchers.IO) {
        sharedPrefs.contains(KEY_ACCESS_TOKEN)
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }
}