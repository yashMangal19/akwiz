package com.akwiz.android.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Palette adapted from Rosé Pine (MIT) — Dawn for light, Moon for dark.
// Light-theme accents are darkened from the originals to meet WCAG contrast.

private val DawnBase = Color(0xFFFAF4ED)
private val DawnSurface = Color(0xFFFFFAF3)
private val DawnOverlay = Color(0xFFF2E9E1)
private val DawnText = Color(0xFF464261)
private val DawnTextMuted = Color(0xFF716D8C)
private val DawnOutline = Color(0xFF908B9F)
private val DawnOutlineFaint = Color(0xFFE4DACE)
private val DawnIris = Color(0xFF7E659B)
private val DawnIrisFill = Color(0xFF856CA0)
private val DawnIrisContainer = Color(0xFFE5DAF1)
private val DawnPine = Color(0xFF286983)
private val DawnPineContainer = Color(0xFFD7E8EF)
private val DawnLove = Color(0xFFAD546D)
private val DawnLoveContainer = Color(0xFFF6DAE2)
private val DawnGold = Color(0xFFC87C15)
private val DawnGoldOnContainer = Color(0xFFBB7414)
private val DawnGoldInk = Color(0xFF9F6310)
private val DawnGoldContainer = Color(0xFFF6E4CA)
private val DawnFoam = Color(0xFF56949F)

private val MoonBase = Color(0xFF232136)
private val MoonSurface = Color(0xFF2A273F)
private val MoonOverlay = Color(0xFF393552)
private val MoonText = Color(0xFFE0DEF4)
private val MoonTextMuted = Color(0xFF918DAA)
private val MoonOutline = Color(0xFF736F8C)
private val MoonOutlineFaint = Color(0xFF393552)
private val MoonIris = Color(0xFFC4A7E7)
private val MoonIrisContainer = Color(0xFF46355A)
private val MoonPine = Color(0xFF3E8FB0)
private val MoonPineInk = Color(0xFF7FC3DC)
private val MoonPineContainer = Color(0xFF2B4550)
private val MoonLove = Color(0xFFEB6F92)
private val MoonLoveContainer = Color(0xFF562E3A)
private val MoonGold = Color(0xFFF6C177)
private val MoonGoldContainer = Color(0xFF56462E)
private val MoonFoam = Color(0xFF9CCFD8)

internal val AkwizLightColors = lightColorScheme(
    primary = DawnIrisFill,
    onPrimary = Color.White,
    primaryContainer = DawnIrisContainer,
    onPrimaryContainer = DawnText,
    secondary = DawnTextMuted,
    onSecondary = Color.White,
    secondaryContainer = DawnOverlay,
    onSecondaryContainer = DawnText,
    tertiary = DawnGoldInk,
    onTertiary = Color.White,
    tertiaryContainer = DawnGoldContainer,
    onTertiaryContainer = DawnText,
    background = DawnBase,
    onBackground = DawnText,
    surface = DawnSurface,
    onSurface = DawnText,
    surfaceVariant = DawnOverlay,
    onSurfaceVariant = DawnTextMuted,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = DawnSurface,
    surfaceContainer = DawnSurface,
    surfaceContainerHigh = DawnOverlay,
    surfaceContainerHighest = DawnOverlay,
    outline = DawnOutline,
    outlineVariant = DawnOutlineFaint,
    error = DawnLove,
    onError = Color.White,
    errorContainer = DawnLoveContainer,
    onErrorContainer = DawnText,
    scrim = Color(0xFF191724),
)

internal val AkwizDarkColors = darkColorScheme(
    primary = MoonIris,
    onPrimary = MoonBase,
    primaryContainer = MoonIrisContainer,
    onPrimaryContainer = MoonText,
    secondary = MoonTextMuted,
    onSecondary = MoonBase,
    secondaryContainer = MoonOverlay,
    onSecondaryContainer = MoonText,
    tertiary = MoonGold,
    onTertiary = MoonBase,
    tertiaryContainer = MoonGoldContainer,
    onTertiaryContainer = MoonText,
    background = MoonBase,
    onBackground = MoonText,
    surface = MoonSurface,
    onSurface = MoonText,
    surfaceVariant = MoonOverlay,
    onSurfaceVariant = MoonTextMuted,
    surfaceContainerLowest = Color(0xFF191724),
    surfaceContainerLow = MoonBase,
    surfaceContainer = MoonSurface,
    surfaceContainerHigh = MoonOverlay,
    surfaceContainerHighest = MoonOverlay,
    outline = MoonOutline,
    outlineVariant = MoonOutlineFaint,
    error = MoonLove,
    onError = MoonBase,
    errorContainer = MoonLoveContainer,
    onErrorContainer = MoonText,
    scrim = Color(0xFF191724),
)

@Immutable
data class QuizColors(
    val correct: Color,
    val onCorrectContainer: Color,
    val correctContainer: Color,
    val incorrect: Color,
    val onIncorrectContainer: Color,
    val incorrectContainer: Color,
    val streakActive: Color,
    val streakOnContainer: Color,
    val streakContainer: Color,
    val streakDormant: Color,
    val celebration: List<Color>,
)

internal val LightQuizColors = QuizColors(
    correct = DawnPine,
    onCorrectContainer = DawnText,
    correctContainer = DawnPineContainer,
    incorrect = DawnLove,
    onIncorrectContainer = DawnText,
    incorrectContainer = DawnLoveContainer,
    streakActive = DawnGold,
    streakOnContainer = DawnGoldOnContainer,
    streakContainer = DawnGoldContainer,
    streakDormant = DawnTextMuted,
    celebration = listOf(DawnIris, DawnGold, DawnPine, DawnLove, DawnFoam),
)

internal val DarkQuizColors = QuizColors(
    correct = MoonPineInk,
    onCorrectContainer = MoonText,
    correctContainer = MoonPineContainer,
    incorrect = MoonLove,
    onIncorrectContainer = MoonText,
    incorrectContainer = MoonLoveContainer,
    streakActive = MoonGold,
    streakOnContainer = MoonGold,
    streakContainer = MoonGoldContainer,
    streakDormant = MoonTextMuted,
    celebration = listOf(MoonIris, MoonGold, MoonPine, MoonLove, MoonFoam),
)

val LocalQuizColors = staticCompositionLocalOf { LightQuizColors }
