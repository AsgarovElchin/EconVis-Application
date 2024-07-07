package asgarov.elchin.econvis

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import asgarov.elchin.econvis.databinding.ActivityMainBinding
import asgarov.elchin.econvis.utils.PreferenceHelper
import asgarov.elchin.econvis.utils.ThemeUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }

        applyLanguagePreference(this)

        if (ThemeUtils.isDarkMode(this)) {
            ThemeUtils.setTheme(this, true)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the navController correctly
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.my_nav)

        // Set the start destination based on conditions
        when {
            isUserLoggedIn() -> {
                navGraph.setStartDestination(R.id.menuContainerActivity)
            }

            isUserOnboarded() -> {
                navGraph.setStartDestination(R.id.signUpOrLoginFragment)
            }

            else -> {
                navGraph.setStartDestination(R.id.viewPagerFragment)
            }
        }

        // Set the graph to the navController
        navController.graph = navGraph
    }

    fun applyLanguagePreference(context: Context) {
        val language = PreferenceHelper.getLanguage(context)
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    private fun isUserLoggedIn(): Boolean {
        return PreferenceHelper.isUserLoggedIn(this)
    }

    private fun isUserOnboarded(): Boolean {
        return PreferenceHelper.isUserOnboarded(this)
    }
}