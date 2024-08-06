package asgarov.elchin.econvis.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREF_NAME = "MyAppPreferences"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_IS_ONBOARDED = "isOnboarded"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_THEME_MODE = "theme_mode"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isUserLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setUserLoggedIn(context: Context, isLoggedIn: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun isUserOnboarded(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_ONBOARDED, false)
    }

    fun setUserOnboarded(context: Context, isOnboarded: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_ONBOARDED, isOnboarded)
        editor.apply()
    }

    fun getLanguage(context: Context): String {
        return getPreferences(context).getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguage(context: Context, language: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_LANGUAGE, language)
        editor.apply()
    }

    fun isDarkMode(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_THEME_MODE, false)
    }

    fun setThemePreference(context: Context, darkMode: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_THEME_MODE, darkMode)
        editor.apply()
    }
}
