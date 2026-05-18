package ci.nsu.mobile.main.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ci.nsu.mobile.main.ui.auth.AuthViewModel
import ci.nsu.mobile.main.ui.calc.CalculationsViewModel
import ci.nsu.mobile.main.ui.newcalc.NewCalcViewModel
import ci.nsu.mobile.main.ui.users.UsersViewModel
import ci.nsu.mobile.main.ui.auth.RegisterViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val locator: ServiceLocator) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(locator.authRepository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(locator.authRepository) as T
            modelClass.isAssignableFrom(UsersViewModel::class.java) ->
                UsersViewModel(locator.userRepository) as T
            modelClass.isAssignableFrom(CalculationsViewModel::class.java) ->
                CalculationsViewModel(locator.depositRepository, locator.authRepository) as T
            modelClass.isAssignableFrom(NewCalcViewModel::class.java) ->
                NewCalcViewModel(locator.depositRepository, locator.authRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}