package com.dat.bookstore_app.presentation.common.adapter

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.databinding.ItemAddressProfileBinding
import com.dat.bookstore_app.domain.models.Address
import com.dat.bookstore_app.presentation.common.base.BaseListAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder

class AddressProfileAdapter(
    private val onItemClick: ((Address) -> Unit)? = null
) : BaseListAdapter<Address, ItemAddressProfileBinding, AddressProfileAdapter.AddressViewHolder>(
    inflater = ItemAddressProfileBinding::inflate,
    diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun createViewHolder(binding: ItemAddressProfileBinding): AddressViewHolder {
        return AddressViewHolder(binding, onItemClick)
    }

    inner class AddressViewHolder(
        binding: ItemAddressProfileBinding,
        private val onItemClick: ((Address) -> Unit)?
    ) : BaseViewHolder<Address, ItemAddressProfileBinding>(binding) {

        override fun bind(item: Address) {
            // Tên + số điện thoại (tên in đậm)
            val namePhone = "${item.fullName ?: ""} | ${item.phoneNumber ?: ""}"
            val spannable = SpannableString(namePhone)
            item.fullName?.let {
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    it.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            binding.tvNamePhone.text = spannable

            // Địa chỉ
            binding.tvAddressLine1.text = item.addressDetail ?: ""
            binding.tvAddressLine2.text = "${item.ward ?: ""}, ${item.province ?: ""}"
            binding.tvAddressLine3.text = "VN" // hoặc lấy từ dữ liệu nếu có

            binding.btnTag.text = if (item.isDefault) {
                "Địa chỉ thanh toán mặc định"
            } else {
                "Địa chỉ khác"
            }
            binding.btnTag.visibility = View.VISIBLE


            // Ẩn divider nếu là item cuối
            val isLastItem = bindingAdapterPosition == itemCount - 1
            binding.divider.visibility = if (isLastItem) View.GONE else View.VISIBLE

            // Click item
            binding.root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }
}
