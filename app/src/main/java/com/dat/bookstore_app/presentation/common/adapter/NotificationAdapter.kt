package com.dat.bookstore_app.presentation.common.adapter

import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.databinding.ItemNotificationBinding
import com.dat.bookstore_app.domain.models.Notification
import com.dat.bookstore_app.presentation.common.base.BasePagingAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder

class NotificationAdapter(
    private val onItemClick: (Notification) -> Unit
) : BasePagingAdapter<Notification, ItemNotificationBinding, NotificationAdapter.NotificationViewHolder> (
    ItemNotificationBinding::inflate,
    NotificationDiffCallback()
) {

    inner class NotificationViewHolder(
        binding: ItemNotificationBinding
    ) : BaseViewHolder<Notification, ItemNotificationBinding>(binding) {
        override fun bind(item: Notification) = with(binding) {
            notificationTitle.text = item.title
            notificationDate.text = item.date
            notificationMessage.text = item.message
            root.setOnClickListener{
                onItemClick(item)
            }
        }
    }

    override fun createViewHolder(binding: ItemNotificationBinding): NotificationViewHolder {
        return NotificationViewHolder(binding)
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }
    }
}