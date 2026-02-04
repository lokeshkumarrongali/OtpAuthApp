package com.example.otpauthapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.otpauthapp.ui.theme.AccentCyan
import com.example.otpauthapp.ui.theme.AccentPink
import com.example.otpauthapp.ui.theme.PrimaryPurple

@Composable
fun BubblyBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height


        drawCircle(
            color = PrimaryPurple.copy(alpha = 0.2f),
            radius = w * 0.45f,
            center = Offset(w * 0.1f, h * 0.05f)
        )


        drawCircle(
            color = AccentCyan.copy(alpha = 0.15f),
            radius = w * 0.55f,
            center = Offset(w * 0.95f, h * 0.95f)
        )
        drawCircle(
            color = AccentPink.copy(alpha = 0.12f),
            radius = w * 0.35f,
            center = Offset(w * -0.05f, h * 0.55f)
        )

        drawCircle(
            color = AccentCyan.copy(alpha = 0.08f),
            radius = w * 0.25f,
            center = Offset(w * 0.9f, h * 0.15f)
        )
        

        drawCircle(
            color = PrimaryPurple.copy(alpha = 0.1f),
            radius = w * 0.4f,
            center = Offset(w * 0.15f, h * 0.9f)
        )

        drawCircle(
            color = PrimaryPurple.copy(alpha = 0.05f),
            radius = w * 0.6f,
            center = Offset(w * 0.5f, h * 0.5f)
        )
    }
}
