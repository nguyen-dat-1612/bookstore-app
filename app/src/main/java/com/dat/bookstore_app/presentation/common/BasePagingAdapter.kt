package com.plus.baseandroidapp.presentation.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding

abstract class BasePagingAdapter<T : Any, VB : ViewBinding, VH : BaseViewHolder<T, VB>>(
    private val inflater: (LayoutInflater, ViewGroup, Boolean) -> VB,
    diffCallback: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, VH>(diffCallback) {

    protected abstract fun createViewHolder(binding: VB): VH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = inflater(LayoutInflater.from(parent.context), parent, false)
        return createViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }
}

/*
Use:
class ProductAdapter : BasePagingAdapter<Product, ItemProductBinding, ProductViewHolder>(
    { inflater, parent, attach -> ItemProductBinding.inflate(inflater, parent, attach) },
    ProductDiffCallback()
) {
    override fun createViewHolder(binding: ItemProductBinding): ProductViewHolder {
        return ProductViewHolder(binding)
    }
}
lifecycleScope.launch {
    viewModel.productPagingFlow
        .collectLatest { pagingData ->
            adapter.submitData(pagingData)
        }
}
 */