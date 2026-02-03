package com.example.otpauthapp.viewmodel

sealed class AuthState {
    object EmailInput : AuthState()
    data class OtpInput(val email: String, val generatedAtMs: Long) : AuthState()
    object Loading : AuthState()
    data class Session(
        val startTimeFormatted: String,
        val elapsedDisplay: String
    ) : AuthState()
    
    sealed class Error : AuthState() {
        data class InvalidOtp(val email: String, val generatedAtMs: Long? = null) : Error()
        data class ExpiredOtp(val email: String) : Error()
        data class AttemptsExceeded(val email: String) : Error()
        data class General(val message: String) : Error()
    }
}
