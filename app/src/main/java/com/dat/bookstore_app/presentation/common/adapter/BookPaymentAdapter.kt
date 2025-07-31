package com.dat.bookstore_app.presentation.common.adapter

import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.databinding.ItemBookPaymentBinding
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.presentation.common.base.BaseListAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder
import com.dat.bookstore_app.utils.extension.loadUrl
import com.dat.bookstore_app.utils.extension.setDiscountedPricePayment

class BookPaymentAdapter : BaseListAdapter<Cart, ItemBookPaymentBinding, BookPaymentAdapter.BookPaymentViewHolder>(
    ItemBookPaymentBinding::inflate,
    BookDiffCallback()
) {
    inner class BookPaymentViewHolder(
        binding: ItemBookPaymentBinding
    ) : BaseViewHolder<Cart, ItemBookPaymentBinding>(binding) {
        override fun bind(item: Cart) = with(binding) {
            txName.text = item.book.title
            txPrice.setDiscountedPricePayment(item.book.price.toInt(), item.book.discount)
            txQuantity.text = "Số lượng ${item.quantity}"
            ivThumbnail.loadUrl(item.book.thumbnail)
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.book.id == newItem.book.id
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }
    }

    override fun createViewHolder(binding: ItemBookPaymentBinding): BookPaymentViewHolder {
        return BookPaymentViewHolder(binding)
    }
}