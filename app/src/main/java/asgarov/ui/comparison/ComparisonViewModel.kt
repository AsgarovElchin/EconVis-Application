package asgarov.ui.comparison

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.CountryData
import asgarov.elchin.econvis.data.model.Indicator
import asgarov.elchin.econvis.data.model.Report
import asgarov.elchin.econvis.data.model.ReportRequest
import asgarov.elchin.econvis.data.model.Year
import asgarov.elchin.econvis.data.repository.ComparsionRepository
import asgarov.elchin.econvis.utils.NetworkUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ComparisonViewModel @Inject constructor(
    application: Application,
    private val reportRepository: ComparsionRepository
) : AndroidViewModel(application) {

    private val _countriesByRegion = MutableLiveData<Map<String, List<Country>>>()
    val countriesByRegion: LiveData<Map<String, List<Country>>> = _countriesByRegion

    private val _indicators = MutableLiveData<Result<List<Indicator>>>()
    val indicators: LiveData<Result<List<Indicator>>> = _indicators

    private val _years = MutableLiveData<Result<List<Year>>>()
    val years: LiveData<Result<List<Year>>> = _years

    private val _reports = MutableLiveData<Result<List<Report>>>()
    val reports: LiveData<Result<List<Report>>> = _reports

    private val _countryDataValues = MutableLiveData<List<CountryData>>()
    val countryDataValues: LiveData<List<CountryData>> = _countryDataValues

    fun fetchCountries() {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            Log.d("ComparisonViewModel", "Fetching countries from network...")
            reportRepository.getCountries().enqueue(object : Callback<List<Country>> {
                override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                    if (response.isSuccessful) {
                        val countries = response.body()!!
                        Log.d("ComparisonViewModel", "Fetched countries: $countries")
                        viewModelScope.launch {
                            reportRepository.insertCountries(countries)
                            val localCountries = reportRepository.getLocalCountries()
                            val groupedByRegion = localCountries.groupBy { it.region }
                            _countriesByRegion.postValue(groupedByRegion)
                            Log.d("ComparisonViewModel", "Countries grouped by region: $groupedByRegion")
                        }
                    } else {
                        Log.e("ComparisonViewModel", "Failed to fetch countries: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                    Log.e("ComparisonViewModel", "Failed to fetch countries", t)
                }
            })
        } else {
            Log.d("ComparisonViewModel", "Fetching countries from local database...")
            viewModelScope.launch {
                val localCountries = reportRepository.getLocalCountries()
                val groupedByRegion = localCountries.groupBy { it.region }
                _countriesByRegion.postValue(groupedByRegion)
                Log.d("ComparisonViewModel", "Countries grouped by region from local database: $groupedByRegion")
            }
        }
    }

    fun fetchIndicators() {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            Log.d("ComparisonViewModel", "Fetching indicators from network...")
            reportRepository.getIndicators().enqueue(object : Callback<List<Indicator>> {
                override fun onResponse(call: Call<List<Indicator>>, response: Response<List<Indicator>>) {
                    if (response.isSuccessful) {
                        val indicators = response.body()!!
                        Log.d("ComparisonViewModel", "Fetched indicators: $indicators")
                        viewModelScope.launch {
                            reportRepository.insertIndicators(indicators)
                            val localIndicators = reportRepository.getLocalIndicators()
                            _indicators.postValue(Result.success(localIndicators))
                            Log.d("ComparisonViewModel", "Local indicators: $localIndicators")
                        }
                    } else {
                        Log.e("ComparisonViewModel", "Failed to fetch indicators: ${response.message()}")
                        _indicators.postValue(Result.failure(Throwable(response.message())))
                    }
                }

                override fun onFailure(call: Call<List<Indicator>>, t: Throwable) {
                    Log.e("ComparisonViewModel", "Failed to fetch indicators", t)
                    _indicators.postValue(Result.failure(t))
                }
            })
        } else {
            Log.d("ComparisonViewModel", "Fetching indicators from local database...")
            viewModelScope.launch {
                val localIndicators = reportRepository.getLocalIndicators()
                _indicators.postValue(Result.success(localIndicators))
                Log.d("ComparisonViewModel", "Local indicators: $localIndicators")
            }
        }
    }

    fun fetchYears() {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            Log.d("ComparisonViewModel", "Fetching years from network...")
            reportRepository.getYears().enqueue(object : Callback<List<Year>> {
                override fun onResponse(call: Call<List<Year>>, response: Response<List<Year>>) {
                    if (response.isSuccessful) {
                        val years = response.body()!!
                        Log.d("ComparisonViewModel", "Fetched years: $years")
                        viewModelScope.launch {
                            reportRepository.insertYears(years)
                            val localYears = reportRepository.getLocalYears()
                            _years.postValue(Result.success(localYears))
                            Log.d("ComparisonViewModel", "Local years: $localYears")
                        }
                    } else {
                        Log.e("ComparisonViewModel", "Failed to fetch years: ${response.message()}")
                        _years.postValue(Result.failure(Throwable(response.message())))
                    }
                }

                override fun onFailure(call: Call<List<Year>>, t: Throwable) {
                    Log.e("ComparisonViewModel", "Failed to fetch years", t)
                    _years.postValue(Result.failure(t))
                }
            })
        } else {
            Log.d("ComparisonViewModel", "Fetching years from local database...")
            viewModelScope.launch {
                val localYears = reportRepository.getLocalYears()
                _years.postValue(Result.success(localYears))
                Log.d("ComparisonViewModel", "Local years: $localYears")
            }
        }
    }

    fun fetchReports(reportRequest: ReportRequest) {
        Log.d("ComparisonViewModel", "Fetching reports with request: $reportRequest")
        reportRepository.getReports(reportRequest).enqueue(object : Callback<List<Report>> {
            override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {
                if (response.isSuccessful) {
                    _reports.postValue(Result.success(response.body()!!))
                    Log.d("ComparisonViewModel", "Fetched reports: ${response.body()!!}")
                } else {
                    Log.e("ComparisonViewModel", "Failed to fetch reports: ${response.message()}")
                    _reports.postValue(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                Log.e("ComparisonViewModel", "Failed to fetch reports", t)
                _reports.postValue(Result.failure(t))
            }
        })
    }

    fun fetchCountryDataValues(countryId: Long, indicator: String, years: List<Int>) {
        viewModelScope.launch {
            try {
                val values = reportRepository.getCountryDataValues(countryId, indicator, years)
                _countryDataValues.postValue(values)
            } catch (e: Exception) {
                // Handle any exceptions
                _countryDataValues.postValue(emptyList())
            }
        }
    }


}