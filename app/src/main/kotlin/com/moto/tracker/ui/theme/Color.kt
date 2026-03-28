package com.moto.tracker.ui.theme

import androidx.compose.ui.graphics.Color

// Brand palette — deep slate + vivid amber
object MotoColors {
    // Primary — deep indigo-slate
    val Primary10 = Color(0xFF0D1440)
    val Primary20 = Color(0xFF1A237E)
    val Primary30 = Color(0xFF283593)
    val Primary40 = Color(0xFF3949AB)
    val Primary80 = Color(0xFF9FA8DA)
    val Primary90 = Color(0xFFCFD3F0)
    val Primary95 = Color(0xFFE8EAF6)
    val Primary99 = Color(0xFFF5F5FF)
    val Primary100 = Color(0xFFFFFFFF)

    // Secondary — amber / saffron (Indian accent)
    val Secondary10 = Color(0xFF4A1800)
    val Secondary20 = Color(0xFF7A3000)
    val Secondary30 = Color(0xFFBF4F00)
    val Secondary40 = Color(0xFFE65100)
    val Secondary50 = Color(0xFFFF6D00)
    val Secondary60 = Color(0xFFFF8F00)
    val Secondary80 = Color(0xFFFFCC80)
    val Secondary90 = Color(0xFFFFE0B2)
    val Secondary95 = Color(0xFFFFF3E0)

    // Tertiary — teal (for fuel/chart accents)
    val Tertiary40 = Color(0xFF00838F)
    val Tertiary80 = Color(0xFF80DEEA)
    val Tertiary90 = Color(0xFFB2EBF2)

    // Status colors
    val Overdue = Color(0xFFD32F2F)
    val OverdueContainer = Color(0xFFFFEBEE)
    val DueSoon = Color(0xFFF57F17)
    val DueSoonContainer = Color(0xFFFFF8E1)
    val Ok = Color(0xFF2E7D32)
    val OkContainer = Color(0xFFE8F5E9)

    // Neutrals
    val Surface = Color(0xFFFAFAFF)
    val SurfaceDark = Color(0xFF121218)
    val Background = Color(0xFFF0F2FF)
    val BackgroundDark = Color(0xFF0A0A12)

    // Card gradient endpoints
    val CardGradientStart = Color(0xFF1E2A78)
    val CardGradientEnd = Color(0xFF3D5AFE)

    // Fuel card gradient
    val FuelGradientStart = Color(0xFF004D40)
    val FuelGradientEnd = Color(0xFF00838F)

    // Document card accent
    val DocGradientStart = Color(0xFF4A148C)
    val DocGradientEnd = Color(0xFF7B1FA2)
}

// Light scheme
val md_theme_light_primary = MotoColors.Primary40
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = MotoColors.Primary90
val md_theme_light_onPrimaryContainer = MotoColors.Primary10
val md_theme_light_secondary = MotoColors.Secondary40
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = MotoColors.Secondary90
val md_theme_light_onSecondaryContainer = MotoColors.Secondary10
val md_theme_light_tertiary = MotoColors.Tertiary40
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = MotoColors.Tertiary90
val md_theme_light_onTertiaryContainer = Color(0xFF00252A)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = MotoColors.Background
val md_theme_light_onBackground = Color(0xFF1A1C2C)
val md_theme_light_surface = MotoColors.Surface
val md_theme_light_onSurface = Color(0xFF1A1C2C)
val md_theme_light_surfaceVariant = Color(0xFFE3E6F8)
val md_theme_light_onSurfaceVariant = Color(0xFF44475C)
val md_theme_light_outline = Color(0xFF757894)
val md_theme_light_inverseOnSurface = Color(0xFFF1EFF8)
val md_theme_light_inverseSurface = Color(0xFF313040)
val md_theme_light_inversePrimary = MotoColors.Primary80
val md_theme_light_surfaceTint = MotoColors.Primary40
val md_theme_light_outlineVariant = Color(0xFFC4C6DB)
val md_theme_light_scrim = Color(0xFF000000)

// Dark scheme
val md_theme_dark_primary = MotoColors.Primary80
val md_theme_dark_onPrimary = MotoColors.Primary20
val md_theme_dark_primaryContainer = MotoColors.Primary30
val md_theme_dark_onPrimaryContainer = MotoColors.Primary90
val md_theme_dark_secondary = MotoColors.Secondary80
val md_theme_dark_onSecondary = MotoColors.Secondary20
val md_theme_dark_secondaryContainer = MotoColors.Secondary30
val md_theme_dark_onSecondaryContainer = MotoColors.Secondary90
val md_theme_dark_tertiary = MotoColors.Tertiary80
val md_theme_dark_onTertiary = Color(0xFF00363D)
val md_theme_dark_tertiaryContainer = Color(0xFF004F58)
val md_theme_dark_onTertiaryContainer = MotoColors.Tertiary90
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = MotoColors.BackgroundDark
val md_theme_dark_onBackground = Color(0xFFE4E1F0)
val md_theme_dark_surface = MotoColors.SurfaceDark
val md_theme_dark_onSurface = Color(0xFFE4E1F0)
val md_theme_dark_surfaceVariant = Color(0xFF44475C)
val md_theme_dark_onSurfaceVariant = Color(0xFFC4C6DB)
val md_theme_dark_outline = Color(0xFF8E90A6)
val md_theme_dark_inverseOnSurface = Color(0xFF1A1C2C)
val md_theme_dark_inverseSurface = Color(0xFFE4E1F0)
val md_theme_dark_inversePrimary = MotoColors.Primary40
val md_theme_dark_surfaceTint = MotoColors.Primary80
val md_theme_dark_outlineVariant = Color(0xFF44475C)
val md_theme_dark_scrim = Color(0xFF000000)
