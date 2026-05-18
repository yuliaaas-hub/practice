package ci.nsu.mobile.main.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ci.nsu.mobile.main.data.remote.UserDto
import ci.nsu.mobile.main.databinding.ItemUserBinding

class UsersAdapter : ListAdapter<UserDto, UsersAdapter.UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UserViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserDto) {
            binding.tvUserName.text = user.name
            binding.tvUserEmail.text = user.email
            binding.tvUserId.text = "ID: ${user.id}"
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<UserDto>() {
        override fun areItemsTheSame(oldItem: UserDto, newItem: UserDto) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UserDto, newItem: UserDto) =
            oldItem == newItem
    }
}