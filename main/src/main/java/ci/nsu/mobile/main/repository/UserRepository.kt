package ci.nsu.mobile.main.repository

import ci.nsu.mobile.main.data.remote.ApiService
import ci.nsu.mobile.main.data.remote.UserDto

class UserRepository(private val api: ApiService) {
    suspend fun getUsers(): List<UserDto> = api.getUsers()
}