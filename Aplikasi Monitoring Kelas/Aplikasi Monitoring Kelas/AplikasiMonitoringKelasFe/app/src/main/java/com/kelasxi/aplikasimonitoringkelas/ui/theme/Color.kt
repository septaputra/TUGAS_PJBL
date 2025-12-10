package com.kelasxi.aplikasimonitoringkelas.ui.theme

import androidx.compose.ui.graphics.Color

// SMKN 2 BUDURAN SIDOARJO - Color System
// Extracted from school logo with professional education palette

// Primary Colors (from logo SMKN 2 BUDURAN SIDOARJO)
val SMKPrimary = Color(0xFF1565C0)          // Deep blue - main logo color
val SMKPrimaryLight = Color(0xFF5E92F3)     // Light blue variant
val SMKPrimaryDark = Color(0xFF003C8F)      // Dark blue variant
val SMKPrimaryContainer = Color(0xFFBBDEFB) // Light blue container

val SMKSecondary = Color(0xFF0D47A1)        // Navy blue - secondary logo color
val SMKSecondaryLight = Color(0xFF5472D3)   // Light navy variant
val SMKSecondaryDark = Color(0xFF002171)    // Dark navy variant
val SMKSecondaryContainer = Color(0xFFE1F5FE) // Light navy container

val SMKAccent = Color(0xFF00BCD4)           // Cyan - accent from logo
val SMKAccentLight = Color(0xFF62EFFF)      // Light cyan variant
val SMKAccentDark = Color(0xFF008BA3)       // Dark cyan variant
val SMKAccentContainer = Color(0xFFB2EBF2) // Light cyan container

// Supporting Colors - Professional Education Palette
val SMKBackground = Color(0xFFF8FAFC)       // Soft background
val SMKSurface = Color(0xFFFFFFFF)          // Clean white surface
val SMKSurfaceVariant = Color(0xFFF1F5F9)   // Subtle surface variant
val SMKOnSurface = Color(0xFF1E293B)        // Primary text
val SMKOnSurfaceVariant = Color(0xFF64748B) // Secondary text
val SMKOutline = Color(0xFFCBD5E1)          // Subtle borders
val SMKOutlineVariant = Color(0xFFE2E8F0)   // Light borders

// Status Colors - Semantic meanings
val SMKSuccess = Color(0xFF059669)          // Green for success/completed
val SMKSuccessLight = Color(0xFF34D399)     // Light success
val SMKSuccessContainer = Color(0xFFD1FAE5) // Success container

val SMKWarning = Color(0xFFD97706)          // Orange for warnings/pending  
val SMKWarningLight = Color(0xFFFBBF24)     // Light warning
val SMKWarningContainer = Color(0xFFFEF3C7) // Warning container

val SMKError = Color(0xFFDC2626)            // Red for errors/urgent
val SMKErrorLight = Color(0xFFF87171)       // Light error
val SMKErrorContainer = Color(0xFFFEE2E2)   // Error container

val SMKInfo = Color(0xFF2563EB)             // Blue for info/general
val SMKInfoLight = Color(0xFF60A5FA)        // Light info
val SMKInfoContainer = Color(0xFFDDEAFE)    // Info container

// Gradient Colors for enhanced visual appeal
val SMKGradientStart = SMKPrimary
val SMKGradientEnd = SMKAccent
val SMKGradientMid = Color(0xFF1976D2)

// Neutral Grays - Enhanced hierarchy
val NeutralGray50 = Color(0xFFF8FAFC)      // Lightest background
val NeutralGray100 = Color(0xFFF1F5F9)     // Light background
val NeutralGray200 = Color(0xFFE2E8F0)     // Border light
val NeutralGray300 = Color(0xFFCBD5E1)     // Border medium  
val NeutralGray400 = Color(0xFF94A3B8)     // Text disabled
val NeutralGray500 = Color(0xFF64748B)     // Text secondary
val NeutralGray600 = Color(0xFF475569)     // Text primary light
val NeutralGray700 = Color(0xFF334155)     // Text primary
val NeutralGray800 = Color(0xFF1E293B)     // Text dark
val NeutralGray900 = Color(0xFF0F172A)     // Text darkest

// Legacy compatibility - mapped to new system
val SchoolBlue80 = SMKPrimaryContainer     // Light cyan blue
val SchoolTeal80 = SMKAccentContainer      // Light teal
val SchoolGreen80 = SMKSuccessContainer    // Light green accent

val SchoolBlue40 = SMKPrimary              // Primary blue
val SchoolTeal40 = SMKAccent               // Secondary teal
val SchoolGreen40 = SMKSuccess             // Success green

// Additional semantic colors (legacy)
val SuccessLight = SMKSuccessContainer
val WarningLight = SMKWarningContainer
val ErrorLight = SMKErrorContainer  
val InfoLight = SMKInfoContainer

val SuccessDark = SMKSuccess
val WarningDark = SMKWarning
val ErrorDark = SMKError
val InfoDark = SMKInfo

// Neutral grays (legacy compatibility)
val NeutralGray10 = NeutralGray50          // Surface variant light
val NeutralGray20 = NeutralGray100         // Background light
val NeutralGray90 = NeutralGray800         // On surface dark
val NeutralGray80 = NeutralGray700         // On surface variant dark

// Legacy colors for backward compatibility
val Purple80 = SchoolBlue80
val PurpleGrey80 = SchoolTeal80
val Pink80 = SchoolGreen80

val Purple40 = SchoolBlue40
val PurpleGrey40 = SchoolTeal40
val Pink40 = SchoolGreen40

// Legacy aliases for semantic colors (to maintain compatibility with existing code)
val ErrorRed = SMKError
val SuccessGreen = SMKSuccess
val WarningYellow = SMKWarning
val SMKOnPrimary = Color(0xFFFFFFFF)  // White color for text on primary color