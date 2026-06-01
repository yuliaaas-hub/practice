package ci.nsu.mobile.main.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.ui.components.AppTextField
import ci.nsu.mobile.main.ui.viewmodel.AuthViewModel
import ci.nsu.mobile.main.ui.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Authenticated -> onLoginSuccess()
            is AuthUiState.RegistrationSuccess -> {
                // Показываем сообщение об успешной регистрации
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Вход в систему",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            AppTextField(
                value = login,
                onValueChange = {
                    login = it
                    if (it.isNotEmpty()) loginError = null
                },
                label = "Логин",
                isError = loginError != null,
                errorMessage = loginError
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (it.isNotEmpty()) passwordError = null
                },
                label = "Пароль",
                isError = passwordError != null,
                errorMessage = passwordError,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (login.isBlank()) {
                        loginError = "Введите логин"
                        isValid = false
                    }
                    if (password.isBlank()) {
                        passwordError = "Введите пароль"
                        isValid = false
                    }
                    if (isValid) {
                        viewModel.login(login, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = uiState !is AuthUiState.Loading
            ) {
                Text("Войти")
            }

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Нет аккаунта? Зарегистрироваться")
            }
        }

        if (uiState is AuthUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}