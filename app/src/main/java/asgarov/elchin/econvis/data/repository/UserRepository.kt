package asgarov.elchin.econvis.data.repository

import asgarov.elchin.econvis.data.model.ResetData
import asgarov.elchin.econvis.data.model.User
import asgarov.elchin.econvis.data.model.VerificationData
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

    suspend fun requestPasswordReset(email: String): Response<ResponseBody> {
        return apiService.requestPasswordReset(mapOf("email" to email))
    }

    suspend fun verifyCode(email: String, code: String): Response<ResponseBody> {
        val verificationData = VerificationData(email, code)
        return apiService.verifyCode(verificationData)
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Response<ResponseBody> {
        val resetData = ResetData(email, code, newPassword)
        return apiService.resetPassword(resetData)
    }
}