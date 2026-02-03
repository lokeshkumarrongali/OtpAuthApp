package com.example.otpauthapp.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.otpauthapp.analytics.AnalyticsLogger
import com.example.otpauthapp.data.OtpManager
import com.example.otpauthapp.data.OtpValidationResult
import com.example.otpauthapp.data.SessionManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val otpManager = OtpManager()
    private val sessionManager = SessionManager(application)
    private val analyticsLogger = AnalyticsLogger()
    private var timerJob: Job? = null
    private var lastEmailUsed: String? = null

    // Single immutable UI state exposure
    private val _uiState = mutableStateOf<AuthState>(AuthState.EmailInput)
    val uiState: State<AuthState> = _uiState

    init {
        // Restore session if it exists
        if (sessionManager.isLoggedIn()) {
            val email = sessionManager.getSessionEmail() ?: ""
            val startTimeMs = sessionManager.getSessionStartTime()
            val startTimeFormatted = formatTime(startTimeMs)
            
            _uiState.value = AuthState.Session(
                startTimeFormatted = startTimeFormatted,
                elapsedDisplay = calculateElapsed(startTimeMs)
            )
            startSessionTimer(startTimeMs)
            analyticsLogger.logOtpValidationSuccess(email) // or log session restored
        }
    }

    private var currentOtpTimestampMs: Long? = null

    // Action handlers
    fun onEmailSubmitted(email: String) {
        lastEmailUsed = email
        val otp = otpManager.generateOtp(email)
        analyticsLogger.logOtpGenerated(email, otp)
        val timestamp = System.currentTimeMillis()
        currentOtpTimestampMs = timestamp
        _uiState.value = AuthState.OtpInput(email, timestamp)
    }

    fun onOtpSubmitted(email: String, otp: String) {
        val result = otpManager.validateOtp(email, otp)
        _uiState.value = when (result) {
            is OtpValidationResult.Success -> {
                analyticsLogger.logOtpValidationSuccess(email)
                val startTimeMs = System.currentTimeMillis()
                sessionManager.saveSession(email, startTimeMs)
                
                startSessionTimer(startTimeMs)
                AuthState.Session(
                    startTimeFormatted = formatTime(startTimeMs),
                    elapsedDisplay = "00:00"
                )
            }
            is OtpValidationResult.InvalidOtp -> {
                analyticsLogger.logOtpValidationFailure(email, "Invalid OTP")
                AuthState.Error.InvalidOtp(email, currentOtpTimestampMs)
            }
            is OtpValidationResult.ExpiredOtp -> {
                analyticsLogger.logOtpValidationFailure(email, "OTP Expired")
                AuthState.Error.ExpiredOtp(email)
            }
            is OtpValidationResult.AttemptsExceeded -> {
                analyticsLogger.logOtpValidationFailure(email, "Attempts Exceeded")
                AuthState.Error.AttemptsExceeded(email)
            }
            is OtpValidationResult.NotFound -> {
                analyticsLogger.logOtpValidationFailure(email, "Not Found")
                AuthState.Error.General("No OTP found")
            }
        }
    }

    fun onResendOtp(email: String) {
        val otp = otpManager.generateOtp(email)
        analyticsLogger.logOtpGenerated(email, otp)
        val timestamp = System.currentTimeMillis()
        currentOtpTimestampMs = timestamp
        _uiState.value = AuthState.OtpInput(email, timestamp)
    }

    fun onLogoutClicked() {
        val currentState = _uiState.value
        if (currentState is AuthState.Session) {
            // we don't have email in Session state, but we can get it from manager
            val email = sessionManager.getSessionEmail() ?: "unknown"
            analyticsLogger.logLogout(email)
        }
        sessionManager.clearSession()
        stopSessionTimer()
        _uiState.value = AuthState.EmailInput
    }

    private fun startSessionTimer(startTimeMs: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val elapsedDisplay = calculateElapsed(startTimeMs)
                
                val currentState = _uiState.value
                if (currentState is AuthState.Session) {
                    _uiState.value = currentState.copy(elapsedDisplay = elapsedDisplay)
                } else {
                    break
                }
            }
        }
    }

    private fun calculateElapsed(startTimeMs: Long): String {
        val elapsedSeconds = (System.currentTimeMillis() - startTimeMs) / 1000
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun stopSessionTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun formatTime(timeMs: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timeMs))
    }

    private fun getCurrentTimeFormatted(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
