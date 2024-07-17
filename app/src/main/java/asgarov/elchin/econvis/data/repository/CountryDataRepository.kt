package asgarov.elchin.econvis.data.repository

import asgarov.elchin.econvis.data.network.ApiService
import javax.inject.Inject

class CountryDataRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getCountryData(countries: List<String>) = apiService.getCountryData(countries)
}