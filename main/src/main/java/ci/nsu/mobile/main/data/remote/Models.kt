package ci.nsu.mobile.main.data.remote


data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val userId: Long
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String
)

//  МОДЕЛЬ ДЛЯ РЕГИСТРАЦИИ
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val middleName: String?,
    val dateOfBirth: String, // формат "YYYY-MM-DD"
    val gender: String, // "MALE" или "FEMALE"
    val groupId: Long?,
    val login: String,
    val password: String,
    val email: String,
    val phone: String?
)

//  МОДЕЛЬ ГРУППЫ (для выпадающего списка)
data class GroupDto(
    val id: Long,
    val name: String
)

//  ВАРИАНТЫ ПОЛА
enum class Gender(val value: String) {
    MALE("MALE"),
    FEMALE("FEMALE")
}