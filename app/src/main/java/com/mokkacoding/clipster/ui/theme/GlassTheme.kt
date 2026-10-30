package com.mokkacoding.clipster.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Adaptive Glassmorphism / Neomorphism Card Modifier.
 * In Dark Mode: Sleek Apple Obsidian Glass.
 * In Light Mode: Ultra-Luxury Tactile Porcelain Neomorphism (Soft 3D).
 */
@Composable
fun Modifier.glassCard(
    cornerRadius: Dp = 20.dp,
    glassAlpha: Float = 0.72f,
    tintColor: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null
): Modifier {
    val isDark = isAppInDarkTheme()

    var modifier = if (isDark) {
        this.liquidGlass(
            cornerRadius = cornerRadius,
            glassAlpha = glassAlpha,
            tintColor = tintColor,
            elevation = 8.dp
        )
    } else {
        this.neomorphicConvex(
            cornerRadius = cornerRadius,
            shape = RoundedCornerShape(cornerRadius),
            elevation = 5.dp,
            accentTint = tintColor
        )
    }

    if (onClick != null) {
        modifier = modifier.liquidGlassPressable(onClick = onClick)
    }
    return modifier
}

