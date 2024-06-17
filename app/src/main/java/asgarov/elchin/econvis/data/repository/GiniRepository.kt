package asgarov.elchin.econvis.data.repository

import asgarov.elchin.econvis.data.network.ApiService
import javax.inject.Inject

class GiniRepository @Inject constructor(private val apiService: ApiService) {

    fun getGiniDataByYear(year: Int) = apiService.getGiniDataByYear(year)
}