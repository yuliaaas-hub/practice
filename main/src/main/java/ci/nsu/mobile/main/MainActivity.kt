package ci.nsu.mobile.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ci.nsu.mobile.main.data.network.TokenManager
import ci.nsu.mobile.main.ui.navigation.AppNavGraph
import ci.nsu.mobile.main.ui.theme.AuthAppTheme
import ci.nsu.mobile.main.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(applicationContext)

        setContent {
            AuthAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: AuthViewModel = viewModel()
                    AppNavGraph(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}