package asgarov.ui.resest_password

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import asgarov.elchin.econvis.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val _passwordResetResult = MutableLiveData<Result<ResponseBody>>()
    val passwordResetResult: LiveData<Result<ResponseBody>> = _passwordResetResult

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.requestPasswordReset(email)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("ResetPasswordViewModel", "Response Body: ${body.string()}")
                        _passwordResetResult.postValue(Result.success(body))
                    } else {
                        Log.e("ResetPasswordViewModel", "Empty response body")
                        _passwordResetResult.postValue(Result.failure(Throwable("Empty response body")))
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: response.message()
                    Log.e("ResetPasswordViewModel", "Error: $errorMessage")
                    _passwordResetResult.postValue(Result.failure(Throwable(errorMessage)))
                }
            } catch (e: Exception) {
                Log.e("ResetPasswordViewModel", "Exception: ${e.message}", e)
                _passwordResetResult.postValue(Result.failure(e))
            }
        }
    }
}