package ci.nsu.mobile.main.repository

import ci.nsu.mobile.main.data.remote.ApiService
import ci.nsu.mobile.main.data.remote.LoginRequest
import ci.nsu.mobile.main.utils.SessionManager
import ci.nsu.mobile.main.data.remote.RegisterRequest

class AuthRepository(
    private val api: ApiService,
    private val sessionManager: SessionManager
) {
    suspend fun login(email: String, password: String) {
        val response = api.login(LoginRequest(email, password))
        sessionManager.saveAuth(response.token, response.userId)
    }
    suspend fun register(request: RegisterRequest) {
        val response = api.register(request)
        sessionManager.saveAuth(response.token, response.userId)
    }

    suspend fun getGroups() = api.getGroups()

    fun logout() = sessionManager.clearSession()
    fun isLoggedIn() = sessionManager.isLoggedIn()
    fun getUserId() = sessionManager.getUserId()
}