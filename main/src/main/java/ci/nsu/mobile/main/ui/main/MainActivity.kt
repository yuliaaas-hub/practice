package ci.nsu.mobile.main.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.databinding.ActivityMainBinding
import ci.nsu.mobile.main.ui.auth.AuthViewModel
import ci.nsu.mobile.main.di.ServiceLocator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locator = ServiceLocator.getInstance(this)
        authViewModel = AuthViewModel(locator.authRepository)

        // Настройка Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        // Кнопка выхода
        binding.btnLogout.setOnClickListener {
            authViewModel.logout()
            finish() // Возврат на AuthActivity
        }
    }
}