package com.dat.bookstore_app.presentation.common.adapter

import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.databinding.ItemProvinceBinding
import com.dat.bookstore_app.domain.models.Province
import com.dat.bookstore_app.presentation.common.base.BaseListAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder

class ProvinceAdapter(
    provinces: List<Province>,
    private val onClick: (Province) -> Unit
) : BaseListAdapter<Province, ItemProvinceBinding, ProvinceAdapter.ProvinceViewHolder>(
    inflater = { inflater, parent, attachToParent ->
        ItemProvinceBinding.inflate(inflater, parent, attachToParent)
    },
    diffCallback = object : DiffUtil.ItemCallback<Province>() {
        override fun areItemsTheSame(oldItem: Province, newItem: Province) =
            oldItem.idProvince == newItem.idProvince

        override fun areContentsTheSame(oldItem: Province, newItem: Province) =
            oldItem == newItem
    }
) {
    init {
        submitList(provinces)
    }
    class ProvinceViewHolder(
        binding: ItemProvinceBinding,
        private val onClick: (Province) -> Unit
    ) : BaseViewHolder<Province, ItemProvinceBinding>(binding) {
        override fun bind(item: Province) {
            binding.tvProvinceName.text = item.name
            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun createViewHolder(binding: ItemProvinceBinding) =
        ProvinceViewHolder(binding, onClick)
}
