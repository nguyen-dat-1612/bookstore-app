package com.dat.bookstore_app.presentation.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dat.bookstore_app.databinding.ItemProvinceBinding
import com.dat.bookstore_app.domain.models.Commune

class CommuneAdapter(
    private val onItemClick: (Commune) -> Unit
) : ListAdapter<Commune, CommuneAdapter.CommuneViewHolder>(CommuneDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommuneViewHolder {
        val binding = ItemProvinceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CommuneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommuneViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommuneViewHolder(
        private val binding: ItemProvinceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Commune) {
            binding.tvProvinceName.text = item.name
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    class CommuneDiffCallback : DiffUtil.ItemCallback<Commune>() {
        override fun areItemsTheSame(oldItem: Commune, newItem: Commune): Boolean {
            return oldItem.idCommune == newItem.idCommune
        }

        override fun areContentsTheSame(oldItem: Commune, newItem: Commune): Boolean {
            return oldItem == newItem
        }
    }
}
