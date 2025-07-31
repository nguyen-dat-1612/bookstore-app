package com.dat.bookstore_app.presentation.common.adapter

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.dat.bookstore_app.R

class CommonLoadStateViewHolder(
    itemView: View,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val progressBar: View = itemView.findViewById(R.id.progressBar)
    private val retryButton: View = itemView.findViewById(R.id.retryButton)
    private val errorMsg: TextView = itemView.findViewById(R.id.errorMsg)

    init {
        retryButton.setOnClickListener { retry() }
    }

    fun bind(loadState: LoadState) {
        progressBar.isVisible = loadState is LoadState.Loading
        retryButton.isVisible = loadState is LoadState.Error
        errorMsg.isVisible = loadState is LoadState.Error

        if (loadState is LoadState.Error) {
            errorMsg.text = loadState.error.localizedMessage ?: "Lỗi không xác định"
        }
    }
}
