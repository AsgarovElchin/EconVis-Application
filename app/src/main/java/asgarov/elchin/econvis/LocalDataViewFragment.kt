package asgarov.elchin.econvis

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import asgarov.elchin.econvis.data.model.NewCountryData
import asgarov.elchin.econvis.databinding.FragmentLocalDataViewBinding
import asgarov.elchin.econvis.utils.CountryAdapter
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.countries.observe(viewLifecycleOwner) { countries ->
            Log.d("LocalDataViewFragment", "Observed countries: $countries")
            binding.recyclerView.adapter = CountryAdapter(countries) { country ->
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
