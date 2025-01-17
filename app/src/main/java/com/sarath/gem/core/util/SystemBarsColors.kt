package com.sarath.gem.core.util

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

@Composable
fun SystemBarsColors(
    navigationBarColor: Color? = null,
    statusBarColor: Color? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    val view = LocalView.current
    val bgColor = MaterialTheme.colorScheme.surface

    DisposableEffect(Unit) {
        val activity =
            try {
                (view.context as ComponentActivity)
            } catch (e: Exception) {
                Log.d("SystemBarsColors", "Failed to get window", e)
                return@DisposableEffect onDispose {}
            }

        activity.enableEdgeToEdge(
            navigationBarStyle =
                if (darkTheme) SystemBarStyle.dark(scrim = navigationBarColor?.toArgb() ?: bgColor.toArgb())
                else
                    SystemBarStyle.light(
                        scrim = navigationBarColor?.toArgb() ?: bgColor.toArgb(),
                        darkScrim = navigationBarColor?.toArgb() ?: bgColor.toArgb(),
                    ),
            statusBarStyle =
                if (darkTheme) SystemBarStyle.dark(scrim = statusBarColor?.toArgb() ?: bgColor.toArgb())
                else
                    SystemBarStyle.light(
                        scrim = statusBarColor?.toArgb() ?: bgColor.toArgb(),
                        darkScrim = statusBarColor?.toArgb() ?: bgColor.toArgb(),
                    ),
        )

        onDispose { activity.enableEdgeToEdge() }
    }
}
