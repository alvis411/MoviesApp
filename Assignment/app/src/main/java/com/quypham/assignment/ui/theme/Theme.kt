package com.quypham.assignment.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat


private val LightElevation = Elevations()
private val DarkElevation = Elevations(card = 1.dp)

// Material 3 color schemes
private val appDarkColorScheme = darkColorScheme(
    primary = appDarkPrimary,
    onPrimary = appDarkOnPrimary,
    primaryContainer = appDarkPrimaryContainer,
    onPrimaryContainer = appDarkOnPrimaryContainer,
    inversePrimary = appDarkPrimaryInverse,
    secondary = appDarkSecondary,
    onSecondary = appDarkOnSecondary,
    secondaryContainer = appDarkSecondaryContainer,
    onSecondaryContainer = appDarkOnSecondaryContainer,
    tertiary = appDarkTertiary,
    onTertiary = appDarkOnTertiary,
    tertiaryContainer = appDarkTertiaryContainer,
    onTertiaryContainer = appDarkOnTertiaryContainer,
    error = appDarkError,
    onError = appDarkOnError,
    errorContainer = appDarkErrorContainer,
    onErrorContainer = appDarkOnErrorContainer,
    background = appDarkBackground,
    onBackground = appDarkOnBackground,
    surface = appDarkSurface,
    onSurface = appDarkOnSurface,
    inverseSurface = appDarkInverseSurface,
    inverseOnSurface = appDarkInverseOnSurface,
    surfaceVariant = appDarkSurfaceVariant,
    onSurfaceVariant = appDarkOnSurfaceVariant,
    outline = appDarkOutline
)

private val appLightColorScheme = lightColorScheme(
    primary = appLightPrimary,
    onPrimary = appLightOnPrimary,
    primaryContainer = appLightPrimaryContainer,
    onPrimaryContainer = appLightOnPrimaryContainer,
    inversePrimary = appLightPrimaryInverse,
    secondary = appLightSecondary,
    onSecondary = appLightOnSecondary,
    secondaryContainer = appLightSecondaryContainer,
    onSecondaryContainer = appLightOnSecondaryContainer,
    tertiary = appLightTertiary,
    onTertiary = appLightOnTertiary,
    tertiaryContainer = appLightTertiaryContainer,
    onTertiaryContainer = appLightOnTertiaryContainer,
    error = appLightError,
    onError = appLightOnError,
    errorContainer = appLightErrorContainer,
    onErrorContainer = appLightOnErrorContainer,
    background = appLightBackground,
    onBackground = appLightOnBackground,
    surface = appLightSurface,
    onSurface = appLightOnSurface,
    inverseSurface = appLightInverseSurface,
    inverseOnSurface = appLightInverseOnSurface,
    surfaceVariant = appLightSurfaceVariant,
    onSurfaceVariant = appLightOnSurfaceVariant,
    outline = appLightOutline
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val elevation = if (darkTheme) DarkElevation else LightElevation

    val appColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> appDarkColorScheme
        else -> appLightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = appColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    CompositionLocalProvider(
        LocalElevations provides elevation,
    ) {
        MaterialTheme(
            colorScheme = appColorScheme,
            typography = appTypography,
            shapes = shapes,
            content = content
        )
    }
}

object AppTheme {
    /**
     * Retrieves the current [Elevations] at the call site's position in the hierarchy.
     */
    val elevations: Elevations
        @Composable
        get() = LocalElevations.current

}