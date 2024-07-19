package asgarov.elchin.econvis.data.repository

import android.util.Log
import asgarov.elchin.econvis.data.model.CountryData
import asgarov.elchin.econvis.data.model.CountryIndicatorData
import asgarov.elchin.econvis.data.model.IndicatorData
import asgarov.elchin.econvis.data.network.ApiService
import asgarov.elchin.econvis.data.network.CountryDataDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CountryDataRepository @Inject constructor(
    private val countryDataDao: CountryDataDao,
    private val apiService: ApiService
) {

    suspend fun fetchCountryData(countryId: Long): Result<List<CountryIndicatorData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCountryData(countryId)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        Log.d("CountryDataRepository", "API response data: $data")
                        val countryIndicatorData =
                            data.map { (indicator: String, yearData: Map<String, String>) ->
                                CountryIndicatorData(
                                    indicator = indicator,
                                    data = yearData.map { (year: String, value: String) ->
                                        IndicatorData(year.toInt(), value.toDouble())
                                    }
                                )
                            }
                        countryIndicatorData.forEach { indicatorData ->
                            indicatorData.data.forEach { data ->
                                val entity = CountryData(
                                    id = 0L, // Auto-generated ID
                                    countryId = countryId,
                                    countryName = "", // Populate this if available
                                    indicator = indicatorData.indicator,
                                    year = data.year,
                                    value = data.value
                                )
                                countryDataDao.insert(entity)
                            }
                        }
                        Result.success(countryIndicatorData)
                    } ?: run {
                        Log.d("CountryDataRepository", "No data available in API response")
                        Result.failure(Throwable("No data available"))
                    }
                } else {
                    Log.d("CountryDataRepository", "API call failed: ${response.message()}")
                    Result.failure(Throwable(response.message()))
                }
            } catch (e: Exception) {
                Log.e("CountryDataRepository", "Exception during API call", e)
                Result.failure(e)
            }
        }
    }
}