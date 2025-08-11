package com.dat.bookstore_app.presentation.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.ItemAddressTypeBinding
import com.dat.bookstore_app.domain.enums.AddressType

class AddressTypeAdapter(
    private val items: List<AddressType>,
    private val onItemSelected: (AddressType) -> Unit
) : RecyclerView.Adapter<AddressTypeAdapter.AddressTypeViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressTypeViewHolder {
        val binding = ItemAddressTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressTypeViewHolder, position: Int) {
        holder.bind(items[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = items.size

    inner class AddressTypeViewHolder(private val binding: ItemAddressTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(type: AddressType, isSelected: Boolean) {
            binding.tvCategoryName.text = type.displayName

            // Cập nhật UI khi chọn
            if (isSelected) {
                binding.container.background = ContextCompat.getDrawable(binding.root.context, R.drawable.bg_category_selected)
                binding.imgTick.visibility = View.VISIBLE
            } else {
                binding.container.background = ContextCompat.getDrawable(binding.root.context, R.drawable.bg_category_unselected)
                binding.imgTick.visibility = View.GONE
            }

            binding.container.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemSelected(type)
            }
        }
    }
}
