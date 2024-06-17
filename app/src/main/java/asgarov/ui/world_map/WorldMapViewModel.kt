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
class WorldMapViewModel @Inject constructor(private val repository: GiniRepository): ViewModel() {

    private val _giniData = MutableLiveData<List<GiniData>>()
    val giniData: LiveData<List<GiniData>> get() = _giniData

    fun fetchGiniData(year: Int) {
        viewModelScope.launch {
            repository.getGiniDataByYear(year).enqueue(object : Callback<List<GiniData>> {
                override fun onResponse(call: Call<List<GiniData>>, response: Response<List<GiniData>>) {
                    if (response.isSuccessful) {
                        Log.d("WorldMapViewModel", "Data fetched successfully: ${response.body()}")
                        _giniData.postValue(response.body())
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