package asgarov.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import asgarov.elchin.econvis.data.model.CountryData
import asgarov.elchin.econvis.data.model.CountryIndicatorData
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

    fun fetchCountryData(countryId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.fetchCountryData(countryId)
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


}