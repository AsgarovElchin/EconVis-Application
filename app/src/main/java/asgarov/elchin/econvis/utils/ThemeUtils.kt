package asgarov.elchin.econvis.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeUtils {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME_MODE = "theme_mode"

    fun setTheme(context: Context, isDarkMode: Boolean) {
        val editor: SharedPreferences.Editor = getSharedPreferences(context).edit()
        editor.putBoolean(KEY_THEME_MODE, isDarkMode)
        editor.apply()

        val mode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun isDarkMode(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_THEME_MODE, false)
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}