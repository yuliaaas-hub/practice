package ci.nsu.mobile.main.ui.users

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.databinding.FragmentUsersBinding
import ci.nsu.mobile.main.di.ServiceLocator
import kotlinx.coroutines.launch

class UsersFragment : Fragment(R.layout.fragment_users) {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UsersViewModel
    private lateinit var adapter: UsersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUsersBinding.bind(view)

        val locator = ServiceLocator.getInstance(requireContext())
        viewModel = ViewModelProvider(this, locator.viewModelFactory)[UsersViewModel::class.java]

        adapter = UsersAdapter()
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsers.adapter = adapter

        // Загрузка при открытии
        viewModel.loadUsers()

        // Наблюдение за данными
        lifecycleScope.launch {
            viewModel.users.collect { users ->
                adapter.submitList(users)
            }
        }

        lifecycleScope.launch {
            viewModel.error.collect { error ->
                error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}