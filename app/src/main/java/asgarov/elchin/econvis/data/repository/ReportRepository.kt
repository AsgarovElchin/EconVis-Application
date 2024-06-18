package asgarov.elchin.econvis.data.repository

import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.Indicator
import asgarov.elchin.econvis.data.model.Report
import asgarov.elchin.econvis.data.model.ReportRequest
import asgarov.elchin.econvis.data.model.Year
import asgarov.elchin.econvis.data.network.ApiService
import retrofit2.Call
import javax.inject.Inject

class ReportRepository @Inject constructor(private val apiService: ApiService) {

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
}