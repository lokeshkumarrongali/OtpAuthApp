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

    private val otpMap = mutableMapOf<String, OtpData>()

    companion object {
        const val OTP_LENGTH = 6
        const val EXPIRY_TIME = 60_000L
        const val MAX_TRIES = 3
    }

    fun generateOtp(email: String): String {
        val code = (0..999999)
            .random().toString()
            .padStart(OTP_LENGTH, '0')
        
        otpMap[email] = OtpData(
            code = code,
            timestampMillis = System.currentTimeMillis(),
            attemptCount = 0
        )
        
        return code
    }

    fun validateOtp(email: String, input: String): OtpValidationResult {
        val data = otpMap[email] ?: return OtpValidationResult.NotFound

        if (isExpired(data.timestampMillis)) {
            removeOtp(email)
            return OtpValidationResult.ExpiredOtp
        }


        return if (data.code == input) {
            removeOtp(email)
            OtpValidationResult.Success
        } else {

            val newTries = data.attemptCount + 1
            
            if (newTries >= MAX_TRIES) {

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
