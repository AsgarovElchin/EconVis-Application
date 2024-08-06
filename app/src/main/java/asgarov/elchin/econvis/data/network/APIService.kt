package asgarov.elchin.econvis.data.network

import asgarov.elchin.econvis.data.model.ApiResponse
import asgarov.elchin.econvis.data.model.Country
import asgarov.elchin.econvis.data.model.GiniData
import asgarov.elchin.econvis.data.model.Indicator
import asgarov.elchin.econvis.data.model.Report
import asgarov.elchin.econvis.data.model.ReportRequest
import asgarov.elchin.econvis.data.model.ResetData
import asgarov.elchin.econvis.data.model.User
import asgarov.elchin.econvis.data.model.VerificationData
import asgarov.elchin.econvis.data.model.Year
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
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

    @POST("api/reports")
    fun getReports(@Body reportRequest: ReportRequest): Call<List<Report>>

    @GET("api/reports/countries")
    fun getCountries(): Call<List<Country>>

    @GET("api/reports/indicators")
    fun getIndicators(): Call<List<Indicator>>

    @GET("api/reports/years")
    fun getYears(): Call<List<Year>>

    @GET("/api/countrydata")
    suspend fun getCountryData(@Query("countryId") countryId: Long): Response<ApiResponse>
}



