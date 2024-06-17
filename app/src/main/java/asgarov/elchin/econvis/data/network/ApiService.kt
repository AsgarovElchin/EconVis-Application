package asgarov.elchin.econvis.data.network

import asgarov.elchin.econvis.data.model.GiniData
import asgarov.elchin.econvis.data.model.ResetData
import asgarov.elchin.econvis.data.model.User
import asgarov.elchin.econvis.data.model.VerificationData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/users/signup")
    suspend fun signUp(@Body user: User): Response<ResponseBody>

    @FormUrlEncoded
    @POST("api/users/signin")
    suspend fun signIn(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<ResponseBody>

    @POST("password/request-reset")
    suspend fun requestPasswordReset(@Body emailRequest: Map<String, String>): Response<ResponseBody>

    @POST("password/verify-code")
    suspend fun verifyCode(@Body verificationData: VerificationData): Response<ResponseBody>

    @POST("password/reset")
    suspend fun resetPassword(@Body resetData: ResetData): Response<ResponseBody>

    @GET("api/gini/year/{year}")
    fun getGiniDataByYear(@Path("year") year: Int): Call<List<GiniData>>
}


