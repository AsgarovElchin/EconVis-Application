package asgarov.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import asgarov.elchin.econvis.data.model.User
import asgarov.elchin.econvis.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val _signInResult = MutableLiveData<Result<ResponseBody>>()
    val signInResult: LiveData<Result<ResponseBody>> = _signInResult

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRepository.signIn(email, password)
                if (response.isSuccessful) {
                    _signInResult.postValue(Result.success(response.body()!!))
                } else {
                    _signInResult.postValue(Result.failure(Throwable(response.message())))
                }
            } catch (e: Exception) {
                _signInResult.postValue(Result.failure(e))
            }
        }
    }


}
