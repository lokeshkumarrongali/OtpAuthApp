package com.example.otpauthapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.otpauthapp.viewmodel.AuthState
import com.example.otpauthapp.ui.components.BubblyBackground
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    state: AuthState,
    onOtpSubmitted: (String, String) -> Unit,
    onResendOtp: (String) -> Unit
) {
    var otp by rememberSaveable { mutableStateOf("") }
    val loading = state is AuthState.Loading
    
    val email = when (state) {
        is AuthState.OtpInput -> state.email
        is AuthState.Error.InvalidOtp -> state.email
        is AuthState.Error.ExpiredOtp -> state.email
        is AuthState.Error.AttemptsExceeded -> state.email
        else -> "" 
    }

    val startTime = when (state) {
        is AuthState.OtpInput -> state.generatedAtMs
        is AuthState.Error.InvalidOtp -> state.generatedAtMs
        else -> null
    }

    var timeLeft by remember { mutableStateOf(60) }

    LaunchedEffect(startTime) {
        if (startTime != null) {
            while (true) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                timeLeft = (60 - elapsed).toInt().coerceAtLeast(0)
                if (timeLeft <= 0) break
                delay(1000)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BubblyBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (email.isNotBlank()) {
                Text(
                    text = "Enter OTP sent to $email",
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it },
                label = { Text("OTP") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,
                singleLine = true
            )



            if (startTime != null && timeLeft > 0) {
                Text(
                    text = "Expires in: ${timeLeft}s",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (timeLeft < 10) Color.Red else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state is AuthState.Error) {
                val msg = when (state) {
                    is AuthState.Error.InvalidOtp -> "Invalid OTP"
                    is AuthState.Error.ExpiredOtp -> "OTP Expired"
                    is AuthState.Error.AttemptsExceeded -> "Too many attempts"
                    is AuthState.Error.General -> state.message
                }
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onOtpSubmitted(email, otp) },
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = !loading && otp.length == 6,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Verify OTP", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { 
                    otp = ""
                    onResendOtp(email) 
                },
                enabled = !loading && email.isNotBlank()
            ) {
                Text("Resend OTP")
            }
        }
    }
}
