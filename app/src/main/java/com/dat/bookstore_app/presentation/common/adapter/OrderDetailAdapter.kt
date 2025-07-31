package com.dat.bookstore_app.presentation.common.adapter

import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.databinding.ItemOrderDetailBinding
import com.dat.bookstore_app.domain.models.OrderItem
import com.dat.bookstore_app.presentation.common.base.BaseListAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder
import com.dat.bookstore_app.utils.extension.loadUrl
import com.dat.bookstore_app.utils.extension.setPriceWithStrikethroughFirst

class OrderDetailAdapter(
    private val onItemClicked: (OrderItem) -> Unit
) : BaseListAdapter<OrderItem, ItemOrderDetailBinding, OrderDetailAdapter.OrderDetailViewHolder>(
    ItemOrderDetailBinding::inflate,
    OrderDetailDiffCallback()
) {

    inner class OrderDetailViewHolder(
        binding: ItemOrderDetailBinding
    ) : BaseViewHolder<OrderItem, ItemOrderDetailBinding>(binding) {
        override fun bind(item: OrderItem) = with(binding) {
            ivThumbnail.loadUrl(item.book.thumbnail)
            txName.text = item.book.title
            txPrice.setPriceWithStrikethroughFirst(item.book.price.toInt(), item.book.discount)
            txQuantity.text = "x${item.quantity}"
        }
    }

    override fun createViewHolder(binding: ItemOrderDetailBinding): OrderDetailViewHolder {
        return OrderDetailViewHolder(binding)
    }

    class OrderDetailDiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
            return oldItem == newItem
        }

    }


}