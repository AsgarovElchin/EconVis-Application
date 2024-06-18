package asgarov.ui.comparison

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.Indicator
import asgarov.elchin.econvis.data.model.Report
import asgarov.elchin.econvis.data.model.ReportRequest
import asgarov.elchin.econvis.data.model.Year
import asgarov.elchin.econvis.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ComparisonViewModel @Inject constructor(
    application: Application,
    private val reportRepository: ReportRepository
) : AndroidViewModel(application) {

    private val _countries = MutableLiveData<Result<List<Country>>>()
    val countries: LiveData<Result<List<Country>>> = _countries

    private val _indicators = MutableLiveData<Result<List<Indicator>>>()
    val indicators: LiveData<Result<List<Indicator>>> = _indicators

    private val _years = MutableLiveData<Result<List<Year>>>()
    val years: LiveData<Result<List<Year>>> = _years

    private val _reports = MutableLiveData<Result<List<Report>>>()
    val reports: LiveData<Result<List<Report>>> = _reports

    fun fetchCountries() {
        reportRepository.getCountries().enqueue(object : Callback<List<Country>> {
            override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                if (response.isSuccessful) {
                    _countries.postValue(Result.success(response.body()!!))
                } else {
                    _countries.postValue(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                _countries.postValue(Result.failure(t))
            }
        })
    }

    fun fetchIndicators() {
        reportRepository.getIndicators().enqueue(object : Callback<List<Indicator>> {
            override fun onResponse(
                call: Call<List<Indicator>>,
                response: Response<List<Indicator>>
            ) {
                if (response.isSuccessful) {
                    _indicators.postValue(Result.success(response.body()!!))
                } else {
                    _indicators.postValue(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<List<Indicator>>, t: Throwable) {
                _indicators.postValue(Result.failure(t))
            }
        })
    }

    fun fetchYears() {
        reportRepository.getYears().enqueue(object : Callback<List<Year>> {
            override fun onResponse(call: Call<List<Year>>, response: Response<List<Year>>) {
                if (response.isSuccessful) {
                    _years.postValue(Result.success(response.body()!!))
                } else {
                    _years.postValue(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<List<Year>>, t: Throwable) {
                _years.postValue(Result.failure(t))
            }
        })
    }

    fun fetchReports(reportRequest: ReportRequest) {
        reportRepository.getReports(reportRequest).enqueue(object : Callback<List<Report>> {
            override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {
                if (response.isSuccessful) {
                    _reports.postValue(Result.success(response.body()!!))
                } else {
                    _reports.postValue(Result.failure(Throwable(response.message())))
                }
            }

            override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                _reports.postValue(Result.failure(t))
            }
        })
    }
}