package com.kelasxi.aplikasimonitoringkelas.ui.theme

import androidx.compose.ui.unit.dp

// SMKN 2 BUDURAN SIDOARJO - Design System
// Comprehensive spacing, corner radius, and elevation system

// Design System - Spacing Scale (8dp baseline)
object Spacing {
    val xxs = 2.dp     // Tight spacing - for micro adjustments
    val xs = 4.dp      // Extra small - for close elements
    val sm = 8.dp      // Small - default spacing unit
    val md = 12.dp     // Medium - comfortable spacing
    val lg = 16.dp     // Large - section spacing
    val xl = 24.dp     // Extra large - major sections
    val xxl = 32.dp    // Extra extra large - page margins
    val xxxl = 48.dp   // Massive - hero sections
    val xxxxl = 64.dp  // Maximum - major page sections
}

// Design System - Corner Radius System
object CornerRadius {
    val none = 0.dp
    val small = 8.dp      // Buttons, small cards, badges
    val medium = 12.dp    // Standard cards, text fields
    val large = 16.dp     // Large cards, bottom sheets
    val xlarge = 24.dp    // Modals, hero cards
    val circle = 50.dp    // Circular elements (use with care)
}

// Design System - Elevation System
object Elevation {
    val none = 0.dp
    val small = 2.dp      // Subtle lift - cards at rest
    val medium = 4.dp     // Standard cards - pressed state
    val large = 8.dp      // Floating elements - FABs
    val xlarge = 16.dp    // Modals and overlays
    val xxlarge = 24.dp   // Maximum elevation for critical elements
}

// Design System - Component Dimensions
object Dimensions {
    // Interactive component heights
    val buttonHeight = 48.dp
    val buttonHeightSmall = 36.dp
    val buttonHeightLarge = 56.dp
    val inputFieldHeight = 56.dp
    val toolbarHeight = 64.dp
    val bottomNavHeight = 80.dp
    val tabHeight = 48.dp
    val listItemHeight = 64.dp
    val listItemHeightSmall = 48.dp
    
    // Icon sizes
    val iconSizeSmall = 16.dp
    val iconSizeMedium = 24.dp
    val iconSize = 32.dp        // Default icon size
    val iconSizeLarge = 40.dp
    val iconSizeXLarge = 48.dp
    
    // Avatar sizes
    val avatarSizeSmall = 32.dp
    val avatarSizeMedium = 40.dp
    val avatarSizeLarge = 56.dp
    val avatarSizeXLarge = 80.dp
    
    // Logo and image sizes
    val logoSizeSmall = 80.dp
    val logoSize = 120.dp       // Default logo size for login
    val logoSizeLarge = 160.dp
    
    // Card and surface properties
    val cardElevation = Elevation.small
    val elevationSmall = Elevation.small
    val cardCornerRadius = CornerRadius.medium
    val surfaceCornerRadius = CornerRadius.medium
    
    // Layout dimensions
    val screenPadding = Spacing.lg
    val sectionSpacing = Spacing.xl
    val cardSpacing = Spacing.md
    val contentMaxWidth = 600.dp
    val dialogMaxWidth = 560.dp
    
    // Minimum touch targets (accessibility)
    val minTouchTarget = 48.dp
    val minTouchTargetSmall = 44.dp
    
    // Specific component spacing
    val cardPadding = Spacing.lg
    val itemSpacing = Spacing.sm
}

// Design System - Animation durations
object AnimationDuration {
    const val short = 200
    const val medium = 300
    const val long = 500
}