package ci.nsu.mobile.main.data.network

import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://192.168.200.160:8080/api/"

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(
            OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor())
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        )
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}