package asgarov.elchin.econvis.data.repository

import asgarov.elchin.econvis.data.model.GiniData
import asgarov.elchin.econvis.data.network.APIService
import asgarov.elchin.econvis.data.network.CountryDataDao
import javax.inject.Inject

class GiniRepository @Inject constructor(private val apiService: APIService, private val countryDataDao: CountryDataDao) {

    fun getGiniDataByYear(year: Int) = apiService.getGiniDataByYear(year)

    suspend fun insertGiniData(giniData: List<GiniData>) {
        countryDataDao.insertGiniData(giniData)
    }

    suspend fun getGiniDataByYearLocal(year: Int): List<GiniData> {
        return countryDataDao.getGiniDataByYear(year)
    }
}