package asgarov.ui.signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import asgarov.elchin.econvis.data.model.User
import asgarov.elchin.econvis.data.repository.UserRepository
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.lang.Exception

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository()

    private val _signupResult = MutableLiveData<Result<ResponseBody>>()
    val signupResult: LiveData<Result<ResponseBody>> = _signupResult

    fun signUp(user: User) {
        viewModelScope.launch {
            try {
                val response = userRepository.signUp(user)
                if (response.isSuccessful) {
                    _signupResult.postValue(Result.success(response.body()!!))
                } else {
                    _signupResult.postValue(Result.failure(Throwable(response.message())))
                }
            } catch (e: Exception) {
                _signupResult.postValue((Result.failure(e)))
            }
        }
    }

}