package ci.nsu.mobile.main.ui.newcalc

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.databinding.FragmentNewCalcBinding
import ci.nsu.mobile.main.di.ServiceLocator
import kotlinx.coroutines.launch

class NewCalcFragment : Fragment(R.layout.fragment_new_calc) {

    private var _binding: FragmentNewCalcBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NewCalcViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewCalcBinding.bind(view)

        val locator = ServiceLocator.getInstance(requireContext())
        viewModel = ViewModelProvider(this, locator.viewModelFactory)[NewCalcViewModel::class.java]

        binding.btnCalculate.setOnClickListener { calculate() }
        binding.btnSave.setOnClickListener { saveCalculation() }

        lifecycleScope.launch {
            viewModel.saved.collect { saved ->
                if (saved) {
                    Toast.makeText(context, "Расчёт сохранён!", Toast.LENGTH_SHORT).show()
                    viewModel.resetSaved()
                    clearForm()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun calculate() {
        val initial = binding.etInitialAmount.text.toString().toDoubleOrNull() ?: 0.0
        val period = binding.etPeriod.text.toString().toIntOrNull() ?: 0
        val rate = binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
        val topUp = binding.etTopUp.text.toString().toDoubleOrNull()

        if (initial <= 0 || period <= 0 || rate <= 0) {
            Toast.makeText(context, "Проверьте введённые данные", Toast.LENGTH_SHORT).show()
            return
        }

        val (final, interest) = viewModel.calculate(initial, period, rate, topUp)

        binding.tvResultFinal.text = "%.2f ₽".format(final)
        binding.tvResultInterest.text = "%.2f ₽".format(interest)
        binding.btnSave.isEnabled = true
    }

    private fun saveCalculation() {
        val initial = binding.etInitialAmount.text.toString().toDoubleOrNull() ?: 0.0
        val period = binding.etPeriod.text.toString().toIntOrNull() ?: 0
        val rate = binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
        val topUp = binding.etTopUp.text.toString().toDoubleOrNull()
        val final = binding.tvResultFinal.text.toString().replace(" ₽", "").toDoubleOrNull() ?: 0.0
        val interest = binding.tvResultInterest.text.toString().replace(" ₽", "").toDoubleOrNull() ?: 0.0

        viewModel.saveCalculation(initial, period, rate, topUp, final, interest)
    }

    private fun clearForm() {
        binding.etInitialAmount.text?.clear()
        binding.etPeriod.text?.clear()
        binding.etRate.text?.clear()
        binding.etTopUp.text?.clear()
        binding.tvResultFinal.text = "-"
        binding.tvResultInterest.text = "-"
        binding.btnSave.isEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}