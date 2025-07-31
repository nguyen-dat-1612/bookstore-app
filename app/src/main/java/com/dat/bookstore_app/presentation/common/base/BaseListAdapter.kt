package com.dat.bookstore_app.presentation.common.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

abstract class BaseListAdapter<T: Any, VB: ViewBinding, VH : BaseViewHolder<T, VB>>(
    private val inflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback) {

    protected abstract fun createViewHolder(binding: VB): VH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = inflater(LayoutInflater.from(parent.context), parent, false)
        return createViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

}
