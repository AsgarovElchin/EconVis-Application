package asgarov.elchin.econvis

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.databinding.FragmentSettingsBinding
import asgarov.elchin.econvis.utils.PreferenceHelper
import asgarov.elchin.econvis.utils.RegionExpandableListAdapter
import asgarov.elchin.econvis.utils.ThemeUtils
import asgarov.ui.CountryDataViewModel
import asgarov.ui.comparison.ComparisonViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val comparisonViewModel: ComparisonViewModel by viewModels()
    private val countryDataViewModel: CountryDataViewModel by viewModels()
    private val selectedCountryIds = mutableListOf<Long>()

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

        binding.buttonSelectCountries.setOnClickListener {
            comparisonViewModel.fetchCountries()
        }

        setupCountryObserver()
        setupCountryDataObserver()

        return binding.root
    }

    private fun setupCountryObserver() {
        comparisonViewModel.countriesByRegion.observe(viewLifecycleOwner, Observer { groupedCountries ->
            if (groupedCountries.isNotEmpty()) {
                val regionList = groupedCountries.keys.toList()
                val countryMap = groupedCountries.mapValues { entry -> entry.value }

                showRegionSelectDialog("Select Country", regionList, countryMap)
            } else {
                Toast.makeText(requireContext(), "No countries found", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupCountryDataObserver() {
        countryDataViewModel.countryDataResult.observe(viewLifecycleOwner, Observer { result ->
            result.onSuccess { data ->
                Log.d("SettingsFragment", "Data fetched successfully: $data")
                Toast.makeText(requireContext(), "Data fetched successfully", Toast.LENGTH_SHORT).show()
                // Update UI with the fetched data
            }.onFailure { throwable ->
                Log.e("SettingsFragment", "Failed to fetch data", throwable)
                Toast.makeText(requireContext(), "Failed to fetch data: ${throwable.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRegionSelectDialog(
        title: String,
        regions: List<String>,
        countryMap: Map<String, List<Country>>
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_categorized_countries, null)
        val expandableListView = dialogView.findViewById<ExpandableListView>(R.id.expandableListView)
        val adapter = RegionExpandableListAdapter(requireContext(), regions, countryMap, selectedCountryIds)
        expandableListView.setAdapter(adapter)

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                // Handle fetching data for selected country
                if (selectedCountryIds.isNotEmpty()) {
                    val selectedCountry = selectedCountryIds.first().toInt()
                    Log.d("SettingsFragment", "Selected country: $selectedCountry")
                    countryDataViewModel.fetchCountryData(selectedCountry)
                    refreshFragment()
                } else {
                    Toast.makeText(requireContext(), "No country selected", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun refreshFragment() {
        selectedCountryIds.clear() // Clear selected country IDs before refreshing
        parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
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