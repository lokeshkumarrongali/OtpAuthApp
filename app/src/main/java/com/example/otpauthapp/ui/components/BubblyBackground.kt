package com.example.otpauthapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.otpauthapp.ui.theme.ElectricCyan
import com.example.otpauthapp.ui.theme.GlowingViolet
import com.example.otpauthapp.ui.theme.NeonPink

@Composable
fun BubblyBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Bubble 1: Top Left - Glowing Purple
        drawCircle(
            color = GlowingViolet.copy(alpha = 0.2f),
            radius = width * 0.45f,
            center = Offset(width * 0.1f, height * 0.05f)
        )

        // Bubble 2: Bottom Right - Cyan Glow
        drawCircle(
            color = ElectricCyan.copy(alpha = 0.15f),
            radius = width * 0.55f,
            center = Offset(width * 0.95f, height * 0.95f)
        )

        // Bubble 3: Middle Left - Pink Accent
        drawCircle(
            color = NeonPink.copy(alpha = 0.12f),
            radius = width * 0.35f,
            center = Offset(width * -0.05f, height * 0.55f)
        )

        // Bubble 4: Top Right - Cyan Accent
        drawCircle(
            color = ElectricCyan.copy(alpha = 0.08f),
            radius = width * 0.25f,
            center = Offset(width * 0.9f, height * 0.15f)
        )
        
        // Bubble 5: Bottom Left - Purple Glow
        drawCircle(
            color = GlowingViolet.copy(alpha = 0.1f),
            radius = width * 0.4f,
            center = Offset(width * 0.15f, height * 0.9f)
        )

        // Extra Glow Bubble: Center
        drawCircle(
            color = GlowingViolet.copy(alpha = 0.05f),
            radius = width * 0.6f,
            center = Offset(width * 0.5f, height * 0.5f)
        )
    }
}
