package com.dat.bookstore_app.presentation.common.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<T, VB : ViewBinding>(
    protected val binding: VB
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: T)

    fun viewBinding(): VB = binding

}