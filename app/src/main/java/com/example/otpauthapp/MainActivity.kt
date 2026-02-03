package com.example.otpauthapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.otpauthapp.ui.LoginScreen
import com.example.otpauthapp.ui.OtpScreen
import com.example.otpauthapp.ui.SessionScreen
import com.example.otpauthapp.ui.theme.OtpAuthAppTheme
import com.example.otpauthapp.viewmodel.AuthState
import com.example.otpauthapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OtpAuthAppTheme {
                val viewModel: AuthViewModel = viewModel()
                val state by viewModel.uiState

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val modifier = Modifier.padding(innerPadding)
                    
                    when (val currentState = state) {
                        is AuthState.EmailInput, is AuthState.Loading -> {
                            if (state is AuthState.EmailInput || state is AuthState.Loading) {
                                LoginScreen(
                                    state = state,
                                    onEmailSubmitted = { viewModel.onEmailSubmitted(it) }
                                )
                            }
                        }
                        is AuthState.OtpInput -> {
                            OtpScreen(
                                state = state,
                                onOtpSubmitted = { email, otp -> viewModel.onOtpSubmitted(email, otp) },
                                onResendOtp = { viewModel.onResendOtp(it) }
                            )
                        }
                        is AuthState.Session -> {
                            SessionScreen(
                                state = state,
                                onLogoutClicked = { viewModel.onLogoutClicked() }
                            )
                        }
                        is AuthState.Error -> {
                            // Determine which screen to show based on previous context
                            // If we were at OTP stage, stay there. Otherwise Login.
                            // In our state flow, OTP errors stay on OTP screen.
                            // General errors might go to Login.
                            if (currentState is AuthState.Error.General) {
                                LoginScreen(
                                    state = state,
                                    onEmailSubmitted = { viewModel.onEmailSubmitted(it) }
                                )
                            } else {
                                OtpScreen(
                                    state = state,
                                    onOtpSubmitted = { email, otp -> viewModel.onOtpSubmitted(email, otp) },
                                    onResendOtp = { viewModel.onResendOtp(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}