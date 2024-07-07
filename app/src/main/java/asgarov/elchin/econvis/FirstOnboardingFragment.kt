package asgarov.elchin.econvis


import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import asgarov.elchin.econvis.databinding.FragmentFirstOnboardingBinding
import asgarov.elchin.econvis.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class FirstOnboardingFragment : Fragment() {
    private lateinit var binding: FragmentFirstOnboardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstOnboardingBinding.inflate(inflater, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)

        binding.ob1Button1.setOnClickListener {
            Log.d("FirstOnboardingFragment", "Next button clicked")
            viewPager?.currentItem = 1
        }

        binding.selectLanguageButton.setOnClickListener {
            showLanguageSelectionDialog()
        }

        return binding.root
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
                Log.d("FirstOnboardingFragment", "Selected language: $selectedLanguage")
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
