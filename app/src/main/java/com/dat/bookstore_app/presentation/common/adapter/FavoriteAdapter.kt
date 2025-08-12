package com.dat.bookstore_app.presentation.common.adapter

import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.databinding.ItemFavoriteBinding
import com.dat.bookstore_app.domain.models.Book
import com.dat.bookstore_app.domain.models.Favorite
import com.dat.bookstore_app.presentation.common.base.BasePagingAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder
import com.dat.bookstore_app.utils.extension.loadUrl
import com.dat.bookstore_app.utils.helpers.CurrencyUtils

class FavoriteAdapter(
    private val onClick: (Favorite) -> Unit,
    private val onDelete: (Favorite) -> Unit,
    private val onBuyNow: (Favorite) -> Unit
) : BasePagingAdapter<Favorite,ItemFavoriteBinding, FavoriteAdapter.FavoriteViewHolder> (
    ItemFavoriteBinding::inflate,
    FavoriteDiffCallback()
) {
    inner class FavoriteViewHolder(binding: ItemFavoriteBinding) : BaseViewHolder<Favorite, ItemFavoriteBinding>(binding) {
        override fun bind(item: Favorite) {
            binding.apply {
                with(item.book) {
                    txName.text = title
                    val discountAmount = price - price * discount * 100
                    txPrice.text = CurrencyUtils.formatVND(discountAmount)
                    ivThumbnail.loadUrl(thumbnail)
                }
                btnDelete.setOnClickListener {
                    onDelete(item)
                }
                btnBuyNow.setOnClickListener {
                    onBuyNow(item)
                }
                root.setOnClickListener {
                    onClick(item)
                }
            }
        }
    }

    override fun createViewHolder(binding: ItemFavoriteBinding): FavoriteViewHolder {
        return FavoriteViewHolder(binding)
    }

    class FavoriteDiffCallback : DiffUtil.ItemCallback<Favorite>() {
        override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
            return oldItem == newItem
        }
    }
}