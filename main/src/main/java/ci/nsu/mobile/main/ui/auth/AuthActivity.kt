package ci.nsu.mobile.main.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.databinding.ActivityAuthBinding
import ci.nsu.mobile.main.di.ServiceLocator
import ci.nsu.mobile.main.ui.main.MainActivity
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locator = ServiceLocator.getInstance(this)
        viewModel = ViewModelProvider(this, locator.viewModelFactory)[AuthViewModel::class.java]

        // Если уже авторизован — переходим в главное меню
        if (viewModel.isLoggedIn) {
            navigateToMain()
            return
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password) {
                navigateToMain()
            }
        }

        binding.tvNoAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let { Toast.makeText(this@AuthActivity, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}