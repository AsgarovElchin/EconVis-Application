package asgarov.elchin.econvis

import android.R
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import asgarov.elchin.econvis.data.model.NewCountryData
import asgarov.elchin.econvis.databinding.FragmentLocalDataViewBinding
import asgarov.elchin.econvis.utils.CountryAdapter
import asgarov.elchin.econvis.utils.NetworkStatusHelper
import asgarov.elchin.econvis.utils.SnackbarUtils
import asgarov.ui.CountryDataViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocalDataViewFragment : Fragment() {
    private lateinit var binding: FragmentLocalDataViewBinding
    private val viewModel: CountryDataViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocalDataViewBinding.inflate(inflater, container, false)

        NetworkStatusHelper.instance.networkStatus.observe(viewLifecycleOwner, Observer { isOnline ->
            val previousStatus = NetworkStatusHelper.instance.getPreviousNetworkStatus()
            Log.d("WorldMapFragment", "Network status observed: isOnline=$isOnline, previousStatus=$previousStatus")
            val message = when {
                isOnline && previousStatus == false -> "You are in online mode"
                !isOnline && previousStatus == true -> "You are in offline mode"
                else -> null
            }
            message?.let {
                Log.d("WorldMapFragment", "Showing Snackbar: $it")
                SnackbarUtils.showCustomSnackbar(binding.root, it, R.color.darker_gray)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            Log.d("LocalDataViewFragment", "Observed countries: $countries")
            // Ensure the countries are displayed in the correct order (top to bottom)
            val sortedCountries = countries.sortedBy { it.countryName }
            binding.recyclerView.adapter = CountryAdapter(sortedCountries) { country ->
                // Show a dialog to confirm deletion
                showDeleteConfirmationDialog(country)
            }
        }

        // Fetch all countries
        viewModel.fetchAllCountries()
    }

    private fun showDeleteConfirmationDialog(country: NewCountryData) {
        // Show a dialog to confirm deletion
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Country")
            .setMessage("Are you sure you want to delete ${country.countryName}?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteDataByCountry(country.countryId)
            }
            .setNegativeButton("No", null)
            .show()
    }
}
