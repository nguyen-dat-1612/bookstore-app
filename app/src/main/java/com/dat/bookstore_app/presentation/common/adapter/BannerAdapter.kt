package com.dat.bookstore_app.presentation.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dat.bookstore_app.databinding.ItemLayoutBannerBinding
import com.dat.bookstore_app.domain.models.Banner
import com.dat.bookstore_app.utils.extension.loadUrl
import com.dat.bookstore_app.utils.extension.loadUrlFull

class BannerAdapter(
    private var items: List<Banner> = emptyList(),
    private val onItemClick: ((Banner) -> Unit)? = null
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(val binding: ItemLayoutBannerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Banner) = with(binding) {
            image.loadUrlFull(item.imageUrl)
            root.setOnClickListener { onItemClick?.invoke(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding = ItemLayoutBannerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BannerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<Banner>) {
        items = newItems              // KHÔNG mutate, chỉ gán lại
        notifyDataSetChanged()
    }
}
