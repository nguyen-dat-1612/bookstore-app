package com.dat.bookstore_app.presentation.common.adapter

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.ItemCartBinding
import com.dat.bookstore_app.domain.models.Cart
import com.dat.bookstore_app.presentation.common.base.BaseListAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder
import com.dat.bookstore_app.utils.extension.loadUrl
import com.dat.bookstore_app.utils.extension.setDiscountedPrice

class CartAdapter(
    private val onUpdateClick: (Cart, quantity: Int) -> Unit,
    private val onDeleteClick: (Cart) -> Unit,
    private val onCheckedChange: (Cart, Boolean) -> Unit,
    private val onItemClicked: (Cart) -> Unit,
) : BaseListAdapter<Cart, ItemCartBinding, CartAdapter.CartViewHolder> (
    ItemCartBinding::inflate,
    CartDiffCallback()
){
    inner class CartViewHolder(
        binding: ItemCartBinding
    ) : BaseViewHolder<Cart, ItemCartBinding>(binding) {
        override fun bind(item: Cart) = with(binding){
            txPrice.setDiscountedPrice(item.book.price.toInt(), item.book.discount);
            txName.text = item.book.title
            txQuantity.text = item.quantity.toString()
            btnPlus.setOnClickListener { onUpdateClick(item, item.quantity + 1) }
            btnMinus.setOnClickListener { onUpdateClick(item, item.quantity - 1)}
            btnDelete.setOnClickListener { onDeleteClick(item) }
            ivThumbnail.loadUrl(item.book.thumbnail)

            // Checkbox binding
            imgCheckbox.setImageResource(
                if (item.isSelected) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked
            )
            imgCheckbox.setOnClickListener {
                onCheckedChange(item, !item.isSelected)
            }
            root.setOnClickListener {
                onItemClicked(item)
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.book.id == newItem.book.id
        }
        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Cart, newItem: Cart): Any? {
            val diffBundle = Bundle()

            if (oldItem.quantity != newItem.quantity) {
                diffBundle.putInt("KEY_QUANTITY", newItem.quantity)
            }

            if (oldItem.isSelected != newItem.isSelected) {
                diffBundle.putInt("KEY_IS_SELECTED", if (newItem.isSelected) 1 else 0)
            }
            // Có thể add thêm các key khác nếu cần

            return if (diffBundle.size() == 0) null else diffBundle
        }
    }

    override fun createViewHolder(binding: ItemCartBinding): CartViewHolder {
        return CartViewHolder(binding)
    }


}