package com.dat.bookstore_app.presentation.common.adapter

import android.widget.BaseAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.databinding.ItemSearchHistoryBinding
import com.dat.bookstore_app.presentation.common.base.BaseListAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder

class SearchHistoryAdapter(
    private val onItemClick: (String) -> Unit
): BaseListAdapter<String, ItemSearchHistoryBinding, SearchHistoryAdapter.SearchHistoryViewHolder>(
    ItemSearchHistoryBinding::inflate,
    SearchHistoryDiffCallback()
){
    inner class SearchHistoryViewHolder(
        itemSearchHistoryBinding: ItemSearchHistoryBinding
    ) : BaseViewHolder<String, ItemSearchHistoryBinding>(binding = itemSearchHistoryBinding) {
        override fun bind(item: String) = with(binding) {
            tvSearchHistory.text = item
            root.setOnClickListener {
                onItemClick(item)
            }
        }

    }

    class SearchHistoryDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    override fun createViewHolder(binding: ItemSearchHistoryBinding): SearchHistoryViewHolder {
        return SearchHistoryViewHolder(binding)
    }


}