package com.kelasxi.aplikasimonitoringkelas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// SMKN 2 BUDURAN SIDOARJO - Dark Theme
private val SMKNDarkColorScheme = darkColorScheme(
    primary = SMKPrimaryLight,
    onPrimary = NeutralGray900,
    primaryContainer = SMKPrimary,
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,
    
    secondary = SMKSecondaryLight,
    onSecondary = NeutralGray900,
    secondaryContainer = SMKSecondary,
    onSecondaryContainer = androidx.compose.ui.graphics.Color.White,
    
    tertiary = SMKAccentLight,
    onTertiary = NeutralGray900,
    tertiaryContainer = SMKAccent,
    onTertiaryContainer = androidx.compose.ui.graphics.Color.White,
    
    background = NeutralGray900,
    onBackground = NeutralGray100,
    surface = NeutralGray800,
    onSurface = NeutralGray100,
    surfaceVariant = NeutralGray700,
    onSurfaceVariant = NeutralGray300,
    
    outline = NeutralGray500,
    outlineVariant = NeutralGray600,
    
    error = SMKErrorLight,
    onError = NeutralGray900,
    errorContainer = SMKError,
    onErrorContainer = androidx.compose.ui.graphics.Color.White
)

// SMKN 2 BUDURAN SIDOARJO - Light Theme (Primary)
private val SMKNLightColorScheme = lightColorScheme(
    primary = SMKPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = SMKPrimaryContainer,
    onPrimaryContainer = SMKPrimaryDark,
    
    secondary = SMKSecondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = SMKSecondaryContainer,
    onSecondaryContainer = SMKSecondaryDark,
    
    tertiary = SMKAccent,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = SMKAccentContainer,
    onTertiaryContainer = SMKAccentDark,
    
    background = SMKBackground,
    onBackground = SMKOnSurface,
    surface = SMKSurface,
    onSurface = SMKOnSurface,
    surfaceVariant = SMKSurfaceVariant,
    onSurfaceVariant = SMKOnSurfaceVariant,
    
    outline = SMKOutline,
    outlineVariant = SMKOutlineVariant,
    
    error = SMKError,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = SMKErrorContainer,
    onErrorContainer = SMKError,
    
    // Status colors integration
    inverseSurface = SMKOnSurface,
    inverseOnSurface = SMKSurface,
    inversePrimary = SMKPrimaryLight
)

// Legacy schemes for backward compatibility
private val DarkColorScheme = SMKNDarkColorScheme
private val LightColorScheme = SMKNLightColorScheme

// Main color schemes
private val SchoolDarkColorScheme = SMKNDarkColorScheme
private val SchoolLightColorScheme = SMKNLightColorScheme

/**
 * SMKN 2 BUDURAN SIDOARJO - Main Theme
 * 
 * Professional education application theme with:
 * - School branding colors extracted from logo
 * - Consistent Material 3 design system
 * - Enhanced typography for readability
 * - Accessibility-compliant color contrast
 * 
 * @param darkTheme Enable dark mode (follows system by default)
 * @param dynamicColor Use Material You dynamic colors (disabled for branding consistency)
 * @param content Content to be themed
 */
@Composable
fun AplikasiMonitoringKelasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color untuk konsistensi dengan branding sekolah
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> SMKNDarkColorScheme
        else -> SMKNLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Preview-friendly theme variant for development
 */
@Composable
fun SMKNPreviewTheme(
    content: @Composable () -> Unit
) {
    AplikasiMonitoringKelasTheme(
        darkTheme = false,
        dynamicColor = false,
        content = content
    )
}