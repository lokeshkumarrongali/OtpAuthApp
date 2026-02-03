package com.example.otpauthapp.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "otp_auth_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_START_TIME = "start_time_ms"
        private const val KEY_EMAIL = "user_email"
    }

    fun saveSession(email: String, startTimeMs: Long) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_EMAIL, email)
            putLong(KEY_START_TIME, startTimeMs)
            apply()
        }
    }

    fun getSessionEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun getSessionStartTime(): Long = prefs.getLong(KEY_START_TIME, 0L)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
