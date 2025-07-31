package com.dat.bookstore_app.presentation.common.adapter

import android.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import coil3.load
import coil3.request.crossfade
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.ItemBookBinding
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.presentation.common.base.BasePagingAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder
import com.dat.bookstore_app.utils.extension.loadUrl
import com.dat.bookstore_app.utils.helpers.CurrencyUtils

class SearchBookAdapter(
    private val onBookClick: (Book) -> Unit
) : BasePagingAdapter<Book, ItemBookBinding, SearchBookAdapter.SearchBookViewHolder> (
    ItemBookBinding::inflate,
    SearchBookDiffCallback()
) {
    inner class SearchBookViewHolder(binding: ItemBookBinding) : BaseViewHolder <Book, ItemBookBinding>(binding) {
        override fun bind(item: Book) {
            with(binding) {
                tvNameBook.text = item.title

                val finalPrice = item.price * (100 - item.discount) / 100
                tvPriceBook.text = CurrencyUtils.formatVND(finalPrice)
                discountPercent.text = "-${item.discount}%"

                originalPrice.text = CurrencyUtils.formatVND(item.price)
                originalPrice.paintFlags = originalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                soldQuantity.text = "Đã bán ${item.sold}"

                stockStatus.apply {
                    text = if (item.quantity > 0) "Còn hàng" else "Hết hàng"
                    setTextColor(
                        ContextCompat.getColor(
                            root.context,
                            if (item.quantity > 0) R.color.green_accent else R.color.red_light
                        )
                    )
                }

                imageProduct.loadUrl(item.thumbnail)

                root.setOnClickListener {
                    onBookClick(item)
                }
            }
        }
    }

    override fun createViewHolder(binding: ItemBookBinding): SearchBookViewHolder {
        return SearchBookViewHolder(binding)
    }

    class SearchBookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }


}