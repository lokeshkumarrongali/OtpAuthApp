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
    private val logger = AnalyticsLogger()
    private var timerJob: Job? = null
    private var lastEmail: String? = null


    private val _uiState = mutableStateOf<AuthState>(AuthState.EmailInput)
    val uiState: State<AuthState> = _uiState

    init {

        if (sessionManager.isLoggedIn()) {
            val email = sessionManager.getSessionEmail() ?: ""
            val startTime = sessionManager.getSessionStartTime()
            val timeStr = formatTime(startTime)
            
            _uiState.value = AuthState.Session(
                startTimeFormatted = timeStr,
                elapsedDisplay = calculateElapsed(startTime)
            )
            startSessionTimer(startTime)
            logger.logOtpValidationSuccess(email)
        }
    }

    private var otpTime: Long? = null

    fun onEmailSubmitted(email: String) {
        lastEmail = email
        val code = otpManager.generateOtp(email)
        logger.logOtpGenerated(email, code)
        val now = System.currentTimeMillis()
        otpTime = now
        _uiState.value = AuthState.OtpInput(email, now)
    }

    fun onOtpSubmitted(email: String, otp: String) {
        val result = otpManager.validateOtp(email, otp)
        _uiState.value = when (result) {
            is OtpValidationResult.Success -> {
                logger.logOtpValidationSuccess(email)
                val startTime = System.currentTimeMillis()
                sessionManager.saveSession(email, startTime)
                
                startSessionTimer(startTime)
                AuthState.Session(
                    startTimeFormatted = formatTime(startTime),
                    elapsedDisplay = "00:00"
                )
            }
            is OtpValidationResult.InvalidOtp -> {
                logger.logOtpValidationFailure(email, "Invalid OTP")
                AuthState.Error.InvalidOtp(email, otpTime)
            }
            is OtpValidationResult.ExpiredOtp -> {
                logger.logOtpValidationFailure(email, "OTP Expired")
                AuthState.Error.ExpiredOtp(email)
            }
            is OtpValidationResult.AttemptsExceeded -> {
                logger.logOtpValidationFailure(email, "Attempts Exceeded")
                AuthState.Error.AttemptsExceeded(email)
            }
            is OtpValidationResult.NotFound -> {
                logger.logOtpValidationFailure(email, "Not Found")
                AuthState.Error.General("No OTP found")
            }
        }
    }

    fun onResendOtp(email: String) {
        val code = otpManager.generateOtp(email)
        logger.logOtpGenerated(email, code)
        val now = System.currentTimeMillis()
        otpTime = now
        _uiState.value = AuthState.OtpInput(email, now)
    }

    fun onLogoutClicked() {
        val state = _uiState.value
        if (state is AuthState.Session) {
            val email = sessionManager.getSessionEmail() ?: "unknown"
            logger.logLogout(email)
        }
        sessionManager.clearSession()
        stopSessionTimer()
        _uiState.value = AuthState.EmailInput
    }

    private fun startSessionTimer(startTime: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val elapsed = calculateElapsed(startTime)
                
                val currentState = _uiState.value
                if (currentState is AuthState.Session) {
                    _uiState.value = currentState.copy(elapsedDisplay = elapsed)
                } else {
                    break
                }
            }
        }
    }

    private fun calculateElapsed(startTime: Long): String {
        val secondsTotal = (System.currentTimeMillis() - startTime) / 1000
        val mins = secondsTotal / 60
        val secs = secondsTotal % 60
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
    }

    private fun stopSessionTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun formatTime(time: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(time))
    }
}
