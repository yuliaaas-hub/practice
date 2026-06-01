package ci.nsu.mobile.main.data.repository


import ci.nsu.mobile.main.data.model.*
import ci.nsu.mobile.main.data.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val apiService: ApiService = ApiClient.apiService
) {
    suspend fun login(login: String, password: String): NetworkResult<UserDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(login, password))
            TokenManager.token = response.token
            NetworkResult.Success(response.user)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            NetworkResult.Error(parseErrorMessage(errorBody), e.code())
        } catch (e: IOException) {
            NetworkResult.Error("Ошибка сети: проверьте подключение")
        } catch (e: Exception) {
            NetworkResult.Error("Неизвестная ошибка: ${e.message}")
        }
    }

    suspend fun register(request: RegisterRequest): NetworkResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.register(request)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                NetworkResult.Error(parseErrorMessage(errorBody), response.code())
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            NetworkResult.Error(parseErrorMessage(errorBody), e.code())
        } catch (e: IOException) {
            NetworkResult.Error("Ошибка сети: проверьте подключение")
        } catch (e: Exception) {
            NetworkResult.Error("Неизвестная ошибка: ${e.message}")
        }
    }

    suspend fun getUsers(): NetworkResult<List<UserDto>> = withContext(Dispatchers.IO) {
        try {
            val users = apiService.getUsers()
            NetworkResult.Success(users)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                TokenManager.clearToken()
                NetworkResult.Error("Требуется авторизация", 401)
            } else {
                NetworkResult.Error(parseErrorMessage(e.response()?.errorBody()?.string()), e.code())
            }
        } catch (e: IOException) {
            NetworkResult.Error("Ошибка сети: проверьте подключение")
        } catch (e: Exception) {
            NetworkResult.Error("Неизвестная ошибка: ${e.message}")
        }
    }

    suspend fun getGroups(): NetworkResult<List<GroupDto>> = withContext(Dispatchers.IO) {
        try {
            val groups = apiService.getGroups()
            NetworkResult.Success(groups)
        } catch (e: HttpException) {
            NetworkResult.Error(parseErrorMessage(e.response()?.errorBody()?.string()), e.code())
        } catch (e: IOException) {
            NetworkResult.Error("Ошибка сети: проверьте подключение")
        } catch (e: Exception) {
            NetworkResult.Error("Неизвестная ошибка: ${e.message}")
        }
    }

    fun logout() {
        TokenManager.clearToken()
    }

    private fun parseErrorMessage(errorBody: String?): String {
        return try {
            if (errorBody.isNullOrEmpty()) return "Неизвестная ошибка"
            val json = Json { ignoreUnknownKeys = true }
            val apiError = json.decodeFromString<ApiError>(errorBody)
            apiError.message.ifEmpty { "Ошибка сервера" }
        } catch (e: Exception) {
            "Ошибка: ${e.message}"
        }
    }
}