package asgarov.elchin.econvis.data.repository

import asgarov.elchin.econvis.data.model.User
import asgarov.elchin.econvis.data.network.ApiService
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) {


    suspend fun signUp(user: User): Response<ResponseBody> {
        return apiService.signUp(user)
    }

    suspend fun signIn(email: String, password: String): Response<ResponseBody> {
        return apiService.signIn(email, password)
    }
}