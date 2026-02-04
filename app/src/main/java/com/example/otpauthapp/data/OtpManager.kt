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
    // save otp for each email
    private val otpMap = mutableMapOf<String, OtpData>()

    companion object {
        const val OTP_LENGTH = 6
        const val EXPIRY_TIME = 60_000L // 60 seconds
        const val MAX_TRIES = 3
    }

    fun generateOtp(email: String): String {
        // random 6 digit code
        val code = (0..999999)
            .random().toString()
            .padStart(OTP_LENGTH, '0')
        
        // save it and reset tries to 0
        otpMap[email] = OtpData(
            code = code,
            timestampMillis = System.currentTimeMillis(),
            attemptCount = 0
        )
        
        return code
    }

    fun validateOtp(email: String, input: String): OtpValidationResult {
        val data = otpMap[email] ?: return OtpValidationResult.NotFound

        // check if expired
        if (isExpired(data.timestampMillis)) {
            removeOtp(email)
            return OtpValidationResult.ExpiredOtp
        }

        // check if code matches
        return if (data.code == input) {
            removeOtp(email) // success, so delete otp
            OtpValidationResult.Success
        } else {
            // wrong code, increase tries
            val newTries = data.attemptCount + 1
            
            if (newTries >= MAX_TRIES) {
                // reached 3 wrong tries, so invalidate it
                removeOtp(email)
                OtpValidationResult.AttemptsExceeded
            } else {
                otpMap[email] = data.copy(attemptCount = newTries)
                OtpValidationResult.InvalidOtp
            }
        }
    }

    fun invalidateOtp(email: String) {
        removeOtp(email)
    }

    private fun removeOtp(email: String) {
        otpMap.remove(email)
    }

    private fun isExpired(time: Long): Boolean {
        return System.currentTimeMillis() - time > EXPIRY_TIME
    }
}
