package ci.nsu.mobile.main.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.remote.UserDto
import ci.nsu.mobile.main.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsersViewModel(private val repository: UserRepository) : ViewModel() {

    private val _users = MutableStateFlow<List<UserDto>>(emptyList())
    val users: StateFlow<List<UserDto>> = _users

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _users.value = repository.getUsers()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки"
            } finally {
                _isLoading.value = false
            }
        }
    }
}