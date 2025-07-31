package com.dat.bookstore_app.presentation.common.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.dat.bookstore_app.R
import com.dat.bookstore_app.databinding.ItemOrderStepBinding
import com.dat.bookstore_app.domain.enums.OrderStatus
import com.dat.bookstore_app.presentation.common.base.BaseListAdapter
import com.dat.bookstore_app.presentation.common.base.BaseViewHolder
import com.dat.bookstore_app.presentation.features.purchase_history.OrderStepUI
import com.dat.bookstore_app.utils.helpers.DateHelper.formatOrderDate
import androidx.core.widget.ImageViewCompat
class StepAdapter(
): BaseListAdapter<OrderStepUI, ItemOrderStepBinding, StepAdapter.StepViewHolder>(
    ItemOrderStepBinding::inflate,
    StepDiffCallback()
) {

    inner class StepViewHolder(
        binding: ItemOrderStepBinding
    ) : BaseViewHolder<OrderStepUI, ItemOrderStepBinding>(binding) {

        override fun bind(item: OrderStepUI) = with(binding) {
            val isCanceledFlow = currentList.any { it.isCanceled }

            // Label hiển thị theo step ảo
            tvStepTitle.text = when (item.status) {
                OrderStatus.ALL        -> "Đơn hàng mới"
                OrderStatus.CONFIRMED  -> "Đang xử lý"
                OrderStatus.SHIPPING   -> "Đang giao hàng"
                OrderStatus.DELIVERED  -> "Đã nhận hàng"
                OrderStatus.CANCELLED  -> "Đã huỷ"
                else                   -> item.status.title
            }

            ivStatus.apply {
                visibility = if (item.isCanceled || item.isCompleted) View.VISIBLE else View.GONE
                setImageResource(
                    when {
                        isCanceledFlow && item.status != OrderStatus.DELIVERED && item.status != OrderStatus.SHIPPING -> R.drawable.ic_step_cancel
                        item.isCanceled -> R.drawable.ic_step_cancel
                        else -> R.drawable.ic_step_done
                    }
                )
            }
            // Đổi màu nền
            val tintColor = if (isCanceledFlow && item.status != OrderStatus.DELIVERED && item.status != OrderStatus.SHIPPING) {
                ContextCompat.getColor(root.context, R.color.red_light)
            } else {
                ContextCompat.getColor(root.context, R.color.green_accent) // hoặc màu mặc định khi hoàn thành
            }

            ImageViewCompat.setImageTintList(ivStatus, ColorStateList.valueOf(tintColor))


            // Icon theo từng bước
            ivStepIcon.setImageResource(
                when (item.status) {
                    OrderStatus.ALL        -> R.drawable.ic_order_new     // icon cho đơn hàng mới
                    OrderStatus.CONFIRMED  -> R.drawable.ic_processing    // icon cho đang xử lý (gộp)
                    OrderStatus.SHIPPING   -> R.drawable.ic_box
                    OrderStatus.DELIVERED  -> R.drawable.ic_complete
                    OrderStatus.CANCELLED  -> R.drawable.ic_step_cancel
                    else                   -> R.drawable.ic_unknown
                }
            )

            // Hiển thị thời gian cập nhật
            tvUpdatedAt.apply {
                when {
                    item.status == OrderStatus.ALL && item.createdAt != null -> {
                        visibility = View.VISIBLE
                        text = "Ngày đặt: ${formatOrderDate(item.createdAt)}"
                    }

                    item.isCurrent && item.updatedAt != null -> {
                        visibility = View.VISIBLE
                        text = "Cập nhật: ${formatOrderDate(item.updatedAt)}"
                    }

                    else -> visibility = View.GONE
                }
            }

            // Dot
            layoutDots.visibility = if (bindingAdapterPosition == currentList.lastIndex) View.GONE else View.VISIBLE
        }
    }

    override fun createViewHolder(binding: ItemOrderStepBinding): StepViewHolder {
        return StepViewHolder(binding)
    }

    class StepDiffCallback : DiffUtil.ItemCallback<OrderStepUI>(){
        override fun areItemsTheSame(oldItem: OrderStepUI, newItem: OrderStepUI): Boolean {
            return oldItem.status == newItem.status
        }

        override fun areContentsTheSame(oldItem: OrderStepUI, newItem: OrderStepUI): Boolean {
            return oldItem == newItem
        }

    }
}