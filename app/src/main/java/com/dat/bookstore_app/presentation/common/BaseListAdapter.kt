package com.plus.baseandroidapp.presentation.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

abstract class BaseListAdapter<T: Any, VB: ViewBinding, VH : BaseViewHolder<T, VB>>(
    private val inflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback) {

    protected abstract fun createViewHolder(binding: VB): VH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = inflater(LayoutInflater.from(parent.context), parent, false)
        return createViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

}

/*
Used:
data class UserClickListener(
    val onItemClick: (User) -> Unit,
    val onEditClick: (User) -> Unit,
    val onDeleteClick: (User) -> Unit
)
class UserAdapter(
    private val clickListener: UserClickListener
) : BaseListAdapter<User, ItemUserBinding, UserViewHolder>(
    ItemUserBinding::inflate,
    UserDiffCallback()
) {
    override fun createViewHolder(binding: ItemUserBinding): UserViewHolder {
        return UserViewHolder(binding, clickListener)
    }
}

class UserViewHolder(
    binding: ItemUserBinding,
    private val clickListener: UserClickListener
) : BaseViewHolder<User, ItemUserBinding>(binding) {

    override fun bind(item: User) {
        binding.tvName.text = item.name

        binding.root.setOnClickListener {
            clickListener.onItemClick(item)
        }

        binding.btnEdit.setOnClickListener {
            clickListener.onEditClick(item)
        }

        binding.btnDelete.setOnClickListener {
            clickListener.onDeleteClick(item)
        }
    }
}
 */