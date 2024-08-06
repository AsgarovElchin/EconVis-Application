package asgarov

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import asgarov.elchin.econvis.utils.NetworkChangeReceiver
import asgarov.elchin.econvis.utils.NetworkStatusHelper
import asgarov.elchin.econvis.utils.PreferenceHelper
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale


@HiltAndroidApp
class MyApplication : Application() {

    private val networkChangeReceiver = NetworkChangeReceiver()

    override fun onCreate() {
        super.onCreate()
        applyLanguagePreference()
        registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        initializeNetworkStatus()
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(networkChangeReceiver)
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

    private fun initializeNetworkStatus() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        val isOnline = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        NetworkStatusHelper.instance.setNetworkStatus(isOnline)
    }
}