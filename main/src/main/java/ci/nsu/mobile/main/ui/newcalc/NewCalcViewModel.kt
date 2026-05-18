package ci.nsu.mobile.main.ui.newcalc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ci.nsu.mobile.main.data.local.DepositCalculation
import ci.nsu.mobile.main.repository.AuthRepository
import ci.nsu.mobile.main.repository.DepositRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow

class NewCalcViewModel(
    private val repository: DepositRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun calculate(
        initialAmount: Double,
        periodMonths: Int,
        interestRate: Double,
        monthlyTopUp: Double?
    ): Pair<Double, Double> {
        var amount = initialAmount
        val monthlyRate = interestRate / 100 / 12

        for (month in 1..periodMonths) {
            amount += amount * monthlyRate
            monthlyTopUp?.let { amount += it }
        }

        val interestEarned = amount - initialAmount - (monthlyTopUp ?: 0.0) * periodMonths
        return Pair(amount, interestEarned)
    }

    fun saveCalculation(
        initialAmount: Double,
        periodMonths: Int,
        interestRate: Double,
        monthlyTopUp: Double?,
        finalAmount: Double,
        interestEarned: Double
    ) {
        val userId = authRepository.getUserId()
        if (userId == -1L) {
            _error.value = "Пользователь не авторизован"
            return
        }

        viewModelScope.launch {
            try {
                val calc = DepositCalculation(
                    userId = userId,
                    initialAmount = initialAmount,
                    periodMonths = periodMonths,
                    interestRate = interestRate,
                    monthlyTopUp = monthlyTopUp,
                    finalAmount = finalAmount,
                    interestEarned = interestEarned
                )
                repository.save(calc)
                _saved.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка сохранения"
            }
        }
    }

    fun resetSaved() { _saved.value = false }
}