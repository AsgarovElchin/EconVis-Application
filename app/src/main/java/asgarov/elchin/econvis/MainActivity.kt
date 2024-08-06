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
        setDefaultTheme()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        setupNavigationGraph()
    }

    override fun onStart() {
        super.onStart()
        if (!isUserLoggedIn()) {
            navController.navigate(R.id.signUpOrLoginFragment)
        }
    }

    private fun setDefaultTheme() {
        val isDarkMode = PreferenceHelper.isUserLoggedIn(this) && PreferenceHelper.isDarkMode(this)
        ThemeUtils.setTheme(isDarkMode)
    }

    private fun applyLanguagePreference(context: Context) {
        val language = PreferenceHelper.getLanguage(context)
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    private fun setupNavigationGraph() {
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.my_nav)

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
        navController.graph = navGraph
    }

    private fun isUserLoggedIn(): Boolean {
        return PreferenceHelper.isUserLoggedIn(this)
    }

    private fun isUserOnboarded(): Boolean {
        return PreferenceHelper.isUserOnboarded(this)
    }
}