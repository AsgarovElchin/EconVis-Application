package asgarov.elchin.econvis

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import asgarov.elchin.econvis.databinding.ActivityMainBinding
import asgarov.elchin.econvis.utils.SharedPreferences

class MainActivity : AppCompatActivity() {
    private val viewModel : MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
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
                navGraph.setStartDestination(R.id.homeFragment)
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
        return SharedPreferences.isUserLoggedIn(this)
    }

    private fun isUserOnboarded(): Boolean {
       return SharedPreferences.isUserOnboarded(this)
    }
}


