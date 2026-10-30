package com.mokkacoding.clipster.ui.theme

import android.os.Build
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDirection
import com.google.android.material.color.MaterialColors
import com.mokkacoding.clipster.ui.common.LocalFixedColorRoles
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.dynamicColorScheme

fun Color.applyOpacity(enabled: Boolean): Color {
    return if (enabled) this else this.copy(alpha = 0.62f)
}

@Composable
@ReadOnlyComposable
fun Color.harmonizeWith(other: Color) =
    Color(MaterialColors.harmonize(this.toArgb(), other.toArgb()))

@Composable
@ReadOnlyComposable
fun Color.harmonizeWithPrimary(): Color =
    this.harmonizeWith(other = MaterialTheme.colorScheme.primary)

@Composable
fun ClipsterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isHighContrastModeEnabled: Boolean = false,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current

    LaunchedEffect(darkTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (darkTheme) {
                view.windowInsetsController?.setSystemBarsAppearance(
                    0,
                    APPEARANCE_LIGHT_STATUS_BARS,
                )
            } else {
                view.windowInsetsController?.setSystemBarsAppearance(
                    APPEARANCE_LIGHT_STATUS_BARS,
                    APPEARANCE_LIGHT_STATUS_BARS,
                )
            }
        }
    }

    val colorScheme =
        dynamicColorScheme(!darkTheme).run {
            if (isHighContrastModeEnabled && darkTheme)
                copy(
                    surface = Color.Black,
                    background = Color.Black,
                    surfaceContainerLowest = Color.Black,
                    surfaceContainerLow = surfaceContainerLowest,
                    surfaceContainer = surfaceContainerLow,
                    surfaceContainerHigh = surfaceContainerLow,
                    surfaceContainerHighest = surfaceContainer,
                )
            else this
        }.applyGlassmorphism(darkTheme)

    val textStyle =
        LocalTextStyle.current.copy(
            lineBreak = LineBreak.Paragraph,
            textDirection = TextDirection.Content,
        )

    val tonalPalettes = LocalTonalPalettes.current

    CompositionLocalProvider(
        LocalFixedColorRoles provides FixedColorRoles.fromTonalPalettes(tonalPalettes),
        LocalTextStyle provides textStyle,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}

fun androidx.compose.material3.ColorScheme.applyGlassmorphism(isDark: Boolean) = copy(
    // Canvas / Page Background (Obsidian Titanium vs Ivory Porcelain)
    background = if (isDark) Color(0xFF090A0F) else Color(0xFFF5F7FA),
    onBackground = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1A202C),

    // Primary Surface (cards, dialogs, sheets)
    surface = if (isDark) Color(0xFF10121A).copy(alpha = 0.85f) else Color(0xFFFFFFFF),
    onSurface = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1A202C),

    // Surface Containers (sections, grouped content)
    surfaceContainer = if (isDark) Color(0xFF171924).copy(alpha = 0.85f) else Color(0xFFFFFFFF),
    surfaceContainerLow = if (isDark) Color(0xFF0D0E15).copy(alpha = 0.80f) else Color(0xFFF0F3F8),
    surfaceContainerHigh = if (isDark) Color(0xFF1E2130).copy(alpha = 0.90f) else Color(0xFFFFFFFF),

    // Surface Variant (chips, tabs, secondary cards)
    surfaceVariant = if (isDark) Color(0xFF141620) else Color(0xFFEEF2F7),
    onSurfaceVariant = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),

    // Outlines / Dividers
    outline = if (isDark) Color(0xFF282C3F) else Color(0xFFE2E8F0),
    outlineVariant = if (isDark) Color(0xFF1A1C28) else Color(0xFFF0F3F8)
)

@Composable
@Deprecated("Use ClipsterTheme instead", replaceWith = ReplaceWith("ClipsterTheme(content)"))
fun PreviewThemeLight(content: @Composable () -> Unit) {
    ClipsterTheme(darkTheme = false, content = content)
}
