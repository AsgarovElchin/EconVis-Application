package asgarov.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asgarov.elchin.econvis.data.repository.CountryDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryDataViewModel @Inject constructor(
    application: Application,
    private val repository: CountryDataRepository
) : AndroidViewModel(application) {

    private val _countryDataResult = MutableLiveData<Result<Map<String, Any>>>()
    val countryDataResult: LiveData<Result<Map<String, Any>>> = _countryDataResult

    fun fetchCountryData(countryId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getCountryData(listOf(countryId.toString()))
                if (response.isSuccessful) {
                    response.body()?.let {
                        _countryDataResult.postValue(Result.success(it))
                    } ?: run {
                        _countryDataResult.postValue(Result.failure(Throwable("No data available")))
                    }
                } else {
                    _countryDataResult.postValue(Result.failure(Throwable(response.message())))
                }
            } catch (e: Exception) {
                _countryDataResult.postValue(Result.failure(e))
            }
        }
    }
}