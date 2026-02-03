package com.example.otpauthapp.data

sealed class OtpValidationResult {
    object Success : OtpValidationResult()
    object InvalidOtp : OtpValidationResult()
    object ExpiredOtp : OtpValidationResult()
    object AttemptsExceeded : OtpValidationResult()
    object NotFound : OtpValidationResult()
}

data class OtpData(
    val code: String,
    val timestampMillis: Long,
    val attemptCount: Int = 0
)

class OtpManager {
    // In-memory storage: Email -> OtpData
    private val otpStorage = mutableMapOf<String, OtpData>()

    companion object {
        const val OTP_LENGTH = 6
        const val EXPIRY_DURATION_MS = 60_000L // 60 seconds
        const val MAX_ATTEMPTS = 3
    }

    fun generateOtp(email: String): String {
        // Generate a random 6-digit numeric OTP with leading zeros
        val otpCode = (0..999999)
            .random().toString()
            .padStart(OTP_LENGTH, '0')
        
        // Store with current timestamp and reset attempts
        otpStorage[email] = OtpData(
            code = otpCode,
            timestampMillis = System.currentTimeMillis(),
            attemptCount = 0
        )
        
        return otpCode
    }

    fun validateOtp(email: String, input: String): OtpValidationResult {
        val storedData = otpStorage[email] ?: return OtpValidationResult.NotFound

        // Expiry check
        if (isExpired(storedData.timestampMillis)) {
            return OtpValidationResult.ExpiredOtp
        }

        // Attempt limit check
        if (storedData.attemptCount >= MAX_ATTEMPTS) {
            return OtpValidationResult.AttemptsExceeded
        }

        // Validation
        return if (storedData.code == input) {
            invalidateOtp(email)
            OtpValidationResult.Success
        } else {
            val updatedData = storedData.copy(attemptCount = storedData.attemptCount + 1)
            otpStorage[email] = updatedData
            OtpValidationResult.InvalidOtp
        }
    }

    fun invalidateOtp(email: String) {
        otpStorage.remove(email)
    }

    private fun isExpired(timestampMillis: Long): Boolean {
        return System.currentTimeMillis() - timestampMillis > EXPIRY_DURATION_MS
    }
}
