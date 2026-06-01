package ci.nsu.mobile.main.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.data.model.*
import ci.nsu.mobile.main.ui.components.AppTextField
import ci.nsu.mobile.main.ui.viewmodel.AuthViewModel
import ci.nsu.mobile.main.ui.viewmodel.AuthUiState

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var patronymic by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("MALE") }
    var selectedGroupId by remember { mutableStateOf<Int?>(null) }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var groupDropdownExpanded by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState
    val groups by viewModel.groups

    LaunchedEffect(Unit) {
        if (groups.isEmpty()) {
            viewModel.loadGroups()
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.RegistrationSuccess -> {
                onRegistrationSuccess()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            AppTextField(value = lastName, onValueChange = { lastName = it }, label = "Фамилия *")
            AppTextField(value = firstName, onValueChange = { firstName = it }, label = "Имя *")
            AppTextField(value = patronymic, onValueChange = { patronymic = it }, label = "Отчество")
            AppTextField(value = dateOfBirth, onValueChange = { dateOfBirth = it }, label = "Дата рождения (ГГГГ-ММ-ДД) *")

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Пол: *")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == "MALE",
                            onClick = { gender = "MALE" }
                        )
                        Text("М")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == "FEMALE",
                            onClick = { gender = "FEMALE" }
                        )
                        Text("Ж")
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = groupDropdownExpanded,
                onExpandedChange = { groupDropdownExpanded = !groupDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedGroupId?.let { id ->
                        groups.find { it.id == id }?.name ?: "Выберите группу"
                    } ?: "Выберите группу *",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = groupDropdownExpanded,
                    onDismissRequest = { groupDropdownExpanded = false }
                ) {
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                selectedGroupId = group.id
                                groupDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            AppTextField(value = login, onValueChange = { login = it }, label = "Логин *")
            AppTextField(
                value = password,
                onValueChange = { password = it },
                label = "Пароль *",
                visualTransformation = PasswordVisualTransformation()
            )
            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email *",
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            AppTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Телефон",
                keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (validateAndRegister(
                            firstName, lastName, dateOfBirth, selectedGroupId,
                            login, password, email, phoneNumber, gender, viewModel
                        )) {
                        // Успешная валидация
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = uiState !is AuthUiState.Loading
            ) {
                Text("Зарегистрироваться")
            }

            TextButton(onClick = onNavigateBack) {
                Text("Уже есть аккаунт? Войти")
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

private fun validateAndRegister(
    firstName: String,
    lastName: String,
    dateOfBirth: String,
    selectedGroupId: Int?,
    login: String,
    password: String,
    email: String,
    phoneNumber: String,
    gender: String,
    viewModel: AuthViewModel
): Boolean {
    if (firstName.isBlank() || lastName.isBlank() || dateOfBirth.isBlank() ||
        selectedGroupId == null || login.isBlank() || password.length < 6 ||
        email.isBlank()) {
        return false
    }

    val person = PersonDto(
        firstName = firstName,
        lastName = lastName,
        middleName = null,
        birthDate = dateOfBirth,
        gender = gender,
        groupId = selectedGroupId
    )

    val request = RegisterRequest(
        login = login,
        password = password,
        email = email,
        phoneNumber = phoneNumber.ifBlank { null },
        person = person
    )

    viewModel.register(request)
    return true
}