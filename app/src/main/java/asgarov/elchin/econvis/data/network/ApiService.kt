package asgarov.elchin.econvis.data.network

import asgarov.elchin.econvis.data.model.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @POST("api/users/signup")
    suspend fun signUp(@Body user: User): Response<ResponseBody>

    @FormUrlEncoded
    @POST("api/users/signin")
    suspend fun signIn(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<ResponseBody>


}