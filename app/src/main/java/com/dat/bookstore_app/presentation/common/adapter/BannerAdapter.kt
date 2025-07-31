package com.dat.bookstore_app.presentation.common.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.dat.bookstore_app.domain.models.Banner
import com.dat.bookstore_app.utils.extension.loadUrl

class BannerAdapter(private val bannerList: List<Banner>) :
    RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    inner class BannerViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.FIT_XY // hoặc CENTER_CROP nếu muốn crop vừa khung
            adjustViewBounds = false
        }
        return BannerViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        val banner = bannerList[position]
        holder.imageView.loadUrl(banner.imageUrl)
    }

    override fun getItemCount(): Int = bannerList.size
}
