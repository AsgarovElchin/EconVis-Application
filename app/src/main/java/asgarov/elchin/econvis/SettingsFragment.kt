package asgarov.elchin.econvis

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import asgarov.elchin.econvis.data.repository.UserRepository
import asgarov.elchin.econvis.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import asgarov.elchin.econvis.utils.PreferenceHelper
import asgarov.elchin.econvis.utils.ThemeUtils
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.buttonLogout.setOnClickListener {
            handleLogout()
        }

        binding.buttonLanguage.setOnClickListener {
            showLanguageSelectionDialog()
        }

        binding.switchTheme.isChecked = ThemeUtils.isDarkMode(requireContext())
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemeUtils.setTheme(requireContext(), isChecked)
        }

        return binding.root
    }

    private fun handleLogout() {
        // Handle logout logic
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf("English", "Spanish", "Chinese", "Arabic", "Azerbaijani", "Hindi", "Bengali", "Portuguese", "Russian", "Urdu")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                val selectedLanguage = when (which) {
                    0 -> "en"
                    1 -> "es"
                    2 -> "zh"
                    3 -> "ar"
                    4 -> "az"
                    5 -> "hi"
                    6 -> "bn"
                    7 -> "pt"
                    8 -> "ru"
                    9 -> "ur"
                    else -> "en"
                }
                saveLanguagePreference(selectedLanguage)
                updateLanguage(selectedLanguage)
            }
            .show()
    }

    private fun saveLanguagePreference(language: String) {
        PreferenceHelper.setLanguage(requireContext(), language)
    }

    private fun updateLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        requireActivity().recreate()
    }
}