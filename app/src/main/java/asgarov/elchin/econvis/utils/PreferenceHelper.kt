package asgarov.elchin.econvis.utils

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaFormat.KEY_LANGUAGE
import android.preference.PreferenceManager
import android.util.Log

object PreferenceHelper {
    private const val PREFS_NAME = "econvis_prefs"
    private const val KEY_IS_USER_LOGGED_IN = "is_user_logged_in"
    private const val KEY_IS_USER_ONBOARDED = "is_user_onboarded"
    private const val KEY_LANGUAGE = "app_language"

    private fun getSharedPreferences(context: Context): android.content.SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setUserLoggedIn(context: Context, loggedIn: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(KEY_IS_USER_LOGGED_IN, loggedIn)
        val success = editor.commit() // Using commit() for immediate save
        Log.d("SharedPreferences", "setUserLoggedIn: $loggedIn, success: $success")
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val loggedIn = getSharedPreferences(context).getBoolean(KEY_IS_USER_LOGGED_IN, false)
        Log.d("SharedPreferences", "isUserLoggedIn: $loggedIn")
        return loggedIn
    }

    fun setUserOnboarded(context: Context, onboarded: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(KEY_IS_USER_ONBOARDED, onboarded)
        val success = editor.commit() // Using commit() for immediate save
        Log.d("SharedPreferences", "setUserOnboarded: $onboarded, success: $success")
    }

    fun isUserOnboarded(context: Context): Boolean {
        val onboarded = getSharedPreferences(context).getBoolean(KEY_IS_USER_ONBOARDED, false)
        Log.d("SharedPreferences", "isUserOnboarded: $onboarded")
        return onboarded
    }

    fun setLanguage(context: Context, languageCode: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_LANGUAGE, languageCode)
        editor.apply()
    }

    fun getLanguage(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_LANGUAGE, "en")
    }
}