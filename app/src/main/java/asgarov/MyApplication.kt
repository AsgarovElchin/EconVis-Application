package asgarov

import android.app.Application
import android.content.res.Configuration
import asgarov.elchin.econvis.utils.PreferenceHelper
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale


@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        applyLanguagePreference()
    }

    private fun applyLanguagePreference() {
        val language = PreferenceHelper.getLanguage(this)
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }


}