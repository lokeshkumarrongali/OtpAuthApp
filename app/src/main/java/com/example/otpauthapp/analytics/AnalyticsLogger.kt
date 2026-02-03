package com.example.otpauthapp.analytics

import timber.log.Timber

class AnalyticsLogger {
    fun logOtpGenerated(email: String, otp: String) {
        Timber.d("🔑 [AUTH_EVENT] OTP generated for email: $email. Code: $otp")
    }

    fun logOtpValidationSuccess(email: String) {
        Timber.i("✅ [AUTH_EVENT] OTP validation successful for email: $email")
    }

    fun logOtpValidationFailure(email: String, reason: String) {
        Timber.w("❌ [AUTH_EVENT] OTP validation failed for email: $email. Reason: $reason")
    }

    fun logLogout(email: String) {
        Timber.d("🚪 [AUTH_EVENT] User logged out: $email")
    }
}
