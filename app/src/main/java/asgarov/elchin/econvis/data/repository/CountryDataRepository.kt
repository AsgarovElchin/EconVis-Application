package asgarov.elchin.econvis.data.repository

import android.util.Log
import asgarov.elchin.econvis.data.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class CountryDataRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getCountryData(countryIds: List<String>): Response<Map<String, Any>> {
        return apiService.getCountryData(countryIds.first().toLong())
    }
}