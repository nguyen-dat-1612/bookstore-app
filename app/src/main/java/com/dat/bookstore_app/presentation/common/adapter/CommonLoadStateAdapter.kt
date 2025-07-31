package com.dat.bookstore_app.presentation.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.dat.bookstore_app.R

class CommonLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<CommonLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: CommonLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): CommonLoadStateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_load_state, parent, false)
        return CommonLoadStateViewHolder(view, retry)
    }
}
