package asgarov.ui.world_map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asgarov.elchin.econvis.data.model.GiniData
import asgarov.elchin.econvis.data.repository.GiniRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WorldMapViewModel @Inject constructor(private val repository: GiniRepository) : ViewModel() {

    private val _giniData = MutableLiveData<List<GiniData>>()
    val giniData: LiveData<List<GiniData>> get() = _giniData

    fun fetchGiniData(year: Int) {
        viewModelScope.launch {
            // First try to fetch from local database
            val localData = repository.getGiniDataByYearLocal(year)
            if (localData.isNotEmpty()) {
                _giniData.postValue(localData)
            } else {
                // If local data is not available, fetch from remote and store locally
                repository.getGiniDataByYear(year).enqueue(object : Callback<List<GiniData>> {
                    override fun onResponse(call: Call<List<GiniData>>, response: Response<List<GiniData>>) {
                        if (response.isSuccessful) {
                            val giniDataList = response.body() ?: emptyList()
                            viewModelScope.launch {
                                repository.insertGiniData(giniDataList) // Insert into local database
                                _giniData.postValue(giniDataList)
                            }
                        } else {
                            Log.e("WorldMapViewModel", "Failed to fetch data: ${response.errorBody()}")
                            _giniData.postValue(emptyList())
                        }
                    }

                    override fun onFailure(call: Call<List<GiniData>>, t: Throwable) {
                        Log.e("WorldMapViewModel", "Error fetching data", t)
                        _giniData.postValue(emptyList())
                    }
                })
            }
        }
    }
}