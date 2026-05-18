package ci.nsu.mobile.main.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.remote.Gender
import ci.nsu.mobile.main.data.remote.GroupDto
import ci.nsu.mobile.main.data.remote.RegisterRequest
import ci.nsu.mobile.main.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    private val _groups = MutableStateFlow<List<GroupDto>>(emptyList())
    val groups: StateFlow<List<GroupDto>> = _groups

    fun loadGroups() {
        viewModelScope.launch {
            try {
                _groups.value = repository.getGroups()
            } catch (e: Exception) {
                _error.value = "Не удалось загрузить группы: ${e.message}"
            }
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        middleName: String?,
        dateOfBirth: String,
        gender: Gender,
        groupId: Long?,
        login: String,
        password: String,
        email: String,
        phone: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = RegisterRequest(
                    firstName = firstName,
                    lastName = lastName,
                    middleName = middleName,
                    dateOfBirth = dateOfBirth,
                    gender = gender.value,
                    groupId = groupId,
                    login = login,
                    password = password,
                    email = email,
                    phone = phone
                )
                repository.register(request)
                _success.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка регистрации"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetSuccess() { _success.value = false }
}