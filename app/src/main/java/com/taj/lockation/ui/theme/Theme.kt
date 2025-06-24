package com.taj.lockation.ui.theme

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.taj.lockation.helpers.Prefs

private val DarkColorScheme = darkColorScheme(
	primary = Purple80,
	secondary = PurpleGrey80,
	tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
	primary = Purple40,
	secondary = PurpleGrey40,
	tertiary = Pink40

	/* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun LockationTheme(
	darkTheme: Boolean = isSystemInDarkTheme(),
	// Dynamic color is available on Android 12+
	dynamicColor: Boolean = true,
	content: @Composable () -> Unit
)
{
	var darkTheme = darkTheme;
	when(LocalContext.current.getSharedPreferences(Prefs.NAME, Context.MODE_PRIVATE).getInt(Prefs.THEME, 0))
	{
		1 -> darkTheme = false;
		2 -> darkTheme = true;
	}

	val colorScheme = when
	{
		dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
		{
			val context = LocalContext.current
			if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
		}

		darkTheme -> DarkColorScheme
		else -> LightColorScheme
	}
	val view = LocalView.current
	if (!view.isInEditMode)
	{
		SideEffect {
			val window = (view.context as Activity).window
			window.statusBarColor = colorScheme.surface.toArgb()
			WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
		}
	}

	MaterialTheme(
		colorScheme = colorScheme,
//		colorScheme = lightColorScheme(),
//		colorScheme = dynamicLightColorScheme(LocalContext.current),
//		colorScheme = darkColorScheme(),
//		colorScheme = dynamicDarkColorScheme(LocalContext.current),

		typography = Typography,
		content = content
	)
}

@Composable
fun VSpacer(height: Dp)
{
	Spacer(modifier = Modifier.height(height))
}

@Composable
fun HSpacer(width: Dp)
{
	Spacer(modifier = Modifier.width(width))
}


@Composable
fun Spacer(height: Dp = 0.dp,width: Dp = 0.dp)
{
	Spacer(modifier = Modifier.height(height).width(width))
}