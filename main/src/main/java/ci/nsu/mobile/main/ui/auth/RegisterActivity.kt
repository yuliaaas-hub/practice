package ci.nsu.mobile.main.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.data.remote.Gender
import ci.nsu.mobile.main.data.remote.GroupDto
import ci.nsu.mobile.main.databinding.ActivityRegisterBinding
import ci.nsu.mobile.main.di.ServiceLocator
import ci.nsu.mobile.main.ui.main.MainActivity
import kotlinx.coroutines.launch
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    private var selectedGender: Gender = Gender.MALE
    private var selectedGroupId: Long? = null
    private var groups: List<GroupDto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locator = ServiceLocator.getInstance(this)
        viewModel = ViewModelProvider(this, locator.viewModelFactory)[RegisterViewModel::class.java]

        setupUI()
        observeData()

        viewModel.loadGroups()
    }

    private fun setupUI() {
        // 🔹 Кнопка выбора даты рождения
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        // 🔹 RadioGroup для выбора пола
        binding.radioGroupGender.setOnCheckedChangeListener { _, checkedId ->
            selectedGender = when (checkedId) {
                R.id.radioMale -> Gender.MALE
                R.id.radioFemale -> Gender.FEMALE
                else -> Gender.MALE
            }
        }

        // 🔹 Кнопка регистрации
        binding.btnRegister.setOnClickListener {
            registerUser()
        }

        // 🔹 Ссылка "Уже есть аккаунт? Войти"
        binding.tvAlreadyHaveAccount.setOnClickListener {
            finish() // Возврат на AuthActivity
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.groups.collect { groupList ->
                groups = groupList
                setupGroupSpinner(groupList)
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let { Toast.makeText(this@RegisterActivity, it, Toast.LENGTH_SHORT).show() }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                binding.btnRegister.isEnabled = !loading
            }
        }

        lifecycleScope.launch {
            viewModel.success.collect { success ->
                if (success) {
                    Toast.makeText(this@RegisterActivity, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun setupGroupSpinner(groupList: List<GroupDto>) {
        val groupNames = groupList.map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGroup.adapter = adapter

        binding.spinnerGroup.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedGroupId = groupList[position].id
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val dateOfBirth = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            binding.etDateOfBirth.setText(dateOfBirth)
        }, year, month, day).show()
    }

    private fun registerUser() {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val middleName = binding.etMiddleName.text.toString().trim().ifEmpty { null }
        val dateOfBirth = binding.etDateOfBirth.text.toString().trim()
        val login = binding.etLogin.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim().ifEmpty { null }

        if (firstName.isEmpty() || lastName.isEmpty() || dateOfBirth.isEmpty() ||
            login.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.register(
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            dateOfBirth = dateOfBirth,
            gender = selectedGender,
            groupId = selectedGroupId,
            login = login,
            password = password,
            email = email,
            phone = phone
        )
    }
}