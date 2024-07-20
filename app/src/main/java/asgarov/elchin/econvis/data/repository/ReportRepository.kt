package asgarov.elchin.econvis.data.repository

import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.Indicator
import asgarov.elchin.econvis.data.model.Report
import asgarov.elchin.econvis.data.model.ReportRequest
import asgarov.elchin.econvis.data.model.Year
import asgarov.elchin.econvis.data.network.ApiService
import asgarov.elchin.econvis.data.network.CountryDataDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val apiService: ApiService,
    private val countryDataDao: CountryDataDao
) {

    fun getReports(reportRequest: ReportRequest): Call<List<Report>> {
        return apiService.getReports(reportRequest)
    }

    fun getCountries(): Call<List<Country>> {
        return apiService.getCountries()
    }

    fun getIndicators(): Call<List<Indicator>> {
        return apiService.getIndicators()
    }

    fun getYears(): Call<List<Year>> {
        return apiService.getYears()
    }

    suspend fun insertCountries(countries: List<Country>) {
        withContext(Dispatchers.IO) {
            countryDataDao.insertCountries(countries)
        }
    }

    suspend fun insertIndicators(indicators: List<Indicator>) {
        withContext(Dispatchers.IO) {
            countryDataDao.insertIndicators(indicators)
        }
    }

    suspend fun insertYears(years: List<Year>) {
        withContext(Dispatchers.IO) {
            countryDataDao.insertYears(years)
        }
    }

    suspend fun getLocalCountries(): List<Country> {
        return countryDataDao.getCountries()
    }

    suspend fun getLocalIndicators(): List<Indicator> {
        return countryDataDao.getIndicators()
    }

    suspend fun getLocalYears(): List<Year> {
        return countryDataDao.getYears()
    }
}