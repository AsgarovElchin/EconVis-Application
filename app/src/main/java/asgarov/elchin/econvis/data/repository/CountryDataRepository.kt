package asgarov.elchin.econvis.data.repository

import android.util.Log
import asgarov.elchin.econvis.data.model.CountryData
import asgarov.elchin.econvis.data.model.CountryIndicatorData
import asgarov.elchin.econvis.data.model.IndicatorData
import asgarov.elchin.econvis.data.model.NewCountryData
import asgarov.elchin.econvis.data.network.APIService
import asgarov.elchin.econvis.data.network.CountryDataDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CountryDataRepository @Inject constructor(
    private val countryDataDao: CountryDataDao,
    private val apiService: APIService
) {

    suspend fun fetchCountryData(countryId: Long, countryName: String): Result<List<CountryIndicatorData>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCountryData(countryId)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        Log.d("CountryDataRepository", "API response data: $data")
                        val countryIndicatorData = data.map { (indicator, yearData) ->
                            CountryIndicatorData(
                                indicator = indicator,
                                data = yearData.map { (year, value) ->
                                    IndicatorData(year.toInt(), value.toDouble())
                                }
                            )
                        }
                        countryIndicatorData.forEach { indicatorData ->
                            indicatorData.data.forEach { data ->
                                val entity = CountryData(
                                    id = 0L, // Auto-generated ID
                                    countryId = countryId,
                                    countryName = countryName, // Use the passed country name
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


    suspend fun getAllCountries(): List<NewCountryData> {
        return countryDataDao.getAllCountries()
    }

    suspend fun deleteDataByCountry(countryId: Long) {
        return countryDataDao.deleteDataByCountry(countryId)
    }

    suspend fun deleteAllData() {
        return countryDataDao.deleteAllData()
    }





}