package ci.nsu.mobile.main.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("token")
    val token: String,
    @SerialName("user")
    val user: UserDto
)

@Serializable
data class UserDto(
    @SerialName("userId")
    val id: Int,
    @SerialName("login")
    val login: String,
    @SerialName("email")
    val email: String?,
    @SerialName("person")
    val person: PersonDto?
)