package com.dat.bookstore_app.presentation.common.adapter

import android.content.res.Resources
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.databinding.ItemBookBinding
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.presentation.common.base.BaseListAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder
import coil3.load
import coil3.request.crossfade
import com.dat.bookstore_app.R
import android.graphics.Paint
import com.dat.bookstore_app.utils.helpers.CurrencyUtils

class BookAdapter(
    private val isHorizontalLayout: Boolean,
    private val onItemClick: (Book) -> Unit
) : BaseListAdapter<Book, ItemBookBinding, BookAdapter.ProductViewHolder>(
    ItemBookBinding::inflate,
    ProductDiffCallback()
) {

    class ProductViewHolder(
        binding: ItemBookBinding
    ) : BaseViewHolder<Book, ItemBookBinding>(binding) {

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

                imageProduct.load(item.thumbnail) {
                    crossfade(true)
//                placeholder(R.drawable.ic_placeholder)
//                error(R.drawable.ic_error_image)
                }
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }

    override fun createViewHolder(binding: ItemBookBinding): ProductViewHolder {
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val layoutParams = holder.itemView.layoutParams
        layoutParams?.width = if (isHorizontalLayout) {
            dpToPx(180)
        } else {
            ViewGroup.LayoutParams.MATCH_PARENT
        }
        holder.itemView.layoutParams = layoutParams

        // Set click listener
        val item = getItem(position)
        (holder.viewBinding().root).setOnClickListener {
            onItemClick(item)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }
}
