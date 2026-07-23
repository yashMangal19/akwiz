package com.akwiz.android.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.akwiz.android.R

// IBM Plex (OFL 1.1), subset to Latin. Serif for question text, sans for controls.

val PlexSerif = FontFamily(
    Font(R.font.plex_serif_regular, FontWeight.Normal),
    Font(R.font.plex_serif_medium, FontWeight.Medium),
    Font(R.font.plex_serif_semibold, FontWeight.SemiBold),
)

val PlexSans = FontFamily(
    Font(R.font.plex_sans_regular, FontWeight.Normal),
    Font(R.font.plex_sans_medium, FontWeight.Medium),
    Font(R.font.plex_sans_semibold, FontWeight.SemiBold),
)

val AkwizTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlexSerif, fontWeight = FontWeight.Medium,
        fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.5).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = PlexSerif, fontWeight = FontWeight.Medium,
        fontSize = 32.sp, lineHeight = 38.sp, letterSpacing = (-0.4).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = PlexSerif, fontWeight = FontWeight.Normal,
        fontSize = 26.sp, lineHeight = 34.sp, letterSpacing = (-0.2).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = PlexSerif, fontWeight = FontWeight.Normal,
        fontSize = 22.sp, lineHeight = 30.sp, letterSpacing = (-0.1).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = PlexSans, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = PlexSans, fontWeight = FontWeight.Medium,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = PlexSans, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = PlexSans, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = PlexSans, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = PlexSans, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = PlexSans, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.9.sp,
    ),
)
