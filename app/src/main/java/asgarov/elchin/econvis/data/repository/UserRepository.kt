package asgarov.elchin.econvis.data.repository

import asgarov.elchin.econvis.data.model.User
import asgarov.elchin.econvis.data.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Response

class UserRepository {
    private val apiService = RetrofitClient.apiService

    suspend fun signUp(user: User): Response<ResponseBody> {
        return apiService.signUp(user)
    }
}