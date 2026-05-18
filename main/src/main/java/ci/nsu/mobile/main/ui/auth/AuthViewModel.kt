package ci.nsu.mobile.main.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    val isLoggedIn: Boolean get() = repository.isLoggedIn()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.login(email, password)
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка авторизации"
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() = repository.logout()
}