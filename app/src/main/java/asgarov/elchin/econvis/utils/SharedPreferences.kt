package asgarov.elchin.econvis.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferences {
    private const val PREFS_NAME = "econvis_prefs"
    private const val KEY_IS_USER_LOGGED_IN = "is_user_logged_in"
    private const val KEY_IS_USER_ONBOARDED = "is_user_onboarded"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun setUserLoggedIn(context: Context, loggedIn: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(KEY_IS_USER_LOGGED_IN, loggedIn)
        editor.apply()
    }

    fun isUserLoggedIn(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IS_USER_LOGGED_IN, false)
    }

    fun setUserOnboarded(context: Context, onboarded: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(KEY_IS_USER_ONBOARDED, onboarded)
        editor.apply()
    }

    fun isUserOnboarded(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_IS_USER_ONBOARDED, false)
    }
}
