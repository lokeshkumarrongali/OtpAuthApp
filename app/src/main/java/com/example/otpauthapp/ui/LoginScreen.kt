package com.example.otpauthapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.otpauthapp.ui.components.BubblyBackground
import com.example.otpauthapp.viewmodel.AuthState

@Composable
fun LoginScreen(
    state: AuthState,
    onEmailSubmitted: (String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    val loading = state is AuthState.Loading
    
    // real time validation
    val isValid = email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val error = email.isNotBlank() && !isValid

    Box(modifier = Modifier.fillMaxSize()) {
        BubblyBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,
                isError = error,
                supportingText = {
                    if (error) {
                        Text("Please enter a valid email address")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onEmailSubmitted(email) },
                modifier = Modifier.fillMaxWidth(0.4f),
                enabled = !loading && email.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Send OTP", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
