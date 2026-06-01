package ci.nsu.mobile.main.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ci.nsu.mobile.main.data.model.UserDto
import ci.nsu.mobile.main.ui.components.LoadingIndicator
import ci.nsu.mobile.main.ui.viewmodel.AuthViewModel
import ci.nsu.mobile.main.ui.viewmodel.AuthUiState

@Composable
fun MainScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    val users by viewModel.users
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пользователи") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Выйти"
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is AuthUiState.Loading -> {
                LoadingIndicator()
            }
            is AuthUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is AuthUiState.Unauthorized -> {
                LaunchedEffect(Unit) {
                    onLogout()
                }
            }
            else -> {
                if (users.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Нет пользователей")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(users) { user ->
                            UserCard(user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(user: UserDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = user.person?.let { "${it.lastName} ${it.firstName}" } ?: user.login,
                style = MaterialTheme.typography.titleMedium
            )
            user.email?.let { email ->
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            user.person?.birthDate?.let { birthDate ->
                Text(
                    text = "Дата рождения: $birthDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}