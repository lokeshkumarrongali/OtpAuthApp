package com.example.otpauthapp.analytics

import timber.log.Timber

class AnalyticsLogger {
    fun logOtpGenerated(email: String, otp: String) {
        Timber.d("🔑 OTP generated for: $email. Code: $otp")
    }

    fun logOtpValidationSuccess(email: String) {
        Timber.i("✅ OTP success for: $email")
    }

    fun logOtpValidationFailure(email: String, reason: String) {
        Timber.w("❌ OTP failed for: $email. Reason: $reason")
    }

    fun logLogout(email: String) {
        Timber.d("🚪 Logout: $email")
    }
}
