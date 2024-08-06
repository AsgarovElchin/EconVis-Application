package asgarov.elchin.econvis.data.repository

import android.content.Context
import android.util.Log
import asgarov.elchin.econvis.data.model.ResetData
import asgarov.elchin.econvis.data.model.User
import asgarov.elchin.econvis.data.model.VerificationData
import asgarov.elchin.econvis.data.network.APIService
import asgarov.elchin.econvis.utils.PreferenceHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: APIService, @ApplicationContext private val context: Context) {


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

    fun logout() {
        // Log the logout process
        Log.d("UserRepository", "Logging out...")
        // Clear user data from SharedPreferences
        PreferenceHelper.setUserLoggedIn(context, false)
        PreferenceHelper.setUserOnboarded(context, false)
        // Verify if preferences are set correctly
        Log.d("UserRepository", "isUserLoggedIn: ${PreferenceHelper.isUserLoggedIn(context)}")
        Log.d("UserRepository", "isUserOnboarded: ${PreferenceHelper.isUserOnboarded(context)}")
        // Additional cleanup if necessary, e.g., invalidating tokens
    }

}