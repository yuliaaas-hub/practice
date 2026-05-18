package ci.nsu.mobile.main.di

import android.content.Context
import ci.nsu.mobile.main.data.local.AppDatabase
import ci.nsu.mobile.main.data.remote.ApiService
import ci.nsu.mobile.main.data.remote.AuthInterceptor
import ci.nsu.mobile.main.repository.AuthRepository
import ci.nsu.mobile.main.repository.DepositRepository
import ci.nsu.mobile.main.repository.UserRepository
import ci.nsu.mobile.main.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceLocator private constructor(context: Context) {
    private val ctx = context.applicationContext

    val sessionManager: SessionManager by lazy { SessionManager(ctx) }

    val database: AppDatabase by lazy { AppDatabase.getDatabase(ctx) }

    private val logging: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(sessionManager))
            .build()
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
//            .baseUrl("http://192.168.200.160:8080/api/")
            .baseUrl("http://10.0.2.2:8080/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(apiService, sessionManager)
    }

    val depositRepository: DepositRepository by lazy {
        DepositRepository(database.depositDao())
    }

    val userRepository: UserRepository by lazy {
        UserRepository(apiService)
    }

    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(this)
    }

    companion object {
        @Volatile private var instance: ServiceLocator? = null

        fun getInstance(context: Context): ServiceLocator =
            instance ?: synchronized(this) {
                instance ?: ServiceLocator(context).also { instance = it }
            }
    }
}