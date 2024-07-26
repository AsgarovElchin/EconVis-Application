package asgarov.elchin.econvis

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import asgarov.elchin.econvis.databinding.ActivityMenuContainerBinding
import asgarov.elchin.econvis.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MenuContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuContainerBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        applyLanguagePreference(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMenuContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.worldMapFragment,
                R.id.comparisonFragment,
                R.id.savedChartsFragment,
                R.id.settingsFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavMenu.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.signUpOrLoginFragment,
                R.id.loginFragment,
                R.id.signUpFragment -> hideBothMenus()
                R.id.localDataViewFragment -> hideBottomMenu()
                else -> showBothMenus()
            }
        }
    }

    private fun hideBothMenus() {
        binding.bottomNavMenu.visibility = View.GONE
        binding.toolbar.visibility = View.GONE
    }

    private fun hideBottomMenu() {
        binding.bottomNavMenu.visibility = View.GONE
        binding.toolbar.visibility = View.VISIBLE
    }

    private fun showBothMenus() {
        binding.bottomNavMenu.visibility = View.VISIBLE
        binding.toolbar.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
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
}