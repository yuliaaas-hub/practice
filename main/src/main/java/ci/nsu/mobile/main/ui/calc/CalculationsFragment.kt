package ci.nsu.mobile.main.ui.calc

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.databinding.FragmentCalculationsBinding
import ci.nsu.mobile.main.di.ServiceLocator
import kotlinx.coroutines.launch

class CalculationsFragment : Fragment(R.layout.fragment_calculations) {

    private var _binding: FragmentCalculationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CalculationsViewModel
    private lateinit var adapter: CalculationsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCalculationsBinding.bind(view)

        val locator = ServiceLocator.getInstance(requireContext())
        viewModel = ViewModelProvider(this, locator.viewModelFactory)[CalculationsViewModel::class.java]

        adapter = CalculationsAdapter { calc -> viewModel.delete(calc) }
        binding.recyclerViewCalculations.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCalculations.adapter = adapter

        viewModel.load()

        lifecycleScope.launch {
            viewModel.list.collect { calculations ->
                adapter.submitList(calculations)
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.load() // Обновляем при возврате на вкладку
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}