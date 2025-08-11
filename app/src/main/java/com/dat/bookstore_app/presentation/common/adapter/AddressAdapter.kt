package com.dat.bookstore_app.presentation.common.adapter

import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.databinding.ItemAddressPaymentBinding
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.presentation.common.base.BaseListAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder

class AddressAdapter(
    private val onAddressSelected: (Address) -> Unit
) : BaseListAdapter<Address, ItemAddressPaymentBinding, AddressAdapter.AddressViewHolder>(
    inflater = ItemAddressPaymentBinding::inflate,
    diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Address, newItem: Address) =
            oldItem == newItem
    }
) {

    // Giữ id của item đang được chọn
    private var selectedId: Long? = null

    inner class AddressViewHolder(
        binding: ItemAddressPaymentBinding
    ) : BaseViewHolder<Address, ItemAddressPaymentBinding>(binding) {

        override fun bind(item: Address) {
            binding.textNamePhone.text = "${item.fullName} - ${item.phoneNumber}"

            // Gộp địa chỉ từ các trường
            val fullAddress = listOfNotNull(
                item.addressDetail,
                item.ward,
                item.province
            ).joinToString(", ")
            binding.textAddress.text = fullAddress

            // Set trạng thái checkbox
            binding.checkBoxSelect.isChecked = (item.id == selectedId)

            // Click vào checkbox hoặc root view đều chọn
            binding.root.setOnClickListener { updateSelection(item) }
            binding.checkBoxSelect.setOnClickListener { updateSelection(item) }
        }
    }

    override fun createViewHolder(binding: ItemAddressPaymentBinding) = AddressViewHolder(binding)

    private fun updateSelection(item: Address) {
        if (selectedId != item.id) {
            val oldSelectedId = selectedId
            selectedId = item.id

            // Chỉ update item cũ và mới để mượt hơn
            val oldIndex = currentList.indexOfFirst { it.id == oldSelectedId }
            val newIndex = currentList.indexOfFirst { it.id == selectedId }

            if (oldIndex != -1) notifyItemChanged(oldIndex)
            if (newIndex != -1) notifyItemChanged(newIndex)

            onAddressSelected(item)
        }
    }
    override fun submitList(list: List<Address>?) {
        super.submitList(list)
        if (selectedId == null && !list.isNullOrEmpty()) {
            selectedId = list[0].id
            notifyItemChanged(0)
            onAddressSelected(list[0])
        }
    }
}