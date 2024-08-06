package asgarov.elchin.econvis.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate


object ThemeUtils {
    fun isDarkMode(context: Context): Boolean {
        return PreferenceHelper.isDarkMode(context)
    }

    fun setTheme(isDarkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun saveThemePreference(context: Context, isDarkMode: Boolean) {
        PreferenceHelper.setThemePreference(context, isDarkMode)
    }
}