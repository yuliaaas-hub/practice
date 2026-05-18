package ci.nsu.mobile.main.ui.calc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.local.DepositCalculation
import ci.nsu.mobile.main.repository.AuthRepository
import ci.nsu.mobile.main.repository.DepositRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CalculationsViewModel(
    private val repository: DepositRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _list = MutableStateFlow<List<DepositCalculation>>(emptyList())
    val list: StateFlow<List<DepositCalculation>> = _list

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun load() {
        val userId = authRepository.getUserId()
        if (userId == -1L) return

        viewModelScope.launch {
            _isLoading.value = true
            repository.getCalculations(userId)
                .catch { e -> _error.value = e.message }
                .collect { calculations ->
                    _list.value = calculations
                    _isLoading.value = false
                }
        }
    }

    fun delete(calc: DepositCalculation) = viewModelScope.launch {
        repository.delete(calc)
        load()
    }
}