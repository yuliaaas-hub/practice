package ci.nsu.mobile.main.data.remote


import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("groups")
    suspend fun getGroups(): List<GroupDto>
}