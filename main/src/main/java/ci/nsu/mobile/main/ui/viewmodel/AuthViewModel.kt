package ci.nsu.mobile.main.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.model.*
import ci.nsu.mobile.main.data.network.NetworkResult
import ci.nsu.mobile.main.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = mutableStateOf<AuthUiState>(AuthUiState.Idle)
    val uiState: State<AuthUiState> = _uiState

    private val _groups = mutableStateOf<List<GroupDto>>(emptyList())
    val groups: State<List<GroupDto>> = _groups

    private val _users = mutableStateOf<List<UserDto>>(emptyList())
    val users: State<List<UserDto>> = _users

    fun loadGroups() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = repository.getGroups()) {
                is NetworkResult.Success -> {
                    _groups.value = result.data
                    _uiState.value = AuthUiState.Idle
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState.Error(result.message)
                }
                is NetworkResult.Loading -> {
                    _uiState.value = AuthUiState.Loading
                }
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = repository.getUsers()) {
                is NetworkResult.Success -> {
                    _users.value = result.data
                    _uiState.value = AuthUiState.Idle
                }
                is NetworkResult.Error -> {
                    _uiState.value = if (result.code == 401) {
                        AuthUiState.Unauthorized(result.message)
                    } else {
                        AuthUiState.Error(result.message)
                    }
                }
                is NetworkResult.Loading -> {
                    _uiState.value = AuthUiState.Loading
                }
            }
        }
    }

    fun login(login: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = repository.login(login, password)) {
                is NetworkResult.Success -> {
                    _uiState.value = AuthUiState.Authenticated(result.data)
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState.Error(result.message)
                }
                is NetworkResult.Loading -> {
                    _uiState.value = AuthUiState.Loading
                }
            }
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            when (val result = repository.register(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = AuthUiState.RegistrationSuccess
                }
                is NetworkResult.Error -> {
                    _uiState.value = AuthUiState.Error(result.message)
                }
                is NetworkResult.Loading -> {
                    _uiState.value = AuthUiState.Loading
                }
            }
        }
    }

    fun logout() {
        repository.logout()
        _users.value = emptyList()
        _uiState.value = AuthUiState.LoggedOut
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Authenticated(val user: UserDto) : AuthUiState()
    object RegistrationSuccess : AuthUiState()
    object LoggedOut : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    data class Unauthorized(val message: String) : AuthUiState()
}