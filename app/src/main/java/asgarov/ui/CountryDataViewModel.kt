package asgarov.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import asgarov.elchin.econvis.data.model.CountryIndicatorData
import asgarov.elchin.econvis.data.model.NewCountryData
import asgarov.elchin.econvis.data.repository.CountryDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CountryDataViewModel @Inject constructor(
    application: Application,
    private val repository: CountryDataRepository
) : AndroidViewModel(application) {

    private val _countryDataResult = MutableLiveData<Result<List<CountryIndicatorData>>>()
    val countryDataResult: LiveData<Result<List<CountryIndicatorData>>> = _countryDataResult

    private val _countries = MutableLiveData<List<NewCountryData>>()
    val countries: LiveData<List<NewCountryData>> = _countries

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    fun fetchCountryData(countryId: Long, countryName: String) {
        viewModelScope.launch {
            try {
                val result = repository.fetchCountryData(countryId, countryName)
                result.onSuccess {
                    Log.d("CountryDataViewModel", "Data fetched successfully: $it")
                    _countryDataResult.postValue(Result.success(it))
                }.onFailure {
                    Log.e("CountryDataViewModel", "Failed to fetch data", it)
                    _countryDataResult.postValue(Result.failure(it))
                }
            } catch (e: Exception) {
                Log.e("CountryDataViewModel", "Exception during data fetch", e)
                _countryDataResult.postValue(Result.failure(e))
            }
        }
    }
    fun fetchAllCountries() {
        viewModelScope.launch {
            try {
                val countryList = repository.getAllCountries()
                Log.d("CountryDataViewModel", "Fetched countries: $countryList")
                _countries.postValue(countryList)
            } catch (e: Exception) {
                Log.e("CountryDataViewModel", "Exception during fetching countries", e)
                _countries.postValue(emptyList())
            }
        }
    }

    fun deleteDataByCountry(countryId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteDataByCountry(countryId)
                _deleteResult.postValue(Result.success(Unit))
                fetchAllCountries() // Refresh the list after deletion
            } catch (e: Exception) {
                Log.e("CountryDataViewModel", "Exception during deleting country data", e)
                _deleteResult.postValue(Result.failure(e))
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            try {
                repository.deleteAllData()
                _deleteResult.postValue(Result.success(Unit))
                fetchAllCountries() // Refresh the list after deletion
            } catch (e: Exception) {
                Log.e("CountryDataViewModel", "Exception during deleting all data", e)
                _deleteResult.postValue(Result.failure(e))
            }
        }
    }
}
